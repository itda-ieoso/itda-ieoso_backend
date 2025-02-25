package itda.ieoso.Lecture;

import itda.ieoso.Assignment.AssignmentDTO;
import itda.ieoso.ContentOrder.ContentOrder;
import itda.ieoso.Course.Course;
import itda.ieoso.Material.MaterialDto;
import itda.ieoso.MaterialHistory.MaterialHistoryDto;
import itda.ieoso.Submission.SubmissionDTO;
import itda.ieoso.Video.VideoDto;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
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
            //String instructorName,
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
                    //lecture.getCourse().getInstructorName(),
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

    public record CurriculumResponseWithCourseCreater(
            Long courseId,
            Long creatorId,
            String instructorName,
            List<CurriculumResponse> curriculumResponses

    ) {
        public static CurriculumResponseWithCourseCreater of(Course course, List<CurriculumResponse> curriculumResponses) {
            return new LectureDTO.CurriculumResponseWithCourseCreater(
                    course.getCourseId(),
                    course.getUser().getUserId(),
                    course.getInstructorName(),
                    curriculumResponses
            );
        }
    }

    public record HistoryResponse(
            List<MaterialHistoryDto.Response> materials,
            List<SubmissionDTO.Response> submissions
    ) {}

    public record ToDoResponse(
            Long courseId,
            Long creatorId,
            String courseTitle,
            List<VideoDto.ToDoResponse> videoDtos,
            List<MaterialDto.ToDoResponse> materialDtos,
            List<AssignmentDTO.ToDoResponse> assignmentDtos
    ) {
        public static ToDoResponse of(Course course,
                                      List<VideoDto.ToDoResponse> videoDtos,
                                      List<MaterialDto.ToDoResponse> materialDtos,
                                      List<AssignmentDTO.ToDoResponse> assignmentDtos) {
            return new ToDoResponse(
                    course.getCourseId(),
                    course.getUser().getUserId(),
                    course.getCourseTitle(),
                    videoDtos,
                    materialDtos,
                    assignmentDtos
            );
        }
    }

}

