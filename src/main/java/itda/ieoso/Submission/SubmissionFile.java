package itda.ieoso.Submission;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmissionFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long submissionFileId;

    @Column(name = "file_url")
    private String submissionFileUrl;  // 파일 URL

    @Column(name = "original_filename")
    private String submissionOriginalFilename;  // 파일의 원래 이름

    @Column(name = "file_size")
    private String submissionFileSize;  // 파일 크기 (KB, MB, GB)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id")  // 부모 Submission 엔티티와의 관계
    private Submission submission;

    // 파일 생성 메서드
    public static SubmissionFile createFile(String fileUrl, String originalFilename, String fileSize, Submission submission) {
        return SubmissionFile.builder()
                .submissionFileUrl(fileUrl)
                .submissionOriginalFilename(originalFilename)
                .submissionFileSize(fileSize)
                .submission(submission)
                .build();
    }
}

