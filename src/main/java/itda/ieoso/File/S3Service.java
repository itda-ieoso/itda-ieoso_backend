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
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
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
    public String uploadFile(String folder, String filename, File file) {
        String filePath = folder + "/" + filename;

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
    private File convertMultipartFileToFile(MultipartFile file) throws IOException {
        Path tempFile = Files.createTempFile("temp-", file.getOriginalFilename());
        file.transferTo(tempFile);
        return tempFile.toFile();
    }

    // S3 파일 다운로드 URL 생성 (Presigned URL)
    public String generatePresignedUrl(String filePath) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(s3Config.getBucketName())
                .key(filePath)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofHours(1)) // URL 유효 시간 설정
                .getObjectRequest(getObjectRequest)
                .build();

        URL presignedUrl = s3Presigner.presignGetObject(presignRequest).url();
        return presignedUrl.toString();
    }
}


