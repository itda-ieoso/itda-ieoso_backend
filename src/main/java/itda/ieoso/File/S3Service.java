package itda.ieoso.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.File;
import java.net.URL;
import java.time.Duration;

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

        // Debugging print to verify values are correct
        System.out.println("Bucket Name: " + s3Config.getBucketName());
        System.out.println("Region: " + s3Config.getRegion());
        System.out.println("Access Key: " + s3Config.getAccessKey());
        System.out.println("Secret Key: " + s3Config.getSecretKey());

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


