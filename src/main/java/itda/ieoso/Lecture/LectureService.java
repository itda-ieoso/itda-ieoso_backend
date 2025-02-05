package itda.ieoso.Lecture;

import itda.ieoso.Assignment.Assignment;
import itda.ieoso.Assignment.AssignmentRepository;
import itda.ieoso.Course.Course;
import itda.ieoso.Course.CourseRepository;
import itda.ieoso.CourseAttendees.CourseAttendees;
import itda.ieoso.CourseAttendees.CourseAttendeesRepository;
import itda.ieoso.CourseAttendees.CourseAttendeesStatus;
import itda.ieoso.Lecture.CurriculumModificationRequest.ModifyRequestDto;
import itda.ieoso.Lecture.CurriculumResponseDto.AssignmentResponseDto;
import itda.ieoso.Lecture.CurriculumResponseDto.MaterialResponseDto;
import itda.ieoso.Lecture.CurriculumResponseDto.VideoResponseDto;
import itda.ieoso.Material.Material;
import itda.ieoso.Material.MaterialHistory;
import itda.ieoso.Material.MaterialHistoryRepository;
import itda.ieoso.Material.MaterialRepository;
import itda.ieoso.Submission.Submission;
import itda.ieoso.Submission.SubmissionRepository;
import itda.ieoso.Submission.SubmissionStatus;
import itda.ieoso.User.User;
import itda.ieoso.User.UserRepository;
import itda.ieoso.Video.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static itda.ieoso.Lecture.CurriculumDto.*;

@Service
@RequiredArgsConstructor
public class LectureService {
    private final CourseRepository courseRepository;
    private final LectureRepository lectureRepository;
    private final MaterialRepository materialRepository;
    private final AssignmentRepository assignmentRepository;
    private final VideoRepository videoRepository;
    private final CourseAttendeesRepository courseAttendeesRepository;
    private final VideoHistoryRepository videoHistoryRepository;
    private final MaterialHistoryRepository materialHistoryRepository;
    private final SubmissionRepository submissionRepository;
    private final UserRepository userRepository;


    // 강의 생성
    @Transactional
    public LectureDTO createLecture(Long courseId, Long userId, Lecture dto) {
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
                .lectureTitle(dto.getLectureTitle())
                .lectureDescription(dto.getLectureDescription())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .videos(new ArrayList<>())
                .materials(new ArrayList<>())
                .assignments(new ArrayList<>())
                .build();

        lecture.setCreatedAt(LocalDateTime.now());
        lecture.setUpdatedAt(LocalDateTime.now()); // 처음 생성 시 updatedAt도 현재 시간

        lectureRepository.save(lecture); // 저장 후 반환

        return LectureDTO.of(lecture);
    }

    // 강의 수정
    @Transactional
    public LectureDTO updateLecture(Long courseId, Long lectureId, Long userId, Lecture dto) {
        // 기존 강의 조회
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new RuntimeException("강의를 찾을 수 없습니다"));

        // 강의를 속한 과정의 생성자 ID와 요청한 사용자 ID가 일치하는지 확인
        if (!lecture.getCourse().getUser().getUserId().equals(userId)) {
            throw new RuntimeException("이 강의를 수정할 권한이 없습니다.");
        }

        // 기존 객체 수정 (새로 객체를 생성하지 않고 덮어씀)
        lecture.setLectureTitle(dto.getLectureTitle());
        lecture.setLectureDescription(dto.getLectureDescription());
        lecture.setStartDate(dto.getStartDate());
        lecture.setEndDate(dto.getEndDate());
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

    // 강의 조회
    public LectureDTO getLecture(Long courseId, Long lectureId, Long userId) {
        // 과정 참여자인지 확인
        if (!isCourseAttendee(courseId, userId)) {
            throw new IllegalArgumentException("과정에 참여한 사용자만 강의를 조회할 수 있습니다.");
        }

        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(()-> new IllegalArgumentException("강좌가 없습니다."));

        // 강의 반환
        LectureDTO lectureDTO = LectureDTO.of(lecture);
        return lectureDTO;
    }

