package itda.ieoso.Course;

import itda.ieoso.CourseAttendees.CourseAttendees;
import itda.ieoso.CourseAttendees.CourseAttendeesRepository;
import itda.ieoso.CourseAttendees.CourseAttendeesStatus;
import itda.ieoso.User.User;
import itda.ieoso.User.UserDTO;
import itda.ieoso.User.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final CourseAttendeesRepository courseAttendeesRepository;
    @Autowired
    public CourseService(CourseRepository courseRepository, UserRepository userRepository, CourseAttendeesRepository courseAttendeesRepository) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.courseAttendeesRepository = courseAttendeesRepository;
    }

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
    }

    // 입장 코드 검증
    public boolean validateEntryCode(Long courseId, String entryCode) {
        // courseId로 강좌 조회
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("강좌를 찾을 수 없습니다"));

        // 입장 코드 비교
        return course.getEntryCode().equals(entryCode);
    }


}




