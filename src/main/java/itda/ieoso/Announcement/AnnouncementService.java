package itda.ieoso.Announcement;

import itda.ieoso.Course.Course;
import itda.ieoso.Course.CourseDTO;
import itda.ieoso.Course.CourseRepository;
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

    // 유저 검증
    public Course validateAnnouncement(Long courseId, Long userId) {
        // 강좌 조회
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("해당 과정이 존재하지 않습니다."));

        // 개설자인지 검증
        if (!course.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("권한없음");
        }

        return course;
    }

    // 공지 생성
    @Transactional
    public AnnouncementResponseDto createAnnouncement(Long courseId, Long userId, AnnouncementRequestDto requestDto) {
        // 강좌 조회
        Course course = validateAnnouncement(courseId,userId);

        // 객체 생성
        Announcement announcement = Announcement.builder()
                .course(course)
                .announcementTitle(requestDto.getTitle())
                .announcementContent(requestDto.getContent())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // 저장
        announcementRepository.save(announcement);

        // 반환
        AnnouncementResponseDto responseDto = AnnouncementResponseDto.of(announcement);
        return responseDto;
    }

    // 공지 목록 조회
    @Transactional
    public List<AnnouncementResponseDto> getAnnouncements(Long courseId, Long userId) {
        // 강좌 조회
        Course course = validateAnnouncement(courseId,userId);

        // 공지 목록 조회
        List<Announcement> announcementList = announcementRepository.findAllByCourse(course);

        // 반환
        List<AnnouncementResponseDto> announcementResponseDtos = announcementList.stream()
                .map(announcement -> AnnouncementResponseDto.summary(announcement))
                .collect(Collectors.toList());

        return announcementResponseDtos;
    }

    // 공지 상세 조회
    @Transactional
    public AnnouncementResponseDto getAnnouncement(Long courseId, Long userId, Long announcementId) {
        // 강좌 조회
        Course course = validateAnnouncement(courseId,userId);

        // 공지 조회
        Announcement announcement = announcementRepository.findByCourseAndAnnouncementId(course,announcementId);
        if (announcement == null) {
            throw new IllegalArgumentException("공지를 찾을수없습니다.");
        }

        // 반환
        AnnouncementResponseDto responseDto = AnnouncementResponseDto.of(announcement);
        return responseDto;

    }

    // 공지 수정
    @Transactional
    public AnnouncementResponseDto updateAnnouncement(Long courseId, Long userId, Long announcementId, AnnouncementRequestDto requestDto) {
        // 강좌 조회
        Course course = validateAnnouncement(courseId,userId);

        // 공지 조회
        Announcement announcement = announcementRepository.findByCourseAndAnnouncementId(course,announcementId);
        if (announcement == null) {
            throw new IllegalArgumentException("공지를 찾을수없습니다.");
        }

        // 수정
        announcement.setAnnouncementTitle(requestDto.getTitle());
        announcement.setAnnouncementContent(requestDto.getContent());
        announcement.setUpdatedAt(LocalDateTime.now());
        announcementRepository.save(announcement);

        // 반환
        AnnouncementResponseDto responseDto = AnnouncementResponseDto.of(announcement);
        return responseDto;
    }

    // 공지 삭제
    public void deleteAnnouncement(Long courseId, Long userId, Long announcementId) {
        // 강좌조회
        Course course = validateAnnouncement(courseId,userId);

        // 공지 조회
        Announcement announcement = announcementRepository.findByCourseAndAnnouncementId(course,announcementId);
        if (announcement == null) {
            throw new IllegalArgumentException("공지를 찾을수없습니다.");
        }

        // 삭제
        announcementRepository.delete(announcement);
    }
}
