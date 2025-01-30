package itda.ieoso.Course;

import itda.ieoso.Assignment.Assignment;
import itda.ieoso.Assignment.AssignmentRepository;
import itda.ieoso.CourseAttendees.CourseAttendees;
import itda.ieoso.CourseAttendees.CourseAttendeesRepository;
import itda.ieoso.CourseAttendees.CourseAttendeesStatus;
import itda.ieoso.Material.Material;
import itda.ieoso.Material.MaterialHistory;
import itda.ieoso.Material.MaterialHistoryRepository;
import itda.ieoso.Material.MaterialRepository;
import itda.ieoso.Submission.Submission;
import itda.ieoso.Submission.SubmissionRepository;
import itda.ieoso.Submission.SubmissionStatus;
import itda.ieoso.User.User;
import itda.ieoso.User.UserDTO;
import itda.ieoso.User.UserRepository;
import itda.ieoso.Video.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
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


    // 강좌 생성
    public CourseDTO createCourse(Long userId, String courseTitle, String courseDescription, int maxStudents, LocalDate closedDate) {
        // userId로 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        if (closedDate == null) {
            throw new IllegalArgumentException("closedDate cannot be null.");
        }

        String entryCode = generateEntryCode();
        // Course 객체 생성 (builder 사용)
        Course course = Course.builder()
                .user(user)
                .courseTitle(courseTitle)
                .courseDescription(courseDescription)
                .maxStudents(maxStudents)
                .closedDate(closedDate)
                .courseThumbnail(null) // courseThumbnail은 null로 설정
                .entryCode(entryCode) // entryCode도 null로 설정
                .build();

        course.setCreatedAt(LocalDateTime.now());
        course.setUpdatedAt(LocalDateTime.now()); // 처음 생성 시 updatedAt도 현재 시간

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
        CourseDTO courseDTO = CourseDTO.of(course, userInfoDto);

        return courseDTO;
    }

    // 강좌 조회
    public CourseDTO getCourseById(Long courseId) {
        // 강좌 조회
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("강좌를 찾을 수 없습니다"));

        // UserDTO 변환
        UserDTO.UserInfoDto userInfoDto = UserDTO.UserInfoDto.of(course.getUser(), course.getUser().getProfileImageUrl());

        // CourseDTO로 변환해서 반환
        return CourseDTO.of(course, userInfoDto);
    }

    // 강좌 수정
    public CourseDTO updateCourse(Long courseId, Long userId, String courseTitle, String courseDescription, int maxStudents, LocalDate closedDate, String courseThumbnail) {
        // 기존 강좌 조회
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("강좌를 찾을 수 없습니다"));

        // 강좌를 생성한 사용자 ID와 요청한 사용자 ID가 일치하는지 확인
        if (!course.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("이 강좌를 수정할 권한이 없습니다.");
        }

        // 기존 객체 수정 (새로 객체를 생성하지 않고 덮어씀)
        course.setCourseTitle(courseTitle);
        course.setCourseDescription(courseDescription);
        course.setMaxStudents(maxStudents);
        course.setClosedDate(closedDate);
        course.setCourseThumbnail(courseThumbnail);
        course.setUpdatedAt(LocalDateTime.now());   // updatedAt 갱신

        // UserDTO 변환
        UserDTO.UserInfoDto userInfoDto = UserDTO.UserInfoDto.of(course.getUser(), course.getUser().getProfileImageUrl());

        // 데이터베이스에 저장
        CourseDTO courseDTO = CourseDTO.of(course, userInfoDto);
        courseRepository.save(course);

        return courseDTO;
    }

    // 강좌 삭제
    public void deleteCourse(Long courseId, Long userId) {
        // 강좌 조회
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("강좌를 찾을 수 없습니다"));

        // 강좌를 생성한 사용자 ID와 요청한 사용자 ID가 일치하는지 확인
        if (!course.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("이 강좌를 삭제할 권한이 없습니다.");
        }

        // 강좌 삭제
        courseRepository.delete(course);
    }

    // 입장코드 생성
    private String generateEntryCode() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8);  // 예: 32자 중 앞 8자 사용
    }

    public void enterCourse(Long courseId, Long userId, String entryCode) {
        // 1. 강의 존재 여부 확인
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("해당 강의를 찾을 수 없습니다."));

        // 2. 입장 코드 검증
        if (!course.getEntryCode().equals(entryCode)) {
            throw new IllegalArgumentException("잘못된 입장 코드입니다.");
        }

        // 3. 유저 존재 여부 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다."));

        // 4. 이미 강의에 등록되어 있는지 확인
        boolean isAlreadyEnrolled = courseAttendeesRepository.existsByCourseAndUser(course, user);
        if (isAlreadyEnrolled) {
            throw new IllegalArgumentException("이미 이 강의에 등록된 유저입니다.");
        }

        // 5. CourseAttendees 생성 및 저장
        CourseAttendees courseAttendees = CourseAttendees.builder()
                .course(course)
                .user(user)
                .joinedAt(LocalDate.now())
                .courseAttendeesStatus(CourseAttendeesStatus.ACTIVE)
                .build();

        courseAttendeesRepository.save(courseAttendees);

        // TODO courseAttendees에 대한 모든 history생성

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
                .orElseThrow(() -> new RuntimeException("강좌를 찾을 수 없습니다"));

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


}