    // 강의 목록 조회
    public List<LectureDTO> getLectureList(Long courseId, Long userId) {
        // 과정 참여자인지 확인
        if (!isCourseAttendee(courseId, userId)) {
            throw new IllegalArgumentException("과정에 참여한 사용자만 강의를 조회할 수 있습니다.");
        }

        // 강의 리스트 반환
        List<Lecture> lectureList = lectureRepository.findAllByCourse_CourseId(courseId);
        List<LectureDTO> lectureDTOList = lectureList.stream()
                .map(LectureDTO::of).collect(Collectors.toList());
        return lectureDTOList;

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

    // ------------------------------------------------------
    // 강의실 커리큘럼 전체 생성
    @Transactional
    public CurriculumModificationRequest createCurriculum(Long userId, Long courseId, CurriculumModificationRequest request) {

        if (!isCourseCreator(courseId, userId)) {
            throw new IllegalArgumentException("잘못된 접근");
        }

        // 생성
        Course course = courseRepository.findById(courseId).orElseThrow();

        if (request.getCurriculumDtos()!=null || !request.getCurriculumDtos().isEmpty()) {
            List<CurriculumDto> curriculumDtos = request.getCurriculumDtos();
            List<Lecture> lectures = curriculumDtos.stream()
                    .map(dto -> {
                        // TODO createLecture()로 변경
                        Lecture lecture = Lecture.builder()
                                .course(course)
                                .lectureTitle(dto.getLectureTitle())
                                .lectureDescription(dto.getLectureDescription())
                                .startDate(dto.getStartDate())
                                .endDate(dto.getEndDate())
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .videos(new ArrayList<>())
                                .materials(new ArrayList<>())
                                .assignments(new ArrayList<>())
                                .build();

                        if (dto.getVideos()!=null || !dto.getVideos().isEmpty()) {
                            lecture.getVideos().addAll(createVideo(dto.getVideos(), course, lecture));
                        }

                        if (dto.getAssignments()!=null || !dto.getAssignments().isEmpty()) {
                            lecture.getAssignments().addAll(createAssignment(dto.getAssignments(),course, lecture));
                        }

                        if (dto.getMaterials()!=null || !dto.getMaterials().isEmpty()) {
                            lecture.getMaterials().addAll(createMaterial(dto.getMaterials(),course,lecture));
                        }

                        return lecture;

                    })
                    .collect(Collectors.toList());

            lectureRepository.saveAll(lectures);

        }

        // 추가, 수정, 삭제
        if (request.getModifyRequestDto() != null || !request.getModifyRequestDto().isEmpty()) {
            List<ModifyRequestDto> modifyRequestDtos = request.getModifyRequestDto();
            modifyRequestDtos.forEach(modifyRequestDto -> {
                // 추가(렉처아이디 & 부자재정보) -> 추가는 부자재 only
                if (modifyRequestDto.getAction().equals("add")) {
                    addRequest(modifyRequestDto, course);
                }

                // 수정(본인아이디, 수정하고픈정보) -> 렉쳐, 부자재 모두 가능
                if (modifyRequestDto.getAction().equals("update")) {
                    updateRequest(modifyRequestDto);
                }

                // 삭제(본인아이디) -> 랙쳐, 부자재 모두가능
                if (modifyRequestDto.getAction().equals("delete")) {
                    deleteRequest(modifyRequestDto);

                }
            });

        }


        return request;
    }


    // video 생성
    private List<Video> createVideo(List<VideoDto> videos, Course course, Lecture lecture) {
        List<Video> videoList = new ArrayList<>();
        for (VideoDto videoDto : videos) {
            Video video = Video.builder()
                    .course(lecture.getCourse())
                    .lecture(lecture)
                    .videoTitle(videoDto.getVideoTitle())
                    .videoUrl(videoDto.getVideoUrl())
                    .startDate(videoDto.getStartDate())
                    .endDate(videoDto.getEndDate())
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

            // videoList에 video추가
            videoList.add(video);
        }

        return videoList;
    }

    // material 생성
    private List<Material> createMaterial(List<MaterialDto> materials,Course course, Lecture lecture) {
        List<Material> materialList = new ArrayList<>();
        for (MaterialDto materialDto : materials) {
            Material material = Material.builder()
                    .course(lecture.getCourse())
                    .lecture(lecture)
                    .materialTitle(materialDto.getMaterialTitle())
                    .materialFile(materialDto.getMaterialFile())
                    .materialHistories(new ArrayList<>())
                    .build();

            // material에 대한 모든 attendees의 materialHistory 생성
            List<CourseAttendees> attendees = courseAttendeesRepository.findAllByCourse(course);
            List<MaterialHistory> materialHistoryList = attendees.stream()
                    .filter(attendee -> attendee.getCourseAttendeesStatus()== CourseAttendeesStatus.ACTIVE)
                    .map(attendee -> MaterialHistory.builder()
                            .course(course)
                            .material(material)
                            .courseAttendees(attendee)
                            .materialHistoryStatus(false)
                            .build())
                    .collect(Collectors.toList());

            // material에 materialHistory추가
            material.getMaterialHistories().addAll(materialHistoryList);

            // materialList에 material추가
            materialList.add(material);
        }
        return materialList;
    }

    // assignment 생성
    private List<Assignment> createAssignment(List<AssignmentDto> assignments,Course course, Lecture lecture) {
        List<Assignment> assignmentList = new ArrayList<>();
        for (AssignmentDto assignmentDto : assignments) {
            Assignment assignment = Assignment.builder()
                    .course(lecture.getCourse())
                    .lecture(lecture)
                    .assignmentTitle(assignmentDto.getAssignmentTitle())
                    .assignmentDescription(assignmentDto.getAssignmentDescription())
                    .startDate(assignmentDto.getStartDate())
                    .endDate(assignmentDto.getEndDate())
                    .submissions(new ArrayList<>())
                    .build();

            // assignment에 대한 모든 attendees의 submission 생성
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

            // assignmentList에 assignment 추가
            assignmentList.add(assignment);

        }
        return assignmentList;
    }

    // lecture에 material, assignment, video 추가
    // FIXME 날짜입력 추가
    private void addRequest(ModifyRequestDto modifyRequestDto, Course course) {

        Lecture lecture = lectureRepository.findById(modifyRequestDto.getId()).orElseThrow();

        if (modifyRequestDto.getType().equals("material")) {

            // material 추가
            Material material = Material.builder()
                    .course(course)
                    .lecture(lecture)
                    .materialTitle(modifyRequestDto.getTitle())
                    .materialFile(modifyRequestDto.getItem())
                    .build();

            materialRepository.save(material);

            // material에 대한 모든 attendees의 materialHistory 생성
            List<CourseAttendees> attendees = courseAttendeesRepository.findAllByCourse(course);
            List<MaterialHistory> materialHistoryList = attendees.stream()
                    .filter(attendee -> attendee.getCourseAttendeesStatus()== CourseAttendeesStatus.ACTIVE)
                    .map(attendee -> MaterialHistory.builder()
                            .course(course)
                            .material(material)
                            .courseAttendees(attendee)
                            .materialHistoryStatus(false)
                            .build())
                    .collect(Collectors.toList());
            materialHistoryRepository.saveAll(materialHistoryList);

        }

        if (modifyRequestDto.getType().equals("assignment")) {

            // assignment 추가
            Assignment assignment = Assignment.builder()
                    .course(course)
                    .lecture(lecture)
                    .assignmentTitle(modifyRequestDto.getTitle())
                    .assignmentDescription(modifyRequestDto.getItem())
                    .startDate(modifyRequestDto.getStartDate())
                    .endDate(modifyRequestDto.getEndDate())
                    .build();

            assignmentRepository.save(assignment);

            // assignment에 대한 모든 attendees의 submission 생성
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
            submissionRepository.saveAll(submissionList);
        }

        if (modifyRequestDto.getType().equals("video")) {

            // video 추가
            Video video = Video.builder()
                    .course(course)
                    .lecture(lecture)
                    .videoTitle(modifyRequestDto.getTitle())
                    .videoUrl(modifyRequestDto.getItem())
                    .startDate(modifyRequestDto.getStartDate())
                    .endDate(modifyRequestDto.getEndDate())
                    .build();

            videoRepository.save(video);

            // video에 대한 모든 attendees의 videoHistory 생성
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
            videoHistoryRepository.saveAll(videoHistoryList);
        }
    }

    // lecture, material, assignment, video 업데이트
    private void updateRequest(ModifyRequestDto modifyRequestDto) {

        if (modifyRequestDto.getType().equals("lecture")) {
            Lecture lecture = lectureRepository.findById(modifyRequestDto.getId()).orElse(null);
            // 수정(전체 데이터 덮어쓰기 / 수정안했으면 기존거 그대로 가져오기)
//            lecture.setLectureTitle(modifyRequestDto.getTitle());
//            lecture.setLectureDescription(modifyRequestDto.getItem());
//            lecture.setStartDate(modifyRequestDto.getStartDate());
//            lecture.setEndDate(modifyRequestDto.getEndDate());
//            lecture.setUpdatedAt(LocalDateTime.now());
            lectureRepository.save(lecture);
        }

        if (modifyRequestDto.getType().equals("material")) {
            Material material = materialRepository.findById(modifyRequestDto.getId()).orElseThrow();
            // 수정
            material.setMaterialTitle(modifyRequestDto.getTitle());
            material.setMaterialFile(modifyRequestDto.getItem());
            materialRepository.save(material);
        }

        if (modifyRequestDto.getType().equals("assignment")) {
            Assignment assignment = assignmentRepository.findById(modifyRequestDto.getId()).orElseThrow();
            // 수정
            assignment.setAssignmentTitle(modifyRequestDto.getTitle());
            assignment.setAssignmentDescription(modifyRequestDto.getItem());
            assignment.setStartDate(modifyRequestDto.getStartDate());
            assignment.setEndDate(modifyRequestDto.getEndDate());
            assignment.setUpdatedAt(LocalDateTime.now());
            assignmentRepository.save(assignment);
        }

        if (modifyRequestDto.getType().equals("video")) {
            Video video = videoRepository.findById(modifyRequestDto.getId()).orElseThrow();
            // 수정
            video.setVideoTitle(modifyRequestDto.getTitle());
            video.setVideoUrl(modifyRequestDto.getItem());
            video.setStartDate(modifyRequestDto.getStartDate());
            video.setEndDate(modifyRequestDto.getEndDate());
            videoRepository.save(video);
        }
    }

    // material, assignment, video 삭제 TODO lecture삭제추가
    private void deleteRequest(ModifyRequestDto modifyRequestDto) {
        if (modifyRequestDto.getType().equals("material")) {
            // materialid가 getId인 materialHistory 전체삭제
            materialHistoryRepository.deleteAllByMaterialId(modifyRequestDto.getId());

            // material 삭제
            materialRepository.deleteById(modifyRequestDto.getId());

        }

        if (modifyRequestDto.getType().equals("assignment")) {
            // assignmentid가 getId인 submission 전체삭제
            submissionRepository.deleteAllByAssignmentId(modifyRequestDto.getId());

            // assignment삭제
            assignmentRepository.deleteById(modifyRequestDto.getId());
        }

        if (modifyRequestDto.getType().equals("video")) {
            // videoid가 getId인 videoHistory 전체삭제
            videoHistoryRepository.deleteAllByVideoId(modifyRequestDto.getId());

            // video삭제
            videoRepository.deleteById(modifyRequestDto.getId());
        }
    }

    // 커리큘럼 조회
    @Transactional
    public List<CurriculumResponseDto> getCurriculum(Long userId, Long courseId) {

        // 과정 찾기
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("해당 과정이 존재하지 않습니다."));

        // 강의 수강생인지 확인
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new IllegalArgumentException("유효한 사용자가 아닙니다."));

