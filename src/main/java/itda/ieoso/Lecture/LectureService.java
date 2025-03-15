package itda.ieoso.Lecture;

import itda.ieoso.Assignment.Assignment;
import itda.ieoso.Assignment.AssignmentDTO;
import itda.ieoso.Assignment.AssignmentRepository;
import itda.ieoso.ContentOrder.ContentOrder;
import itda.ieoso.ContentOrder.ContentOrderRepository;
import itda.ieoso.ContentOrder.ContentOrderService;
import itda.ieoso.Course.Course;
import itda.ieoso.Course.CourseRepository;
import itda.ieoso.CourseAttendees.CourseAttendees;
import itda.ieoso.CourseAttendees.CourseAttendeesRepository;
import itda.ieoso.CourseAttendees.CourseAttendeesStatus;
import itda.ieoso.Exception.CustomException;
import itda.ieoso.Exception.ErrorCode;
import itda.ieoso.Material.Material;
import itda.ieoso.Material.MaterialDto;
import itda.ieoso.Material.MaterialRepository;
import itda.ieoso.MaterialHistory.MaterialHistory;
import itda.ieoso.MaterialHistory.MaterialHistoryDto;
import itda.ieoso.MaterialHistory.MaterialHistoryRepository;
import itda.ieoso.Submission.Submission;
import itda.ieoso.Submission.SubmissionDTO;
import itda.ieoso.Submission.SubmissionRepository;
import itda.ieoso.Submission.SubmissionStatus;
import itda.ieoso.User.User;
import itda.ieoso.User.UserRepository;
import itda.ieoso.Video.Video;
import itda.ieoso.Video.VideoDto;
import itda.ieoso.Video.VideoRepository;
import itda.ieoso.VideoHistory.VideoHistory;
import itda.ieoso.VideoHistory.VideoHistoryRepository;
import itda.ieoso.VideoHistory.VideoHistoryStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LectureService {
    private final CourseRepository courseRepository;
    private final LectureRepository lectureRepository;
    private final CourseAttendeesRepository courseAttendeesRepository;
    private final VideoHistoryRepository videoHistoryRepository;
    private final MaterialHistoryRepository materialHistoryRepository;
    private final SubmissionRepository submissionRepository;
    private final UserRepository userRepository;
    private final ContentOrderService contentOrderService;
    private final ContentOrderRepository contentOrderRepository;
    private final VideoRepository videoRepository;
    private final MaterialRepository materialRepository;
    private final AssignmentRepository assignmentRepository;

    // SecurityContext에서 현재 사용자 조회
    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // 현재 로그인한 사용자의 이메일 가져오기
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    // 강의 생성
    @Transactional
    public LectureDTO.Response createLecture(Long courseId, Long userId, LectureDTO.Request request) {

        User authenticatedUser = getAuthenticatedUser();
        // 과정 생성자인지 확인
        if (!isCourseCreator(courseId, authenticatedUser.getUserId())) {
            throw new CustomException(ErrorCode.COURSE_PERMISSION_DENIED);
        }

        // 과정 찾기
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CustomException(ErrorCode.COURSE_NOT_FOUND));

        // Lecture 객체 생성 (빌더 사용)
        Lecture lecture = Lecture.builder()
                .course(course)
                .lectureTitle(request.lectureTitle())
                .lectureDescription(request.lectureDescription())
                .startDate(request.startDate())
                .endDate(request.endDate())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .videos(new ArrayList<>())
                .materials(new ArrayList<>())
                .assignments(new ArrayList<>())
                .build();

        lectureRepository.save(lecture); // 저장 후 반환

//        // contentOrder 생성
//        contentOrderService.createContentOrder(course, "lecture", lecture.getLectureId());

        LectureDTO.Response response = LectureDTO.Response.of(lecture);
        return response;
    }

    // 강의 수정
    @Transactional
    public LectureDTO.Response updateLecture(Long courseId, Long lectureId, Long userId, LectureDTO.Request request) {
        User authenticatedUser = getAuthenticatedUser();
        // 기존 강의 조회
        Lecture lecture = lectureRepository.findByCourse_CourseIdAndLectureId(courseId,lectureId)
                .orElseThrow(() -> new CustomException(ErrorCode.LECTURE_NOT_FOUND));

        // 강의를 속한 과정의 생성자 ID와 요청한 사용자 ID가 일치하는지 확인
        if (!lecture.getCourse().getUser().getUserId().equals(authenticatedUser.getUserId())) {
            throw new CustomException(ErrorCode.COURSE_PERMISSION_DENIED);
        }

        // 기존 객체 수정 (새로 객체를 생성하지 않고 덮어씀)
        if (request.lectureTitle()!=null) lecture.setLectureTitle(request.lectureTitle());
        if (request.lectureDescription()!=null) lecture.setLectureDescription(request.lectureDescription());
        if (request.startDate()!=null) lecture.setStartDate(request.startDate());
        if (request.endDate()!=null) lecture.setEndDate(request.endDate());
        lecture.setUpdatedAt(LocalDateTime.now()); // updatedAt 갱신

        // 데이터베이스에 저장
        lectureRepository.save(lecture);

        // 반환
        LectureDTO.Response response = LectureDTO.Response.of(lecture);

        return response;
    }

    // 강의 삭제
    @Transactional
    public LectureDTO.deleteResponse deleteLecture(Long courseId, Long lectureId, Long userId) {
        User authenticatedUser = getAuthenticatedUser();
        // 강의 찾기
        Lecture lecture = lectureRepository.findByCourse_CourseIdAndLectureId(courseId,lectureId)
                .orElseThrow(() -> new CustomException(ErrorCode.LECTURE_NOT_FOUND));

        // 강의의 과정 생성자인지 확인
        if (!isCourseCreator(lecture.getCourse().getCourseId(), authenticatedUser.getUserId())) {
            throw new CustomException(ErrorCode.COURSE_PERMISSION_DENIED);
        }

        // contentOrder 삭제
        contentOrderRepository.deleteAllByLecture(lecture);

        // 강의 삭제
        lectureRepository.delete(lecture);

        // 반환
        LectureDTO.deleteResponse response = LectureDTO.deleteResponse.builder()
                .lectureId(lectureId)
                .message("lecture 삭제 완료")
                .build();

        return response;
    }

    // 과정 생성자인지 확인
    public boolean isCourseCreator(Long courseId, Long userId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CustomException(ErrorCode.COURSE_NOT_FOUND));
        return course.getUser().getUserId().equals(userId); // Course에 `creatorId` 필드가 있다고 가정
    }

    // 과정 참여자인지 확인
    public boolean isCourseAttendee(Long courseId, Long userId) {
        // CourseAttendees 테이블에서 courseId와 userId로 참여 상태 확인
        return courseAttendeesRepository.existsByCourse_CourseIdAndUser_UserId(courseId, userId);
    }

    // lecture조회(커리큘럼 전체조회)
    @Transactional
    public LectureDTO.CurriculumResponseWithCourseCreater getLectureList(Long courseId, Long userId) {
        User authenticatedUser = getAuthenticatedUser();
        // 과정 찾기(필요?)
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CustomException(ErrorCode.COURSE_NOT_FOUND));

        // courseAttendees 찾기(강의수강생 검증)
        if (!courseAttendeesRepository.existsByCourse_CourseIdAndUser_UserId(courseId,authenticatedUser.getUserId())) {
            throw new CustomException(ErrorCode.COURSEATTENDEES_PERMISSION_DENIED);
        }


        // lecutre 조회
        List<Lecture> lectureList = lectureRepository.findAllByCourse_CourseId(courseId);
        List<LectureDTO.CurriculumResponse> lectrueResponseList = lectureList.stream()
                .map(lecture -> {
                    // 해당 lecture에 대한 contentOrder 정보 조회
                    List<ContentOrder> contentOrders = contentOrderRepository.findOrderedByCourseIdAndLectureId(courseId, lecture.getLectureId());

                    return LectureDTO.CurriculumResponse.of(lecture, contentOrders);
                })
                .collect(Collectors.toList());

        LectureDTO.CurriculumResponseWithCourseCreater response = LectureDTO.CurriculumResponseWithCourseCreater.of(course, lectrueResponseList);

        return response;

