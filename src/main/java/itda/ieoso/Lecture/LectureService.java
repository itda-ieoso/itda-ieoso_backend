package itda.ieoso.Lecture;

import itda.ieoso.Assignment.Assignment;
import itda.ieoso.Assignment.AssignmentDTO;
import itda.ieoso.Assignment.AssignmentRepository;
import itda.ieoso.ContentOrder.ContentOrder;
import itda.ieoso.ContentOrder.ContentOrderDto;
import itda.ieoso.ContentOrder.ContentOrderRepository;
import itda.ieoso.ContentOrder.ContentOrderService;
import itda.ieoso.Course.Course;
import itda.ieoso.Course.CourseRepository;
import itda.ieoso.CourseAttendees.CourseAttendees;
import itda.ieoso.CourseAttendees.CourseAttendeesRepository;
import itda.ieoso.Lecture.CurriculumResponseDto.AssignmentResponseDto;
import itda.ieoso.Lecture.CurriculumResponseDto.MaterialResponseDto;
import itda.ieoso.Lecture.CurriculumResponseDto.VideoResponseDto;
import itda.ieoso.Material.Material;
import itda.ieoso.Material.MaterialDto;
import itda.ieoso.Material.MaterialRepository;
import itda.ieoso.MaterialHistory.MaterialHistory;
import itda.ieoso.MaterialHistory.MaterialHistoryDto;
import itda.ieoso.MaterialHistory.MaterialHistoryRepository;
import itda.ieoso.Submission.Submission;
import itda.ieoso.Submission.SubmissionDTO;
import itda.ieoso.Submission.SubmissionRepository;
import itda.ieoso.User.User;
import itda.ieoso.User.UserRepository;
import itda.ieoso.Video.Video;
import itda.ieoso.Video.VideoDto;
import itda.ieoso.Video.VideoRepository;
import itda.ieoso.VideoHistory.VideoHistory;
import itda.ieoso.VideoHistory.VideoHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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


    // 강의 생성
    @Transactional
    public LectureDTO.Response createLecture(Long courseId, Long userId, LectureDTO.Request request) {
        // 과정 생성자인지 확인
        if (!isCourseCreator(courseId, userId)) {
            throw new IllegalArgumentException("강의를 생성할 권한이 없습니다.");
        }

        // 과정 찾기
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("해당 과정이 존재하지 않습니다."));

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

        // contentOrder 생성
        contentOrderService.createContentOrder(course, "lecture", lecture.getLectureId());

        LectureDTO.Response response = LectureDTO.Response.of(lecture);
        return response;
    }

    // 강의 수정
    @Transactional
    public LectureDTO.Response updateLecture(Long courseId, Long lectureId, Long userId, LectureDTO.Request request) {
        // 기존 강의 조회
        Lecture lecture = lectureRepository.findByCourse_CourseIdAndLectureId(courseId,lectureId)
                .orElseThrow(() -> new RuntimeException("강의를 찾을 수 없습니다"));

        // 강의를 속한 과정의 생성자 ID와 요청한 사용자 ID가 일치하는지 확인
        if (!lecture.getCourse().getUser().getUserId().equals(userId)) {
            throw new RuntimeException("이 강의를 수정할 권한이 없습니다.");
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
        // 강의 찾기
        Lecture lecture = lectureRepository.findByCourse_CourseIdAndLectureId(courseId,lectureId)
                .orElseThrow(() -> new IllegalArgumentException("해당 강의가 존재하지 않습니다."));

        // 강의의 과정 생성자인지 확인
        if (!isCourseCreator(lecture.getCourse().getCourseId(), userId)) {
            throw new IllegalArgumentException("강의를 삭제할 권한이 없습니다.");
        }

        // contentOrder 삭제
        contentOrderService.deleteContentOrder(lectureId, "lecture");

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
                .orElseThrow(() -> new IllegalArgumentException("해당 과정이 존재하지 않습니다."));
        return course.getUser().getUserId().equals(userId); // Course에 `creatorId` 필드가 있다고 가정
    }

    // 과정 참여자인지 확인
    public boolean isCourseAttendee(Long courseId, Long userId) {
        // CourseAttendees 테이블에서 courseId와 userId로 참여 상태 확인
        return courseAttendeesRepository.existsByCourse_CourseIdAndUser_UserId(courseId, userId);
    }

    // lecture조회(커리큘럼 전체조회)
    @Transactional
    public List<ContentOrderDto.Response> getLectureList(Long courseId, Long userId) {
        // 과정 찾기(필요?)
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("해당 과정이 존재하지 않습니다."));

        // courseAttendees 찾기(강의수강생 검증)
        if (!courseAttendeesRepository.existsByCourse_CourseIdAndUser_UserId(courseId,userId)) {
            throw new IllegalArgumentException("수강생이 아닙니다.");
        }

        List<ContentOrder> contentOrders = contentOrderRepository.findOrderedByCourseId(courseId);

        // type(string) , list<ids>로 type별로 contentid분리하기
        Map<String, List<Long>> contentIdsByType = new HashMap<>();
        for (ContentOrder contentOrder : contentOrders) {
            contentIdsByType.computeIfAbsent(contentOrder.getContentType(),
                    k-> new ArrayList<>()).add(contentOrder.getContentId());
        }

        // 타입별로 한번에 id조회
        Map<Long, Object> lectureMap = lectureRepository.findByLectureIdIn(contentIdsByType.getOrDefault("lecture", List.of()))
                .stream().collect(Collectors.toMap(Lecture::getLectureId, LectureDTO.Response::of));

        Map<Long, Object> videoMap = videoRepository.findByVideoIdIn(contentIdsByType.getOrDefault("video", List.of()))
                .stream().collect(Collectors.toMap(Video::getVideoId, VideoDto.Response::of));

        Map<Long, Object> materialMap = materialRepository.findByMaterialIdIn(contentIdsByType.getOrDefault("material", List.of()))
                .stream().collect(Collectors.toMap(Material::getMaterialId, MaterialDto.Response::of));

        Map<Long, Object> assignmentMap = assignmentRepository.findByAssignmentIdIn(contentIdsByType.getOrDefault("assignment", List.of()))
                .stream().collect(Collectors.toMap(Assignment::getAssignmentId, AssignmentDTO.Response::of));

        List<ContentOrderDto.Response> orderedList = new ArrayList<>();
        for (ContentOrder order : contentOrders) {
            Object contentData = switch (order.getContentType()) {
                case "lecture" -> lectureMap.get(order.getContentId());
                case "video" -> videoMap.get(order.getContentId());
                case "material" -> materialMap.get(order.getContentId());
                case "assignment" -> assignmentMap.get(order.getContentId());
                default -> null;
            };

            if (contentData != null) {
                orderedList.add(new ContentOrderDto.Response(order.getContentOrderId(), order.getContentType(), contentData));
            }
        }

        return orderedList;

    }

    // 수강생 히스토리 전체 조회
    @Transactional
    public LectureDTO.HistoryResponse getLectureHistories(Long courseId, Long userId) {
        // 과정 찾기(필요?)
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("해당 과정이 존재하지 않습니다."));

        // courseAttendees 찾기(강의수강생 검증)
        CourseAttendees courseAttendees = courseAttendeesRepository.findByCourse_CourseIdAndUser_UserId(courseId,userId)
                .orElseThrow(()-> new IllegalArgumentException("수강생이 아닙니다."));


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

    @Transactional
    public List<CurriculumResponseDto> getToDoList(Long courseId, Long userId, LocalDateTime date) {
        // 과정 찾기
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("해당 과정이 존재하지 않습니다."));

        // 강의 수강생인지 확인
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new IllegalArgumentException("유효한 사용자가 아닙니다."));

        CourseAttendees courseAttendees = courseAttendeesRepository.findByCourseAndUser(course,user)
                .orElseThrow(()-> new IllegalArgumentException("수강생이 아닙니다."));

        // coures내에서 lectureList조회
        List<Lecture> lectureList = lectureRepository.findAllByCourse(course);

        if (date == null) {
            // 전체조회
        }


        // 입력날짜를 기간으로 포함하는 lecture, video, assignment, material 필터링
        return lectureList.stream().map(lecture -> {
            List<VideoResponseDto> videos = lecture.getVideos().stream()
                    // 입력날짜를 기간으로 포함하는 video만 필터링
                    .filter(video -> {
                        return (video.getStartDate() != null && video.getEndDate() != null) &&              // startDate와 endDate가 null이 아니고
                                (date.isEqual(video.getStartDate()) || date.isEqual(video.getEndDate()) ||  // date가 startDate와 같거나, endDate와 같거나
                                        (date.isAfter(video.getStartDate()) && date.isBefore(video.getEndDate()))); // date가 startDate보다 앞이면서 endDate보다 뒤일때
                    })
                    // 필터링된 video로 attendees의 videoHistory 조회
                    .map(video -> {
                        VideoHistory videoHistory=videoHistoryRepository.findByVideoAndCourseAttendees(video, courseAttendees);
                        if (videoHistory == null) {
                            throw new IllegalArgumentException("잘못된접근");
                        }
                        // video와 시청여부 포함한 dto 반환
                        return VideoResponseDto.builder()
                                .videoId(video.getVideoId())
                                .videoTitle(video.getVideoTitle())
                                .videoUrl(video.getVideoUrl())
                                .startDate(video.getStartDate())
                                .endDate(video.getEndDate())
                                .videoHistoryStatus(videoHistory.getVideoHistoryStatus())
                                .build();
                    }).toList();

            List<MaterialResponseDto> materials = lecture.getMaterials().stream()
                    // 강의자료는 필터링 없음
                    // material로 attendees의 materialHistory 조회
                    .map(material -> {
                        MaterialHistory materialHistory = materialHistoryRepository.findByMaterialAndCourseAttendees(material, courseAttendees);
                        if (materialHistory == null) {
                            throw new IllegalArgumentException("잘못된접근");
                        }
                        // material과 다운로드여부 포함한 dto 반환
                        return MaterialResponseDto.builder()
                                .materialId(material.getMaterialId())
                                .materialTitle(material.getMaterialTitle())
                                .materialFile(material.getMaterialFile())
                                .materialHistoryStatus(materialHistory.isMaterialHistoryStatus())
                                .build();
                    }).toList();

            List<AssignmentResponseDto> assignments = lecture.getAssignments().stream()
                    // 입력날짜를 기간으로 포함하는 assignment만 필터링
                    .filter(assignment -> {
                        return (assignment.getStartDate() != null && assignment.getEndDate() != null) &&              // startDate와 endDate가 null이 아니고
                                (date.isEqual(assignment.getStartDate()) || date.isEqual(assignment.getEndDate()) ||  // date가 startDate와 같거나, endDate와 같거나
                                        (date.isAfter(assignment.getStartDate()) && date.isBefore(assignment.getEndDate()))); // date가 startDate보다 앞이면서 endDate보다 뒤일때
                    })
                    // 필터링된 assignment로 attendees의 submission 조회
                    .map(assignment -> {
                        Submission submission = submissionRepository.findByAssignmentAndCourseAttendees(assignment,courseAttendees);
                        if (submission == null) {
                            throw new IllegalArgumentException("잘못된접근");
                        }
                        // assignment와 과제제출여부 포함한 dto 반환
                        return AssignmentResponseDto.builder()
                                .assignmentId(assignment.getAssignmentId())
                                .assignmentTitle(assignment.getAssignmentTitle())
                                .assignmentDescription(assignment.getAssignmentDescription())
                                .startDate(assignment.getStartDate())
                                .endDate(assignment.getEndDate())
                                .submissionStatus(submission.getSubmissionStatus())
                                .build();
                    }).toList();


            // 반환된 데이터를 합쳐서 todoList로 반환 (커리큘럼 조회dto 재활용)
            return new CurriculumResponseDto(
                    lecture.getLectureId(),
                    lecture.getLectureTitle(),
                    lecture.getLectureDescription(),
                    videos,
                    materials,
                    assignments,
                    lecture.getStartDate(),
                    lecture.getEndDate()
            );
        })
        // 각 lecture에 video, assignment, material 3개가 전부 없으면 lecture도 필터링
        .filter(curriculum -> !(curriculum.getVideos().isEmpty() &&
                //curriculum.getMaterials().isEmpty() &&
                curriculum.getAssignments().isEmpty()))
        .collect(Collectors.toList());
    }

    // TODO 일주일전체 조회 추가

}
