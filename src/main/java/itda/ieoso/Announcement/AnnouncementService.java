package itda.ieoso.Announcement;

import itda.ieoso.Course.Course;
import itda.ieoso.Course.CourseDTO;
import itda.ieoso.Course.CourseRepository;
import itda.ieoso.CourseAttendees.CourseAttendeesRepository;
import itda.ieoso.Exception.CustomException;
import itda.ieoso.Exception.ErrorCode;
import itda.ieoso.Material.MaterialDto;
import itda.ieoso.User.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final CourseRepository courseRepository;
    private final CourseAttendeesRepository courseAttendeesRepository;
    private final UserService userService;

    // 공지 생성(개설자만)
    @Transactional
    public AnnouncementDto.Response createAnnouncement(Long courseId, String token, AnnouncementDto.Request requestDto) {
        Long userId = userService.getUserByToken(token).getUserId();
        // 강좌 조회(개설자 검증)
        Course course = validateCourseCreator(courseId,userId);

        // 객체 생성
        Announcement announcement = Announcement.builder()
                .course(course)
                .announcementTitle(requestDto.title())
                .announcementContent(requestDto.content())
                .viewCount(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // 저장
        announcementRepository.save(announcement);

        // 반환
        AnnouncementDto.Response responseDto = AnnouncementDto.Response.builder()
                .announcementId(announcement.getAnnouncementId())
                .instructorName(announcement.getCourse().getInstructorName())
                .announcementTitle(announcement.getAnnouncementTitle())
                .announcementContent(announcement.getAnnouncementContent())
                .viewCount(announcement.getViewCount())
                .createdAt(announcement.getCreatedAt())
                .build();

        return responseDto;
    }

    // 공지 수정(개설자만)
    @Transactional
    public AnnouncementDto.Response updateAnnouncement(Long courseId, String token, Long announcementId, AnnouncementDto.Request requestDto) {
        Long userId = userService.getUserByToken(token).getUserId();
        // 강좌 조회(개설자 검증)
        Course course = validateCourseCreator(courseId,userId);

        // 공지 조회
        Announcement announcement = announcementRepository.findByCourseAndAnnouncementId(course,announcementId);
        if (announcement == null) {
            throw new CustomException(ErrorCode.ANNOUNCEMENT_NOT_FOUND);
        }

        // 수정
        if (requestDto.title()!=null) announcement.setAnnouncementTitle(requestDto.title());
        if (requestDto.content()!=null) announcement.setAnnouncementContent(requestDto.content());
        announcement.setUpdatedAt(LocalDateTime.now());
        announcementRepository.save(announcement);

        // 반환
        AnnouncementDto.Response responseDto = AnnouncementDto.Response.builder()
                .announcementId(announcement.getAnnouncementId())
                .instructorName(announcement.getCourse().getInstructorName())
                .announcementTitle(announcement.getAnnouncementTitle())
                .announcementContent(announcement.getAnnouncementContent())
                .viewCount(announcement.getViewCount())
                .createdAt(announcement.getCreatedAt())
                .build();

        return responseDto;
    }

    // 공지 삭제(개설자만)
    @Transactional
    public AnnouncementDto.deleteResponse deleteAnnouncement(Long courseId, String token, Long announcementId) {
        Long userId = userService.getUserByToken(token).getUserId();
        // 강좌조회(개설자 검증)
        Course course = validateCourseCreator(courseId,userId);

        // 공지 조회
        Announcement announcement = announcementRepository.findByCourseAndAnnouncementId(course,announcementId);
        if (announcement == null) {
            throw new CustomException(ErrorCode.ANNOUNCEMENT_NOT_FOUND);
        }

        // 삭제
        announcementRepository.delete(announcement);

        // 반환
        AnnouncementDto.deleteResponse response = AnnouncementDto.deleteResponse.builder()
                .id(announcementId)
                .message("announcement 삭제 완료")
                .build();

        return response;
    }

    // 공지 목록 조회(개설자, 수강생)
    @Transactional
    public List<AnnouncementDto.Response> getAnnouncements(Long courseId, String token) {
        Long userId = userService.getUserByToken(token).getUserId();
        // 강좌 조회(개설자, 수강생 검증)
        if (!validateCourseAttendees(courseId,userId)) {
            throw new CustomException(ErrorCode.COURSEATTENDEES_PERMISSION_DENIED);
        }

        // 공지 목록 조회
        List<Announcement> announcementList = announcementRepository.findAllByCourse_CourseId(courseId);

        // 반환
        List<AnnouncementDto.Response> announcementResponseDtos = announcementList.stream()
                .map(announcement -> AnnouncementDto.Response.builder()
                        .announcementId(announcement.getAnnouncementId())
                        .instructorName(announcement.getCourse().getInstructorName())
                        .announcementTitle(announcement.getAnnouncementTitle())
                        .viewCount(announcement.getViewCount())
                        .createdAt(announcement.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        return announcementResponseDtos;
    }

    // 공지 상세 조회(개설자, 수강생)
    @Transactional
    public AnnouncementDto.Response getAnnouncement(Long courseId, String token, Long announcementId) {
        Long userId = userService.getUserByToken(token).getUserId();
        // 강좌 조회(개설자, 수강생 검증)
        if (!validateCourseAttendees(courseId,userId)) {
            throw new CustomException(ErrorCode.COURSEATTENDEES_PERMISSION_DENIED);
        }

        // 공지 조회
        Announcement announcement = announcementRepository.findByCourse_CourseIdAndAnnouncementId(courseId,announcementId);
        if (announcement == null) {
            throw new CustomException(ErrorCode.ANNOUNCEMENT_NOT_FOUND);
        }

        // 조회수 업데이트
        announcement.updateViewCount();

        // 저장
        announcementRepository.save(announcement);

        // 반환
        AnnouncementDto.Response responseDto = AnnouncementDto.Response.builder()
                .announcementId(announcement.getAnnouncementId())
                .instructorName(announcement.getCourse().getInstructorName())
                .announcementTitle(announcement.getAnnouncementTitle())
                .announcementContent(announcement.getAnnouncementContent())
                .viewCount(announcement.getViewCount())
                .createdAt(announcement.getCreatedAt())
                .build();

        return responseDto;

    }

    // 개설자 검증
    private Course validateCourseCreator(Long courseId, Long userId) {
        // 강좌 조회
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CustomException(ErrorCode.COURSE_NOT_FOUND));

        // 개설자인지 검증
        if (!course.getUser().getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.COURSE_PERMISSION_DENIED);
        }

        return course;
    }

    // 강의참여자 검증
    private boolean validateCourseAttendees(Long courseId, Long userId) {
        // 강의 참여자인지 검증
        return courseAttendeesRepository.existsByCourse_CourseIdAndUser_UserId(courseId,userId);
    }

}
