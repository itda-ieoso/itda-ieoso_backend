package itda.ieoso.Lecture;

import itda.ieoso.Assignment.Assignment;
import itda.ieoso.Material.Material;
import itda.ieoso.Video.Video;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class LectureDTO {

    private Long lectureId;  // 강의 ID
    private String lectureTitle;  // 강의 제목
    private String lectureDescription;  // 강의 설명
    private LocalDate startDate;  // 시작 날짜
    private LocalDate endDate;  // 종료 날짜
    private LocalDateTime createdAt;  // 생성 날짜
    private LocalDateTime updatedAt;  // 수정 날짜
    private List<Video> videoList;
    private List<Material> materialList;
    private List<Assignment> assignmentList; // TODO AssignmentDto로 변경?

    // LectureDTO를 생성하는 static 메소드
    public static LectureDTO of(Lecture lecture) {
        return LectureDTO.builder()
                .lectureId(lecture.getLectureId())
                .lectureTitle(lecture.getLectureTitle())
                .lectureDescription(lecture.getLectureDescription())
                .startDate(lecture.getStartDate())
                .endDate(lecture.getEndDate())
                .createdAt(lecture.getCreatedAt())
                .updatedAt(lecture.getUpdatedAt())
                .build();
    }
}

