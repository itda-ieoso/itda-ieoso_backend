package itda.ieoso.Lecture;

import itda.ieoso.Course.Course;
import itda.ieoso.Course.CourseRepository;
import itda.ieoso.CourseAttendees.CourseAttendeesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LectureService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private LectureRepository lectureRepository;

    // 강의 생성
    public LectureDTO createLecture(Long courseId, Long userId, String title, String description, String videoLink, LocalDate startDate, LocalDate endDate) {
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
                .lectureTitle(title)
                .lectureDescription(description)
                .videoLink(videoLink)
                .startDate(startDate)
                .endDate(endDate)
                .build();

        lecture.setCreatedAt(LocalDateTime.now());
        lecture.setUpdatedAt(LocalDateTime.now()); // 처음 생성 시 updatedAt도 현재 시간

        lectureRepository.save(lecture); // 저장 후 반환

        return LectureDTO.of(lecture);
    }

    // 강의 수정
    public LectureDTO updateLecture(Long courseId, Long lectureId, Long userId, String lectureTitle, String lectureDescription, String videoLink, LocalDate startDate, LocalDate endDate) {
        // 기존 강의 조회
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new RuntimeException("강의를 찾을 수 없습니다"));

        // 강의를 속한 과정의 생성자 ID와 요청한 사용자 ID가 일치하는지 확인
        if (!lecture.getCourse().getUser().getUserId().equals(userId)) {
            throw new RuntimeException("이 강의를 수정할 권한이 없습니다.");
        }

        // 기존 객체 수정 (새로 객체를 생성하지 않고 덮어씀)
        lecture.setLectureTitle(lectureTitle);
        lecture.setLectureDescription(lectureDescription);
        lecture.setVideoLink(videoLink);
        lecture.setStartDate(startDate);
        lecture.setEndDate(endDate);
        lecture.setUpdatedAt(LocalDateTime.now()); // updatedAt 갱신

        // LectureDTO로 변환
        LectureDTO lectureDTO = LectureDTO.of(lecture);

        // 데이터베이스에 저장
        lectureRepository.save(lecture);

        return lectureDTO;
    }

    // 강의 삭제
    public void deleteLecture(Long courseId, Long lectureId, Long userId) {
        // 강의 찾기
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("해당 강의가 존재하지 않습니다."));

        // 강의의 과정 생성자인지 확인
        if (!isCourseCreator(lecture.getCourse().getCourseId(), userId)) {
            throw new IllegalArgumentException("강의를 삭제할 권한이 없습니다.");
        }

        // 강의 삭제
        lectureRepository.delete(lecture);
    }

    public List<Lecture> getLecturesByCourseId(Long courseId, Long userId) {
        // 과정 참여자인지 확인
        if (!isCourseAttendee(courseId, userId)) {
            throw new IllegalArgumentException("과정에 참여한 사용자만 강의를 조회할 수 있습니다.");
        }

        // 강의 리스트 반환
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("해당 과정이 존재하지 않습니다."))
                .getLectures();
    }

    // 과정 생성자인지 확인
    public boolean isCourseCreator(Long courseId, Long userId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("해당 과정이 존재하지 않습니다."));
        return course.getUser().getUserId().equals(userId); // Course에 `creatorId` 필드가 있다고 가정
    }

    // 강의가 속한 과정의 생성자인지 확인
    public boolean isLectureOwner(Long lectureId, Long userId) {
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("해당 강의가 존재하지 않습니다."));
        return isCourseCreator(lecture.getCourse().getCourseId(), userId);
    }

    // 과정 참여자인지 확인
    @Autowired
    private CourseAttendeesRepository courseAttendeesRepository;

    public boolean isCourseAttendee(Long courseId, Long userId) {
        // `CourseAttendees` 테이블에서 courseId와 userId로 참여 상태 확인
        return courseAttendeesRepository.existsByCourse_CourseIdAndUser_UserId(courseId, userId);
    }

    // 강의 조회
    public LectureDTO getLectureById(Long lectureId) {
        // 강의 조회
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new RuntimeException("강의를 찾을 수 없습니다"));

        // LectureDTO로 변환해서 반환
        return LectureDTO.of(lecture);
    }
}
