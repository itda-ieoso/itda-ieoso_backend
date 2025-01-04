package itda.ieoso.File.Dto;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PresignedUrlDto {
    private String prefix;  // 파일을 저장할 S3 경로의 접두사 (예: "user/123")
    private String fileName;  // 파일 이름 (저장된 이름)
    private String originalFileName;  // 원본 파일 이름 (업로드된 이름)
    private String fileType;  // 파일 타입 (예: "image/png", "application/pdf")
    private String fileSize;  // 파일 크기 (옵션)
}