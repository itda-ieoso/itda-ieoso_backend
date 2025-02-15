package itda.ieoso.Video;

import itda.ieoso.ContentOrder.ContentOrderService;
import itda.ieoso.Course.Course;
import itda.ieoso.Course.CourseRepository;
import itda.ieoso.CourseAttendees.CourseAttendees;
import itda.ieoso.CourseAttendees.CourseAttendeesRepository;
import itda.ieoso.CourseAttendees.CourseAttendeesStatus;
import itda.ieoso.Lecture.Lecture;
import itda.ieoso.Lecture.LectureRepository;
import itda.ieoso.VideoHistory.VideoHistory;
import itda.ieoso.VideoHistory.VideoHistoryRepository;
import itda.ieoso.VideoHistory.VideoHistoryStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VideoService {
    private final VideoRepository videoRepository;
    private final CourseRepository courseRepository;
    private final CourseAttendeesRepository courseAttendeesRepository;
    private final LectureRepository lectureRepository;
    private final VideoHistoryRepository videoHistoryRepository;
    private final ContentOrderService contentOrderService;

    // video 생성
    @Transactional
    public VideoDto.Response createVideo(Long courseId, Long lectureId, Long userId, VideoDto.Request request) {
        // course 조회
        Course course = courseRepository.findById(courseId)
                .orElseThrow(()-> new IllegalArgumentException("강좌를 찾을수없습니다."));

        // lecture 조회
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(()-> new IllegalArgumentException("챕터를 찾을수없습니다."));

        // 권한 검증
        if (!course.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("강의 개설자가 아닙니다.");
        }

        // video 생성
        Video video = Video.builder()
                .course(course)
                .lecture(lecture)
                .videoTitle(request.videoTitle())
                .videoUrl(request.videoUrl())
                .startDate(request.startDate())
                .endDate(request.endDate())
                .videoHistories(new ArrayList<>())
                .build();

        //  video 저장
        videoRepository.save(video);

        // contentorder 생성
        contentOrderService.createContentOrder(course, "video", video.getVideoId());

        // videoHistory 생성
        addVideoHistoryToVideo(course,video);

        // 반환
        VideoDto.Response response = VideoDto.Response.of(video);

        return response;
    }

    // video 업데이트
    @Transactional
    public VideoDto.Response updateVideo(Long courseId, Long videoId, Long userId, VideoDto.Request request) {
        // course 조회
        Course course = courseRepository.findById(courseId)
                .orElseThrow(()-> new IllegalArgumentException("강좌를 찾을수없습니다."));

        // 권한 검증
        if (!course.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("강의 개설자가 아닙니다.");
        }

        // video 조회
        Video video = videoRepository.findByCourseAndVideoId(course, videoId);
        if (video == null) {
            throw new IllegalArgumentException("video를 찾을수없습니다.");
        }

        // video 수정
        if (request.videoTitle()!=null) video.setVideoTitle(request.videoTitle());
        if (request.videoUrl()!=null) video.setVideoUrl(request.videoUrl());
        if (request.startDate()!=null) video.setStartDate(request.startDate());
        if (request.endDate()!=null) video.setEndDate(request.endDate());
        videoRepository.save(video);

        // 반환
        VideoDto.Response response = VideoDto.Response.of(video);

        return response;

    }

    // video 삭제
    @Transactional
    public VideoDto.deleteResponse deleteVideo(Long courseId, Long videoId, Long userId) {
        // course 조회
        Course course = courseRepository.findById(courseId)
                .orElseThrow(()-> new IllegalArgumentException("강좌를 찾을수없습니다."));

        // 권한 검증
        if (!course.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("강의 개설자가 아닙니다.");
        }

        // video 조회
        Video video = videoRepository.findByCourseAndVideoId(course, videoId);
        if (video == null) {
            throw new IllegalArgumentException("video를 찾을수없습니다.");
        }

        // videoHistory 삭제 (추후 수정후 삭제)
        videoHistoryRepository.deleteAllByVideo(video);

        // contentOrder 삭제
        contentOrderService.deleteContentOrder(videoId, "video");

        // video 삭제
        videoRepository.delete(video);

        // 반환
        VideoDto.deleteResponse response = VideoDto.deleteResponse.builder()
                .videoId(videoId)
                .message("video 삭제 완료")
                .build();
        return response;

    }

    // video 조회
    public void getVideo() {

    }

    // video 목록 조회
    public void getVideos() {

    }

    // courseAttendees만큼의 videoHistory 생성
    private void addVideoHistoryToVideo(Course course, Video video) {
        // course내의 모든 courseAttendees 조회
        List<CourseAttendees> attendees = courseAttendeesRepository.findAllByCourse(course);

        // history 생성
        List<VideoHistory> videoHistoryList = attendees.stream()
                .filter(attendee -> attendee.getCourseAttendeesStatus() == CourseAttendeesStatus.ACTIVE)
                .map(attendee -> VideoHistory.builder()
                        .course(course)
                        .video(video)
                        .courseAttendees(attendee)
                        .videoHistoryStatus(VideoHistoryStatus.NOT_WATCHED)
                        .build())
                .collect(Collectors.toList());

        // video에 videoHistory추가
        video.getVideoHistories().addAll(videoHistoryList);
        videoRepository.save(video);
    }
}
