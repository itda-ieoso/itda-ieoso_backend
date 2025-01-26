package itda.ieoso.Course;

import itda.ieoso.CourseAttendees.CourseAttendees;
import itda.ieoso.CourseAttendees.CourseAttendeesDTO;
import itda.ieoso.CourseAttendees.CourseAttendeesRepository;
import itda.ieoso.CourseAttendees.CourseAttendeesStatus;
import itda.ieoso.User.User;
import itda.ieoso.User.UserDTO;
import itda.ieoso.User.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
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

        // UserDTO 생성
        UserDTO.UserInfoDto userInfoDto = UserDTO.UserInfoDto.of(course.getUser(), course.getUser().getProfileImageUrl());

        // CourseDTO로 변환하여 반환
        CourseDTO courseDTO = CourseDTO.of(course, userInfoDto);

        // 데이터베이스에 저장
        courseRepository.save(course);

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

//    // 강의실 입장 및 CourseAttendeesDTO 반환 처리
//    public CourseAttendeesDTO enterCourse(Long courseId, Long userId, String entryCode) {
//        // 입장 코드 검증
//        boolean isValid = validateEntryCode(courseId, entryCode);
//        if (!isValid) {
//            throw new IllegalArgumentException("입장 코드가 잘못되었습니다.");
//        }
//
//        // 강의와 사용자 가져오기
//        Course course = courseRepository.findById(courseId).orElseThrow(() -> new RuntimeException("Course not found"));
//
//        // 사용자 정보 가져오기 (userId로 사용자 조회)
//        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
//
//        // 현재 시간과 상태 설정
//        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
//        CourseAttendeesStatus status = CourseAttendeesStatus.ACTIVE;
//
//        // CourseAttendees 엔티티 생성
//        CourseAttendees courseAttendees = new CourseAttendees(course, user, currentTime, status);
//
//        // 엔티티 DB에 저장
//        courseAttendeesRepository.save(courseAttendees);
//
//        // CourseAttendeesDTO 반환
//        return CourseAttendeesDTO.builder()
//                .courseId(course.getCourseId())
//                .userId(user.getUserId())
//                .joinedAt(currentTime)
//                .status(status.name())
//                .build();
//    }

    // 입장 코드 검증
    public boolean validateEntryCode(Long courseId, String entryCode) {
        // courseId로 강좌 조회
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("강좌를 찾을 수 없습니다"));

        // 입장 코드 비교
        return course.getEntryCode().equals(entryCode);
    }


}