//        List<ContentOrder> contentOrders = contentOrderRepository.findOrderedByCourseId(courseId);
//
//        // type(string) , list<ids>로 type별로 contentid분리하기
//        Map<String, List<Long>> contentIdsByType = new HashMap<>();
//        for (ContentOrder contentOrder : contentOrders) {
//            contentIdsByType.computeIfAbsent(contentOrder.getContentType(),
//                    k-> new ArrayList<>()).add(contentOrder.getContentId());
//        }
//
//        // 타입별로 한번에 id조회
//        Map<Long, Object> lectureMap = lectureRepository.findByLectureIdIn(contentIdsByType.getOrDefault("lecture", List.of()))
//                .stream().collect(Collectors.toMap(Lecture::getLectureId, LectureDTO.Response::of));
//
//        Map<Long, Object> videoMap = videoRepository.findByVideoIdIn(contentIdsByType.getOrDefault("video", List.of()))
//                .stream().collect(Collectors.toMap(Video::getVideoId, VideoDto.Response::of));
//
//        Map<Long, Object> materialMap = materialRepository.findByMaterialIdIn(contentIdsByType.getOrDefault("material", List.of()))
//                .stream().collect(Collectors.toMap(Material::getMaterialId, MaterialDto.Response::of));
//
//        Map<Long, Object> assignmentMap = assignmentRepository.findByAssignmentIdIn(contentIdsByType.getOrDefault("assignment", List.of()))
//                .stream().collect(Collectors.toMap(Assignment::getAssignmentId, AssignmentDTO.Response::of));
//
//        List<ContentOrderDto.Response> orderedList = new ArrayList<>();
//        for (ContentOrder order : contentOrders) {
//            Object contentData = switch (order.getContentType()) {
//                case "lecture" -> lectureMap.get(order.getContentId());
//                case "video" -> videoMap.get(order.getContentId());
//                case "material" -> materialMap.get(order.getContentId());
//                case "assignment" -> assignmentMap.get(order.getContentId());
//                default -> null;
//            };
//
//            if (contentData != null) {
//                orderedList.add(new ContentOrderDto.Response(order.getContentOrderId(), order.getContentType(), contentData));
//            }
//        }
//
//        return orderedList;



    }

    // 수강생 히스토리 전체 조회
    @Transactional
    public LectureDTO.HistoryResponse getLectureHistories(Long courseId, Long userId) {
        User authenticatedUser = getAuthenticatedUser();
        // 과정 찾기(필요?)
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CustomException(ErrorCode.COURSE_NOT_FOUND));

        // courseAttendees 찾기(강의수강생 검증)
        CourseAttendees courseAttendees = courseAttendeesRepository.findByCourse_CourseIdAndUser_UserId(courseId,authenticatedUser.getUserId())
                .orElseThrow(()-> new CustomException(ErrorCode.COURSEATTENDEES_PERMISSION_DENIED));


        // videoHistory 조회 -> mvp이후

        // materialHistory 조회
        List<MaterialHistory> materialHistories = materialHistoryRepository.findAllByCourseAndCourseAttendees(course, courseAttendees);
        List<MaterialHistoryDto.Response> materialHistoryDtos = materialHistories.stream()
                .map(MaterialHistoryDto.Response::of)
                .collect(Collectors.toList());

        // submission 조회
        List<Submission> submissions = submissionRepository.findAllByCourseAndCourseAttendees(course, courseAttendees);
        List<SubmissionDTO.Response> submissionDtos = submissions.stream()
                .map(SubmissionDTO.Response::of)
                .collect(Collectors.toList());

        return new LectureDTO.HistoryResponse(materialHistoryDtos, submissionDtos);
    }

    // 오늘 할일 조회
    @Transactional
    public List<LectureDTO.ToDoResponse> getDayTodoList(Long userId, LocalDate date) {
        User authenticatedUser = getAuthenticatedUser();


        // 유저가 속한 강의목록 불러오기
        List<CourseAttendees> courseAttendeesList = courseAttendeesRepository.findByUser_UserId(authenticatedUser.getUserId());
        if (courseAttendeesList.isEmpty()) {
            return Collections.emptyList();
        }

        // course별로 오늘할일 목록 추출
        List<LectureDTO.ToDoResponse> toDoResponses = new ArrayList<>();
        for (CourseAttendees courseAttendee : courseAttendeesList) {

            // 강의 조회
            Course course = courseAttendee.getCourse();

            // video 조회
            List<Video> videos = videoRepository.findByCourseAndDateRange(course, date);
            List<VideoDto.ToDoResponse> videoDtos = videos.stream()
                    .map(video -> {
                        ContentOrder contentorder = contentOrderRepository.findByContentTypeAndContentId("video", video.getVideoId());

                        // 수강자인경우 -> 히스토리 추가
//                        VideoHistoryStatus videoHistroyStatus = null;
//                        if (courseAttendee.getCourseAttendeesStatus()== CourseAttendeesStatus.ACTIVE) {
//                            VideoHistory videoHistory = videoHistoryRepository.findByVideoAndCourseAttendees(video, courseAttendee);
//                            videoHistroyStatus = videoHistory.getVideoHistoryStatus();
//                        }

                        return VideoDto.ToDoResponse.of(video, /*videoHistroyStatus,*/ contentorder);

                    })
                    .collect(Collectors.toList());

            // material 조회
            List<Material> materials = materialRepository.findByCourseAndDateRange(course, date);
            List<MaterialDto.ToDoResponse> materialDtos = materials.stream()
                    .map(material -> {
                        ContentOrder contentOrder = contentOrderRepository.findByContentTypeAndContentId("material", material.getMaterialId());

                        // 수강자인 경우 -> 히스토리 추가
                        Boolean materialHistroyStatus = null;
                        if (courseAttendee.getCourseAttendeesStatus()==CourseAttendeesStatus.ACTIVE) {
                            MaterialHistory materialHistory = materialHistoryRepository.findByMaterialAndCourseAttendees(material, courseAttendee);
                            materialHistroyStatus = materialHistory.isMaterialHistoryStatus();
                        }

                        return MaterialDto.ToDoResponse.of(material, materialHistroyStatus, contentOrder);
                    })
                    .collect(Collectors.toList());

            // assignment 조회
            List<Assignment> assignments = assignmentRepository.findByCourseAndDateRange(course, date);
            List<AssignmentDTO.ToDoResponse> assignmentDtos = assignments.stream()
                    .map(assignment -> {
                        ContentOrder contentOrder = contentOrderRepository.findByContentTypeAndContentId("assignment", assignment.getAssignmentId());

                        // 수강생일 경우 -> 히스토리 추가
                        SubmissionStatus submissionStatus = null;
                        if (courseAttendee.getCourseAttendeesStatus()== CourseAttendeesStatus.ACTIVE) {
                            Submission submission = submissionRepository.findByAssignmentAndCourseAttendees(assignment, courseAttendee);
                            submissionStatus = submission.getSubmissionStatus();
                        }

                        return AssignmentDTO.ToDoResponse.of(assignment, submissionStatus, contentOrder);
                    })
                    .collect(Collectors.toList());


            LectureDTO.ToDoResponse lists = LectureDTO.ToDoResponse.of(course, videoDtos, materialDtos, assignmentDtos);
            toDoResponses.add(lists);
        }

        return toDoResponses;
    }



}
