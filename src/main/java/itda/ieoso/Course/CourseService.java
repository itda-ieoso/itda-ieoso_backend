package itda.ieoso.Course;

import itda.ieoso.Announcement.AnnouncementRepository;
import itda.ieoso.Assignment.Assignment;
import itda.ieoso.Assignment.AssignmentRepository;
import itda.ieoso.ContentOrder.ContentOrderRepository;
import itda.ieoso.ContentOrder.ContentOrderService;
import itda.ieoso.CourseAttendees.CourseAttendees;
import itda.ieoso.CourseAttendees.CourseAttendeesRepository;
import itda.ieoso.CourseAttendees.CourseAttendeesStatus;
import itda.ieoso.Exception.CustomException;
import itda.ieoso.Exception.ErrorCode;
import itda.ieoso.File.S3Service;
import itda.ieoso.Lecture.Lecture;
import itda.ieoso.Lecture.LectureRepository;
import itda.ieoso.Material.Material;
import itda.ieoso.Material.MaterialRepository;
import itda.ieoso.MaterialHistory.MaterialHistory;
import itda.ieoso.MaterialHistory.MaterialHistoryRepository;
import itda.ieoso.Submission.Submission;
import itda.ieoso.Submission.SubmissionRepository;
import itda.ieoso.Submission.SubmissionStatus;
import itda.ieoso.User.User;
import itda.ieoso.User.UserDTO;
import itda.ieoso.User.UserRepository;
import itda.ieoso.User.UserService;
import itda.ieoso.Video.*;
import itda.ieoso.VideoHistory.VideoHistory;
import itda.ieoso.VideoHistory.VideoHistoryRepository;
import itda.ieoso.VideoHistory.VideoHistoryStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.sql.Time;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final CourseAttendeesRepository courseAttendeesRepository;
    private final VideoRepository videoRepository;
    private final VideoHistoryRepository videoHistoryRepository;
    private final MaterialRepository materialRepository;
    private final MaterialHistoryRepository materialHistoryRepository;
    private final SubmissionRepository submissionRepository;
    private final AssignmentRepository assignmentRepository;
    private final AnnouncementRepository announcementRepository;
    private final UserService userService;
    private final S3Service s3Service;
    private final ContentOrderService contentOrderService;
    private final LectureRepository lectureRepository;
    private final ContentOrderRepository contentOrderRepository;

    // 강의실 생성
    @Transactional
    public CourseDTO createCourse(Long userId) {
        // userId로 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // Course null 객체 생성 (builder 사용)
        Course course = Course.builder()
                .user(user)
                .courseTitle("빈 강의실")
                .courseDescription("빈 강의실 입니다.")
                //.maxStudents(50) // default TODO 필요여부 프론트에 여쭤보기
                .instructorName(user.getName())
                .startDate(null)
                .endDate(null)
                .durationWeeks(-1)
                .lectureDay(null)
                .lectureTime(null)
                .assignmentDueDay(null)
                .assignmentDueTime(null)
                .difficultyLevel(Course.DifficultyLevel.EASY)
                .courseThumbnail(null)
                .entryCode(generateEntryCode())
                .init(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // 데이터베이스에 저장
        courseRepository.save(course);

        // 생성한 사람을 CourseAttendees에 추가
        CourseAttendees courseAttendees = CourseAttendees.builder()
                .course(course)
                .user(user)
                .joinedAt(LocalDate.now()) // 현재 시간
                .courseAttendeesStatus(CourseAttendeesStatus.OWNER) // 소유자 역할로 상태 설정
                .build();

        courseAttendeesRepository.save(courseAttendees); // CourseAttendees 저장

        // UserDTO 생성
        UserDTO.UserInfoDto userInfoDto = UserDTO.UserInfoDto.of(course.getUser(), course.getUser().getProfileImageUrl());

        // CourseDTO로 변환하여 반환
        CourseDTO courseDTO = CourseDTO.of(course, userInfoDto, null);

        return courseDTO;
    }

    // 입장코드 생성
    private String generateEntryCode() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8);  // 예: 32자 중 앞 8자 사용
    }

    // 강의실 수정
    @Transactional
    public CourseDTO updateCourse(Long courseId, Long userId, CourseDTO.BasicUpdateRequest request) {
        // 기존 강좌 조회
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CustomException(ErrorCode.COURSE_NOT_FOUND));

        // 강좌를 생성한 사용자 ID와 요청한 사용자 ID가 일치하는지 확인
        if (!course.getUser().getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.COURSE_PERMISSION_DENIED);
        }

        // 예외처리
        // dureation, startDate 필수
        if (request.durationWeeks() == null || (request.durationWeeks() <= 0 || request.durationWeeks() > 12)) {
            throw new CustomException(ErrorCode.INVALID_DURATION_WEEK);
        }

        if (request.startDate() == null || request.startDate().isBefore(LocalDate.now())) {
            throw new CustomException(ErrorCode.INVALID_DATE_RANGE);
        }

        // 리스트를 문자열로 변환 (예: 쉼표로 구분)
        String lectureDayString = "";
        if (request.lectureDay() != null) {
            lectureDayString = request.lectureDay().stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));
        }

        String assignmentDueDayString = "";
        if (request.assignmentDueDay() != null) {
            assignmentDueDayString = request.assignmentDueDay().stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));

        }
        // course 수정
        // course.setMaxStudents(maxStudents);
        if (request.title() != null && !request.title().isEmpty()) course.setCourseTitle(request.title());
        if (request.instructorName() != null && !request.instructorName().isEmpty()) course.setInstructorName(request.instructorName());
        if (request.startDate() != null) course.setStartDate(request.startDate());
        course.setDurationWeeks(request.durationWeeks());
        if (request.lectureDay()!=null && !request.lectureDay().isEmpty()) course.setLectureDay(lectureDayString);
        if (request.lectureTime() != null) course.setLectureTime(request.lectureTime());
        if (request.assignmentDueDay()!=null && !request.assignmentDueDay().isEmpty()) course.setAssignmentDueDay(assignmentDueDayString);
        if (request.assignmentDueTime() != null) course.setAssignmentDueTime(request.assignmentDueTime());
        if (request.difficultyLevel() != null) course.setDifficultyLevel(request.difficultyLevel());
        if (request.startDate() !=null && request.durationWeeks() > 0) course.setEndDate((request.startDate().plusWeeks(request.durationWeeks()-1)).plusDays(6));
        course.setUpdatedAt(LocalDateTime.now());

        // 데이터베이스에 저장
        courseRepository.save(course);

        // 초기 업데이트 여부 확인
        if (!course.isInit()) {

            // 예외처리 묶기


            // 초기 설정시 커리큘럼 자동생성
            initializeCourse(course, request.startDate(), request.durationWeeks(),
                             request.lectureDay(), request.lectureTime(),
                             request.assignmentDueDay(), request.assignmentDueTime());

            // 초기설정여부 상태변경
            course.updateInit();
        }

        // UserDTO 변환
        UserDTO.UserInfoDto userInfoDto = UserDTO.UserInfoDto.of(course.getUser(), course.getUser().getProfileImageUrl());

        CourseDTO courseDTO = CourseDTO.of(course, userInfoDto, null);

        return courseDTO;
    }

    // 강의실 설정창 초기설정
    private void initializeCourse(Course course, LocalDate startDate, Integer durationWeeks,
                                  List<Integer> lectureDay, Time lectureTime,
                                  List<Integer> assignmentDueDay, Time assignmentDueTime) {

        // durationWeeks 만큼 lecture 생성
        for (int i = 1; i <= durationWeeks; i++) {
            // lecture 생성 및 저장
            Lecture lecture = createLecture(course, startDate);

            // lectureDay 만큼 video 자동생성
            if ((lectureDay != null && !lectureDay.isEmpty()) || lectureTime != null) {
                for (int k = 0; k < lectureDay.size(); k++) {
                    // video 생성 및 저장
                    int day = lectureDay.get(k); // 그 주의 요일에 해당하는 날짜 찾기)
                    Video video = createVideo(course, lecture, day, lectureTime);
                    videoRepository.save(video);
                    // contentOrder 생성
                    contentOrderService.createContentOrder(course, lecture, "video", video.getVideoId());

                }
            }

            // assignmentDueDay 만큼 assignment 자동생성
            if ((assignmentDueDay != null && !assignmentDueDay.isEmpty()) || assignmentDueTime != null) {
                for (int j = 0; j < assignmentDueDay.size(); j++) {
                    // assignment 생성 및 저장
                    int day = assignmentDueDay.get(j);
                    Assignment assignment = createAssignment(course, lecture, day, assignmentDueTime);
                    assignmentRepository.save(assignment);
                    // contentOrder 생성
                    contentOrderService.createContentOrder(course, lecture,"assignment", assignment.getAssignmentId());
                }
            }

            startDate = startDate.plusWeeks(1);

//            for (int i = 0; i < lectureDay.size(); i++) {
//                // material 생성
//                Material material = createMaterial(course, lecture);
//                // material 저장
//                materialRepository.save(material);
//                // contentOrder 생성
//                contentOrderService.createContentOrder(course, lecture,"material", material.getMaterialId());

        }
    }

    // lecture 생성
    private Lecture createLecture(Course course, LocalDate startDate) {

        // lecture 생성
        Lecture lecture = Lecture.builder()
                .course(course)
                .lectureTitle("챕터")
                .lectureDescription("챕터 설명을 작성하세요.")
                .startDate(startDate)
                .endDate(startDate.plusDays(6))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .videos(new ArrayList<>())
                .assignments(new ArrayList<>())
                .materials(new ArrayList<>())
                .build();

        // lecture 저장
        lectureRepository.save(lecture);
        return lecture;

    }

    // video 와 videoHistory 생성
    private Video createVideo(Course course, Lecture lecture, int day, Time lectureTime) {
        // 날짜 반환
        LocalDate startDate = findDateInWeek(lecture.getStartDate(), lecture.getEndDate(), day);

        Video video = Video.builder()
                .course(lecture.getCourse())
                .lecture(lecture)
                .videoTitle("강의 영상 제목을 입력하세요.")
                .videoUrl("영상 링크 첨부")
                .startDate(LocalDateTime.of(startDate, lectureTime.toLocalTime()))
                .endDate(LocalDateTime.of(course.getEndDate(), LocalTime.of(23, 59, 59))) // 강좌종료
                .videoHistories(new ArrayList<>())
                .build();

        // video에대한 모든 attendees의 videoHistory 추가
        List<CourseAttendees> attendees = courseAttendeesRepository.findAllByCourse(course);

        List<VideoHistory> videoHistoryList = attendees.stream()
                .filter(attendee -> attendee.getCourseAttendeesStatus()== CourseAttendeesStatus.ACTIVE)
                .map(attendee -> VideoHistory.builder()
                        .course(course)
                        .video(video)
                        .courseAttendees(attendee)
                        .videoHistoryStatus(VideoHistoryStatus.NOT_WATCHED)
                        .build())
                .collect(Collectors.toList());

        // vidoe에 videoHistory추가
        video.getVideoHistories().addAll(videoHistoryList);

        // video 반환
        return video;
    }

    // assignment 와 submission 생성
    private Assignment createAssignment(Course course, Lecture lecture, int day, Time assignmentDueTime) {
        // 요일 받아오기
        LocalDate endDate = findDateInWeek(lecture.getStartDate(), lecture.getEndDate(), day);

        Assignment assignment = Assignment.builder()
                .course(lecture.getCourse())
                .lecture(lecture)
                .assignmentTitle("과제 제목을 입력하세요.")
                .assignmentDescription("과제 설명")
                .startDate(LocalDateTime.of(course.getStartDate(),LocalTime.of(0, 0, 0))) // 강좌시작일
                .endDate(LocalDateTime.of(endDate, assignmentDueTime.toLocalTime()))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .submissions(new ArrayList<>())
                .build();

        // assignment 에대한 모든 attendees의 submission 추가
        List<CourseAttendees> attendees = courseAttendeesRepository.findAllByCourse(course);

        List<Submission> submissionList = attendees.stream()
                .filter(attendee -> attendee.getCourseAttendeesStatus()== CourseAttendeesStatus.ACTIVE)
                .map(attendee -> Submission.builder()
                        .course(course)
                        .assignment(assignment)
                        .courseAttendees(attendee)
                        .user(attendee.getUser())
                        .submissionStatus(SubmissionStatus.NOT_SUBMITTED)
                        .build())
                .collect(Collectors.toList());

        // assignment에 submission추가
        assignment.getSubmissions().addAll(submissionList);

        // assignment 반환
        return assignment;
    }

    // material 과 materialHistory 생성
    //    private Material createMaterial(Course course, Lecture lecture) {
