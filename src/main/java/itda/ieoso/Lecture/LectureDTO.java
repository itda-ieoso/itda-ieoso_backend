package itda.ieoso.Lecture;

import itda.ieoso.Assignment.Assignment;
import itda.ieoso.Assignment.AssignmentDTO;
import itda.ieoso.Material.Material;
import itda.ieoso.Material.MaterialDto;
import itda.ieoso.MaterialHistory.MaterialHistoryDto;
import itda.ieoso.Submission.SubmissionDTO;
import itda.ieoso.Video.Video;
import itda.ieoso.Video.VideoDto;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class LectureDTO {
    public record Request(
            String lectureTitle,
            String lectureDescription,
            LocalDate startDate,
            LocalDate endDate
    ) {}

    @Builder
    public record Response(
            Long lectureId,
            String lectureTitle,
            String lectureDescription,
            LocalDate startDate,
            LocalDate endDate
    ) {
        public static Response of(Lecture lecture) {
            return new Response(
                    lecture.getLectureId(),
                    lecture.getLectureTitle(),
                    lecture.getLectureDescription(),
                    lecture.getStartDate(),
                    lecture.getEndDate()
                    );
        }
    }

    @Builder
    public record deleteResponse(
            Long lectureId,
            String message
    ) {}

    @Builder
    public record CurriculumResponse(
            Long lectureId,
            String lectureTitle,
            String lectureDescription,
            LocalDate startDate,
            LocalDate endDate,
            List<VideoDto.Response> videos,
            List<MaterialDto.Response> materials,
            List<AssignmentDTO.Response> assignments
    ) {
        public static CurriculumResponse of(Lecture lecture) {
            List<VideoDto.Response> videoDtos = lecture.getVideos().stream()
                    .map(VideoDto.Response::of)
                    .collect(Collectors.toList());

            List<MaterialDto.Response> materialDtos = lecture.getMaterials().stream()
                    .map(MaterialDto.Response::of)
                    .collect(Collectors.toList());

            List<AssignmentDTO.Response> assignmentDtos = lecture.getAssignments().stream()
                    .map(AssignmentDTO.Response::of)
                    .collect(Collectors.toList());

            return new LectureDTO.CurriculumResponse (
                    lecture.getLectureId(),
                    lecture.getLectureTitle(),
                    lecture.getLectureDescription(),
                    lecture.getStartDate(),
                    lecture.getEndDate(),
                    videoDtos,
                    materialDtos,
                    assignmentDtos
            );
        }
    }

    public record HistoryResponse(
            List<MaterialHistoryDto.Response> materials,
            List<SubmissionDTO.Response> submissions
    ) {}



}