        // courseAttendees 찾기
        CourseAttendees courseAttendees = courseAttendeesRepository.findByCourseAndUser(course,user)
                .orElseThrow(()-> new IllegalArgumentException("수강생이 아닙니다."));

        // lecture 조회
        List<Lecture> lectureList = lectureRepository.findAllByCourse(course);

        return lectureList.stream().map(lecture -> {
            List<VideoResponseDto> videos = lecture.getVideos().stream()
                    .map(video -> {
                        VideoHistory videoHistory=videoHistoryRepository.findByVideoAndCourseAttendees(video, courseAttendees);
                        if (videoHistory == null) {
                            throw new IllegalArgumentException("잘못된접근");
                        }
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
                    .map(material -> {
                        MaterialHistory materialHistory = materialHistoryRepository.findByMaterialAndCourseAttendees(material, courseAttendees);
                        if (materialHistory == null) {
                            throw new IllegalArgumentException("잘못된접근");
                        }

                        return MaterialResponseDto.builder()
                                .materialId(material.getMaterialId())
                                .materialTitle(material.getMaterialTitle())
                                .materialFile(material.getMaterialFile())
                                .materialHistoryStatus(materialHistory.isMaterialHistoryStatus())
                                .build();
                    }).toList();


            List<AssignmentResponseDto> assignments = lecture.getAssignments().stream()
                    .map(assignment -> {
                        Submission submission = submissionRepository.findByAssignmentAndCourseAttendees(assignment,courseAttendees);
                        if (submission == null) {
                            throw new IllegalArgumentException("잘못된접근");
                        }
                        return AssignmentResponseDto.builder()
                                .assignmentId(assignment.getAssignmentId())
                                .assignmentTitle(assignment.getAssignmentTitle())
                                .assignmentDescription(assignment.getAssignmentDescription())
                                .startDate(assignment.getStartDate())
                                .endDate(assignment.getEndDate())
                                .submissionStatus(submission.getSubmissionStatus())
                                .build();
                    }).toList();

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
        }).collect(Collectors.toList());

    }

    @Transactional
    public List<CurriculumResponseDto> getToDoList(Long courseId, Long userId, LocalDate date) {
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
                curriculum.getMaterials().isEmpty() &&
                curriculum.getAssignments().isEmpty()))
        .collect(Collectors.toList());
    }

    // TODO 일주일전체 조회 추가

}