//        Material material = Material.builder()
//                .course(lecture.getCourse())
//                .lecture(lecture)
//                .materialTitle("강의 자료 제목을 입력하세요.")
//                .materialFile("강의자료 첨부")
//                .materialHistories(new ArrayList<>())
//                .build();
//
//        // video에대한 모든 attendees의 videoHistory 추가
//        List<CourseAttendees> attendees = courseAttendeesRepository.findAllByCourse(course);
//
//        List<MaterialHistory> materialHistoryList = attendees.stream()
//                .filter(attendee -> attendee.getCourseAttendeesStatus()== CourseAttendeesStatus.ACTIVE)
//                .map(attendee -> MaterialHistory.builder()
//                        .course(course)
//                        .material(material)
//                        .courseAttendees(attendee)
//                        .materialHistoryStatus(false)
//                        .build())
//                .collect(Collectors.toList());
//
//        // vidoe에 videoHistory추가
//        material.getMaterialHistories().addAll(materialHistoryList);
//
//        // video 반환
//        return material;
//    }

    // 입력한 요일로 날짜 찾기
    private LocalDate findDateInWeek(LocalDate startDate, LocalDate endDate, int targetDay) {
        if (targetDay < 1 || targetDay > 7) {
            throw new CustomException(ErrorCode.INVALID_DAY_OF_WEEK);
        }

        DayOfWeek startDay = startDate.getDayOfWeek(); // 시작날짜의 요일(월~일: 1~7)
        int startDayInt = startDay.getValue();

        int daysToAdd = targetDay - startDayInt;
        if (daysToAdd < 0) {
            daysToAdd += 7; // 음수면 +7로 양수로 변경
        }

        LocalDate resultDate = startDate.plusDays(daysToAdd);
        return resultDate.isAfter(endDate) ? null : resultDate;
    }

    // 강의실 개요 편집
    @Transactional
    public CourseDTO updateCourseOverview(Long courseId, Long userId, String description, MultipartFile file) throws IOException {
        // 기존 강좌 조회
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CustomException(ErrorCode.COURSE_NOT_FOUND));

        // 강좌를 생성한 사용자 ID와 요청한 사용자 ID가 일치하는지 확인
        if (!course.getUser().getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.COURSE_PERMISSION_DENIED);
        }

