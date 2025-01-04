package itda.ieoso.File.Service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import itda.ieoso.File.Domain.File;
import itda.ieoso.File.Dto.FileResponseDto;
import itda.ieoso.File.Dto.PresignedUrlDto;
import itda.ieoso.File.Repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;
    private final AmazonS3 amazonS3;

    @Value("${amazon.aws.bucket}")
    private String bucket;

    @Value("${amazon.aws.cloudfront.distribution-domain}")
    private String domain;

    // 파일 삭제
    @Transactional
    public void deleteFile(File file) {
        if (file == null) return;
        amazonS3.deleteObject(new DeleteObjectRequest(bucket, file.getFilePath()));  // S3에서 파일 삭제
        fileRepository.delete(file);  // DB에서 파일 정보 삭제
    }

    // 파일 생성
    @Transactional
    public FileResponseDto createFile(PresignedUrlDto request) {
        if (request.getFileName() == null) return null;

        // 파일 경로 생성
        String filePath = createPath(request.getPrefix(), request.getFileName());

        // File 객체 생성 및 DB 저장
        File file = File.builder()
                .fileName(request.getFileName())  // 저장된 파일 이름
                .filePath(filePath)  // S3에 저장된 파일 경로
                .fileType(request.getFileType())  // 파일 타입 (예: "image/png", "application/pdf")
                .originalFileName(request.getOriginalFileName())  // 원본 파일 이름
                .fileSize(request.getFileSize())  // 파일 크기
                .build();
        fileRepository.save(file);

        // 반환할 URL 생성 (Put 요청을 위한 PreSigned URL)
        return FileResponseDto.builder()
                .fileId(file.getId())
                .fileName(file.getFileName())  // 저장된 파일 이름
                .fileUrl(getPreSignedUrl(filePath, HttpMethod.PUT))  // PreSigned URL
                .originalFileName(file.getOriginalFileName())  // 원본 파일 이름
                .fileSize(file.getFileSize())  // 파일 크기
                .fileType(file.getFileType())  // 파일 타입
                .build();
    }

    // PreSigned URL 생성 함수 (PUT, DELETE 요청용)
    public String getPreSignedUrl(String filePath, HttpMethod httpMethod) {
        GeneratePresignedUrlRequest generatePresignedUrlRequest = getGeneratePresignedUrlRequest(bucket, filePath, httpMethod);
        return amazonS3.generatePresignedUrl(generatePresignedUrlRequest).toString();
    }

    // PreSigned URL 생성 요청을 위한 기본 설정
    private GeneratePresignedUrlRequest getGeneratePresignedUrlRequest(String bucket, String fileName, HttpMethod httpMethod) {
        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucket, fileName)
                .withMethod(httpMethod)
                .withExpiration(getPresignedUrlExpiration());  // URL의 만료 시간 설정
        return generatePresignedUrlRequest;
    }

    // 파일 경로 생성 (prefix와 파일 이름을 결합)
    private String createPath(String prefix, String fileName) {
        return String.format("%s/%s", prefix, fileName);
    }

    // PreSigned URL의 만료 시간 설정 (예: 2분)
    private Date getPresignedUrlExpiration() {
        Date expiration = new Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 2;  // 2분
        expiration.setTime(expTimeMillis);
        return expiration;
    }

    // CDN URL 생성 함수 (파일을 외부에서 접근할 수 있도록 URL을 생성)
    public String getCDNUrl(String prefix, String fileName) {
        if (prefix == null || fileName == null) return null;
        return domain + "/" + prefix + "/" + fileName;
    }
}