// video, material, assignment 추가 (lectureid, 본문내용)
// video에대한 모든 attendees의 videoHistory 생성
// vidoe에 videoHistory추가

// course입장
// 모든lecture의 video, material, assignment에 대한 history 생성





// 히스토리 생성 시점
// 1. 커리큘럼 추가시               -> 부자재별로 모든 attendees의 해당부자재의 히스토리 생성
// 2. 사람유입시점                 -> course의 모든 강의에대해 attendees한명의 히스토리 전체 생성

// 히스토리 삭제 시점
// 1. 커리큘럼 삭제시                  -> 부자재별로 모든 attendees의 해당 부자재의 히스토리 삭제
// 2. course 유저한명이 퇴장하는 시점    -> course에 모든 강의에대해 attendees한명의 히스토리 전체삭제 -> 중도포기자 포함? 수강생통계에 보여짐
// 3. 수료되는 시점                   -> course의 모든 강의에대해 모든 attendees의 모든강의 히스토리 삭제 -> 남기기

// 커리큘럼 추가시
// 1. courseid가 엔드포인트의 courseid인 attendees 모두 불러오기
// 2. for문 돌면서 전부 videoHistory 생성

// 강의실 입장시
// 1. attendees 생성
// 2. courseid에 해당하는 모든 video, assignment, material 조회 -> 리스트
// 3. for문 돌면서 video한개에 대해 videoHistory에 couserId가 입장한 강의실이고, videoid값이 2번인것 있는지 검증
// 4. 없으면 생성

// 생성 -> 검증필요없음 새롭게 생겨난데이터니깐
// 입장 -> 기존데이터에 대해 생성하는거니깐 검증필요