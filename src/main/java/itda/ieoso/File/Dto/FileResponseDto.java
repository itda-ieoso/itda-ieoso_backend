package itda.ieoso.File.Dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FileResponseDto {
    private Long fileId;  // 파일 ID
    private String fileName;  // 저장된 파일 이름 (예: document.pdf)
    private String fileUrl;  // PreSigned URL (파일 업로드를 위한 URL)
    private String originalFileName;  // 원본 파일 이름
    private String fileSize;  // 파일 크기 (옵션)
    private String fileType;  // MIME 타입 (예: "image/png", "application/pdf")
}
