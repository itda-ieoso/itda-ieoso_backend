package itda.ieoso.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class S3Service {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    // S3Config에서 값을 주입받음
    private final S3Config s3Config;

    @Autowired
    public S3Service(S3Config s3Config) {
        this.s3Config = s3Config;  // S3Config 인스턴스 저장

        // S3Config에서 값들을 확인하고 S3Client 초기화
        if (s3Config.getAccessKey() == null || s3Config.getAccessKey().isEmpty()) {
            throw new IllegalArgumentException("Access Key is not set in the configuration.");
        }

        if (s3Config.getSecretKey() == null || s3Config.getSecretKey().isEmpty()) {
            throw new IllegalArgumentException("Secret Key is not set in the configuration.");
        }

        // S3 Client와 Presigner를 설정
        this.s3Client = S3Client.builder()
                .region(Region.of(s3Config.getRegion()))  // 버킷의 리전
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(s3Config.getAccessKey(), s3Config.getSecretKey())))  // 자격 증명
                .build();

        this.s3Presigner = S3Presigner.builder()
                .region(Region.of(s3Config.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(s3Config.getAccessKey(), s3Config.getSecretKey())))
                .build();
    }

    // S3 파일 업로드 (폴더 지정)
    public String uploadFile(String folder, String filename, File file) throws IOException {

        // 파일명 URL 인코딩 (한글 및 특수문자 처리)
        String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8.toString())
                .replaceAll("\\+", "%20"); // "+"를 "%20"으로 변경 (URL-safe한 공백 처리)

        String filePath = folder + "/" + encodedFilename;

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(s3Config.getBucketName())
                .key(filePath)
                .build();

        s3Client.putObject(putObjectRequest, software.amazon.awssdk.core.sync.RequestBody.fromFile(file));

        // S3에 저장된 파일의 URL 반환
        return "https://" + s3Config.getBucketName() + ".s3." + s3Config.getRegion() + ".amazonaws.com/" + filePath;
    }

    // 여러 파일 업로드 메서드
    public List<String> uploadFiles(String folder, MultipartFile[] files) throws IOException {
        List<String> fileUrls = new ArrayList<>();

        // 각 파일을 업로드하고 URL을 리스트에 저장
        for (MultipartFile file : files) {
            // 파일명을 UUID로 변경하여 중복 방지
            String filename = System.currentTimeMillis() + "-" + file.getOriginalFilename();
            File convertedFile = convertMultipartFileToFile(file);
            String fileUrl = uploadFile(folder, filename, convertedFile);
            fileUrls.add(fileUrl);
        }

        return fileUrls;
    }

    // MultipartFile을 File로 변환하는 유틸리티 메서드
    public File convertMultipartFileToFile(MultipartFile file) throws IOException {
        Path tempFile = Files.createTempFile("temp-", file.getOriginalFilename());
        file.transferTo(tempFile);
        return tempFile.toFile();
    }

    // S3 파일 다운로드 URL 생성 (Presigned URL)
    public String generatePresignedUrl(String s3Url) throws IOException {

        // URL에서 "https://"와 "s3.ap-northeast-2.amazonaws.com"을 제거하고, 경로를 남깁니다.
        String withoutS3Prefix = s3Url.replace("https://", "").replace("s3.ap-northeast-2.amazonaws.com/", "").replace("itdaawsbucket.", "");
        String filePath = withoutS3Prefix;
        // 파일 이름을 URL 인코딩
        String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);

        // Content-Disposition 설정: 브라우저가 파일 다운로드하도록 지정
        String contentDisposition = "attachment; filename=\"" + fileName + "\"";
        String contentType = Files.probeContentType(Paths.get(filePath));

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(s3Config.getBucketName())
                .key(filePath)
                .responseContentType(contentType)
                .responseContentDisposition(contentDisposition)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofHours(1)) // URL 유효 시간 설정
                .getObjectRequest(getObjectRequest)
                .build();

        URL presignedUrl = s3Presigner.presignGetObject(presignRequest).url();

        return presignedUrl.toString();
    }

    public void moveFileToDeleteFolder(String fileUrl) {
        // 기존 URL로부터 S3 key 추출
        String bucketUrlPrefix = "https://" + s3Config.getBucketName() + ".s3." + s3Config.getRegion() + ".amazonaws.com/";
        String originalKey = fileUrl.replace(bucketUrlPrefix, ""); // example: submissions/abc.jpg
        String fileName = originalKey.substring(originalKey.lastIndexOf("/") + 1);
        String newKey = "delete/" + fileName;

        // S3 객체 복사
        CopyObjectRequest copyReq = CopyObjectRequest.builder()
                .sourceBucket(s3Config.getBucketName())
                .sourceKey(originalKey)
                .destinationBucket(s3Config.getBucketName())
                .destinationKey(newKey)
                .build();
        s3Client.copyObject(copyReq);

        // 원본 삭제
        DeleteObjectRequest deleteReq = DeleteObjectRequest.builder()
                .bucket(s3Config.getBucketName())
                .key(originalKey)
                .build();
        s3Client.deleteObject(deleteReq);
    }
}


