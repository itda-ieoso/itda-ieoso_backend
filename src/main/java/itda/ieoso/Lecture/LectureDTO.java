package itda.ieoso.Lecture;

import itda.ieoso.Assignment.Assignment;
import itda.ieoso.Assignment.AssignmentDTO;
import itda.ieoso.ContentOrder.ContentOrder;
import itda.ieoso.Course.Course;
import itda.ieoso.Material.Material;
import itda.ieoso.Material.MaterialDto;
import itda.ieoso.MaterialHistory.MaterialHistoryDto;
import itda.ieoso.Submission.SubmissionDTO;
import itda.ieoso.Video.Video;
import itda.ieoso.Video.VideoDto;
import itda.ieoso.VideoHistory.VideoHistory;
import itda.ieoso.VideoHistory.VideoHistoryDto;
import itda.ieoso.VideoHistory.VideoHistoryStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        public static CurriculumResponse of(Lecture lecture, List<ContentOrder> contentOrders) {
            // contentOrder 를 type별로 매핑
            Map<Long, ContentOrder> videoOrders = new HashMap<>();
            Map<Long, ContentOrder> materialOrders = new HashMap<>();
            Map<Long, ContentOrder> assignmentOrders = new HashMap<>();

            for (ContentOrder contentOrder : contentOrders) {
                switch (contentOrder.getContentType()) {
                    case "video" -> videoOrders.put(contentOrder.getContentId(), contentOrder);
                    case "material" -> materialOrders.put(contentOrder.getContentId(), contentOrder);
                    case "assignment" -> assignmentOrders.put(contentOrder.getContentId(), contentOrder);
                }
            }

            List<VideoDto.Response> videoDtos = lecture.getVideos().stream()
                    .map(video -> {
                        ContentOrder order = videoOrders.get(video.getVideoId());
                        return VideoDto.Response.of(video, order);
                    })
                    .collect(Collectors.toList());

            List<MaterialDto.Response> materialDtos = lecture.getMaterials().stream()
                    .map(material -> {
                        ContentOrder order = materialOrders.get(material.getMaterialId());
                        return MaterialDto.Response.of(material, order);
                    })
                    .collect(Collectors.toList());

            List<AssignmentDTO.Response> assignmentDtos = lecture.getAssignments().stream()
                    .map(assignment -> {
                        ContentOrder order = assignmentOrders.get(assignment.getAssignmentId());
                        return AssignmentDTO.Response.of(assignment, order);
                    })
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

    public record TodayToDoListResponse(
            Long courseId,
            String courseTitle,
            List<VideoHistoryDto.Response> videoHistoryDto,
            List<MaterialHistoryDto.ToDoListResponse> materialHistoryDto,
            List<SubmissionDTO.ToDoListResponse> submissions
    ) {
        public static LectureDTO.TodayToDoListResponse of(Course course,
                                                          List<VideoHistoryDto.Response> videoHistoryDto,
                                                          List<MaterialHistoryDto.ToDoListResponse> materialHistoryDto,
                                                          List<SubmissionDTO.ToDoListResponse> submissions) {

            return new LectureDTO.TodayToDoListResponse(
                    course.getCourseId(),
                    course.getCourseTitle(),
                    videoHistoryDto,
                    materialHistoryDto,
                    submissions
            );

        }
    }



}