//        // 기존 정보 가져오기
//        String currentDescription = course.getCourseDescription();
//        String currentThumbnail = course.getCourseThumbnail();

        // 개요 설명 업데이트
        if (description != null && !description.isEmpty()) {
            course.setCourseDescription(description);  // 새로 받은 description으로 업데이트
        }

        // 파일 업데이트 (새 파일이 있을 경우)
        if (file != null && !file.isEmpty()) {
            // MultipartFile을 File로 변환
            File convertedFile = s3Service.convertMultipartFileToFile(file);

            // 파일을 S3에 업로드하고 URL 받기
            String newFileUrl = s3Service.uploadFile("course-thumbnails", file.getOriginalFilename(), convertedFile);

            // 새로운 썸네일 URL로 업데이트
            course.setCourseThumbnail(newFileUrl);
        }

        // UserDTO 변환
        UserDTO.UserInfoDto userInfoDto = UserDTO.UserInfoDto.of(course.getUser(), course.getUser().getProfileImageUrl());

        // 데이터베이스에 저장
        CourseDTO courseDTO = CourseDTO.of(course, userInfoDto, null);
        courseRepository.save(course);

        return courseDTO;
    }

    // 강의실 삭제
    @Transactional
    public void deleteCourse(Long courseId, Long userId) {
        // 강좌 조회
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CustomException(ErrorCode.COURSE_NOT_FOUND));

        // 강좌를 생성한 사용자 ID와 요청한 사용자 ID가 일치하는지 확인
        if (!course.getUser().getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.COURSE_PERMISSION_DENIED);
        }

        // 공지 삭제
        announcementRepository.deleteAllByCourse(course);

        // 유저들의 히스토리 삭제
        materialHistoryRepository.deleteAllByCourse(course);
        videoHistoryRepository.deleteAllByCourse(course);
        submissionRepository.deleteAllByCourse(course);

        // 강좌에 있는 courseAttendees 모두 삭제
        courseAttendeesRepository.deleteAllByCourse(course);

        // contentOrder 삭제
        contentOrderRepository.deleteAllByCourse(course);

        // 강좌 삭제
        courseRepository.delete(course);
    }

    // 강의실 설정창 조회(설정 & 개요 페이지)
    @Transactional
    public CourseDTO getCourseById(Long courseId) throws IOException {
        // 강좌 조회
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CustomException(ErrorCode.COURSE_NOT_FOUND));

        String presignedThumbnailUrl = null;
        if (course.getCourseThumbnail() != null) {
            presignedThumbnailUrl = s3Service.generatePresignedUrl(course.getCourseThumbnail());
        }

        // UserDTO 변환
        UserDTO.UserInfoDto userInfoDto = UserDTO.UserInfoDto.of(course.getUser(), course.getUser().getProfileImageUrl());

        // CourseDTO로 변환해서 반환
        return CourseDTO.of(course, userInfoDto, presignedThumbnailUrl);
    }

    // 강의실 입장 (입장 유저의 히스토리 생성)
    public void enterCourse(Long userId, String entryCode) {
        // 1. 강의 존재 여부 확인
        Course course = courseRepository.findByEntryCode(entryCode)
                .orElseThrow(() -> new CustomException(ErrorCode.COURSE_NOT_FOUND));

        // 3. 유저 존재 여부 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 4. 이미 강의에 등록되어 있는지 확인
        boolean isAlreadyEnrolled = courseAttendeesRepository.existsByCourseAndUser(course, user);
        if (isAlreadyEnrolled) {
            throw new CustomException(ErrorCode.ALREADY_ENROLLED);
        }

        // 5. CourseAttendees 생성 및 저장
        CourseAttendees courseAttendees = CourseAttendees.builder()
                .course(course)
                .user(user)
                .joinedAt(LocalDate.now())
                .courseAttendeesStatus(CourseAttendeesStatus.ACTIVE)
                .build();

        courseAttendeesRepository.save(courseAttendees);

        // courseAttendees에 대한 모든 history생성
        // video 히스토리 생성
        List<Video> videoList = videoRepository.findAllByCourse(course);
        saveHistories(
                course,
                courseAttendees,
                videoList,
                video -> VideoHistory.builder()
                        .course(course)
                        .courseAttendees(courseAttendees)
                        .video(video)
                        .videoHistoryStatus(VideoHistoryStatus.NOT_WATCHED)
                        .build(),
                (c, ca) -> videoHistoryRepository.findAllByCourseAndCourseAttendees(c,ca),
                VideoHistory::getVideo,
                videoHistoryRepository
        );

        // materialHistory 생성
        List<Material> materialList = materialRepository.findAllByCourse(course);
        saveHistories(
                course,
                courseAttendees,
                materialList,
                material -> MaterialHistory.builder()
                        .course(course)
                        .courseAttendees(courseAttendees)
                        .material(material)
                        .materialHistoryStatus(false)
                        .build(),
                (c,ca) -> materialHistoryRepository.findAllByCourseAndCourseAttendees(c,ca),
                MaterialHistory::getMaterial,
                materialHistoryRepository
        );

        // submission 생성
        List<Assignment> assignmentList = assignmentRepository.findAllByCourse(course);
        saveHistories(
                course,
                courseAttendees,
                assignmentList,
                assignment -> Submission.builder()
                        .course(course)
                        .courseAttendees(courseAttendees)
                        .assignment(assignment)
                        .user(courseAttendees.getUser())
                        .submissionStatus(SubmissionStatus.NOT_SUBMITTED)
                        .build(),
                (c,ca) -> submissionRepository.findAllByCourseAndCourseAttendees(c,ca),
                Submission::getAssignment,
                submissionRepository
        );

    }

    // 입장 코드 검증
    public boolean validateEntryCode(Long courseId, String entryCode) {
        // courseId로 강좌 조회
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CustomException(ErrorCode.COURSE_NOT_FOUND));

        // 입장 코드 비교
        return course.getEntryCode().equals(entryCode);
    }

    @Transactional
    public <T, E> void saveHistories( // (T = history / E = entity)
            Course course,
            CourseAttendees courseAttendees,
            List<E> entityList,
            Function<E, T> historyBuilder, // 각각의 히스토리 생성로직
            BiFunction<Course, CourseAttendees, List<T>> existingHistoryFinder, // 각각의 히스토리 조회
            Function<T,E> entityExtractor,
            JpaRepository<T, Long> repository
    ) {
        // 데이터베이스에 존재하는 attendees의 entity히스토리 조회
        List<T> existingHistories = existingHistoryFinder.apply(course, courseAttendees);

        // attendees의 entity히스토리 목록을 통해 History가 존재하는 entity들 불러오기
        Set<E> existingEntities = existingHistories.stream()
                .map(entityExtractor)
                .collect(Collectors.toSet());

        // existingEntities에 있는 entity를 제외한 entity들에대해 attendees의 History 생성
        List<T> newHistories = entityList.stream()
                .filter(entity -> !existingEntities.contains(entity))
                .map(historyBuilder)
                .collect(Collectors.toList());

        // db에 저장
        repository.saveAll(newHistories);
    }

    // 각 히스토리 추출 메서드
    private <T,E> E extractEntity(T history) {
        if (history instanceof VideoHistory) {
            return (E) ((VideoHistory) history).getVideo();
        } else if (history instanceof MaterialHistory) {
            return (E) ((MaterialHistory) history).getMaterial();
        } else if (history instanceof Submission) {
            return (E) ((Submission) history).getAssignment();
        }
        throw new IllegalArgumentException("지원하지 않는 타입" + history.getClass());
    }

    // 사용자가 가입한 강의실 목록 조회
    public List<CourseDTO> getCoursesByUser(Long userId) throws IOException {
        List<CourseAttendees> courseAttendeesList = courseAttendeesRepository.findByUser_UserId(userId);

        // 강의실 목록 반환 (DTO 변환)
        List<CourseDTO> courseDTOList = new ArrayList<>();
        for (CourseAttendees courseAttendees : courseAttendeesList) {

            Course course = courseAttendees.getCourse();
            UserDTO.UserInfoDto userInfoDto = UserDTO.UserInfoDto.of(course.getUser(), course.getUser().getName());

            String presignedThumbnailUrl = null;
            if (course.getCourseThumbnail() != null) {
                presignedThumbnailUrl = s3Service.generatePresignedUrl(course.getCourseThumbnail());
            }

            // CourseDTO 생성
            CourseDTO courseDTO = CourseDTO.of(course, userInfoDto, presignedThumbnailUrl);
            courseDTOList.add(courseDTO);
        }

        return courseDTOList;
    }


}




