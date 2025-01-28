package itda.ieoso.Lecture;

import itda.ieoso.Assignment.Assignment;
import itda.ieoso.Assignment.AssignmentRepository;
import itda.ieoso.Course.Course;
import itda.ieoso.Course.CourseRepository;
import itda.ieoso.CourseAttendees.CourseAttendeesRepository;
import itda.ieoso.Lecture.CurriculumModificationRequest.ModifyRequestDto;
import itda.ieoso.Material.Material;
import itda.ieoso.Material.MaterialRepository;
import itda.ieoso.Video.Video;
import itda.ieoso.Video.VideoRepository;
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
public class LectureService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private LectureRepository lectureRepository;

    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private AssignmentRepository assignmentRepository;

   @Autowired
   private VideoRepository videoRepository;


    // 강의 생성
    public LectureDTO createLecture(Long courseId, Long userId, String title, String description, LocalDate startDate, LocalDate endDate) {
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
                .startDate(startDate)
                .endDate(endDate)
                .build();

        lecture.setCreatedAt(LocalDateTime.now());
        lecture.setUpdatedAt(LocalDateTime.now()); // 처음 생성 시 updatedAt도 현재 시간

        lectureRepository.save(lecture); // 저장 후 반환

        return LectureDTO.of(lecture);
    }

    // 강의 수정
    public LectureDTO updateLecture(Long courseId, Long lectureId, Long userId, String lectureTitle, String lectureDescription, LocalDate startDate, LocalDate endDate) {
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

    // ------------------------------------------------------
    @Transactional
    public List<CurriculumDto> createCurriculum(Long userId, Long courseId, List<CurriculumDto> curriculumDtos) {
        if (!isCourseCreator(courseId, userId)) {
            throw new IllegalArgumentException("잘못된 사용자");
        }

        // 강좌 불러오기
        Course course = courseRepository.findById(courseId)
                .orElseThrow(()-> new IllegalArgumentException("강좌를 찾을수없음"));

        // 강의 생성(챕터)
        List<Lecture> lectures = curriculumDtos.stream()
                .map(dto -> {

                    Lecture lecture = Lecture.builder()
                            .course(course)
                            .lectureTitle(dto.getLectureTitle())
                            .lectureDescription(dto.getLectureDescription())
                            .startDate(LocalDate.now())
                            .endDate(LocalDate.now())
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .videos(new ArrayList<>())
                            .materials(new ArrayList<>())
                            .assignments(new ArrayList<>())
                            .build();

                    // vidoe 저장
                    List<Video> videos = dto.getVideos().stream()
                            .map(videoDto -> {Video video = Video.builder()
                                    .videoTitle(videoDto.getVideoTitle())
                                    .videoUrl(videoDto.getVideoUrl())
                                    .lecture(lecture)
                                    .build();
                                return video;
                            })
                            .collect(Collectors.toList());

                    // TODO 나중에 관리를 위해 양방향 매핑시 lecture 쪽에도 추가

                    // material 저장
                    List<Material> materials = dto.getMaterials().stream()
                            .map(materialDto -> {Material material = Material.builder()
                                    .materialTitle(materialDto.getMaterialTitle())
                                    .materialFile(materialDto.getMaterialFile())
                                    .lecture(lecture)
                                    .build();
                                return material;
                            })
                            .collect(Collectors.toList());

                    // assignment 저장
                    List<Assignment> assignments = dto.getAssignments().stream()
                            .map(assignmentDto -> {Assignment assignment = Assignment.builder()
                                    .assignmentTitle(assignmentDto.getAssignmentTitle())
                                    .assignmentDescription(assignmentDto.getAssignmentDescription())
                                    .startDate(assignmentDto.getStartDate())
                                    .endDate(assignmentDto.getEndDate())
                                    .createdAt(LocalDate.now())
                                    .updatedAt(LocalDate.now())
                                    .lecture(lecture)
                                    .build();
                                return assignment;
                            })
                            .collect(Collectors.toList());

                    // Lecture 객체에 연관된 Video, Material, Assignment 설정
                    lecture.getVideos().addAll(videos); // video 추가
                    lecture.getMaterials().addAll(materials); // material 추가
                    lecture.getAssignments().addAll(assignments); // assignment 추가

                    return lecture;
                })
                .collect(Collectors.toList());

        lectureRepository.saveAll(lectures);

        return curriculumDtos;
    }


    public CurriculumModificationRequest createCuri(Long userId, Long courseId, CurriculumModificationRequest request) {

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
                            lecture.getVideos().addAll(createVideo(dto.getVideos(), lecture));
                        }

                        if (dto.getAssignments()!=null || !dto.getAssignments().isEmpty()) {
                            lecture.getAssignments().addAll(createAssignment(dto.getAssignments(),lecture));
                        }

                        if (dto.getMaterials()!=null || !dto.getMaterials().isEmpty()) {
                            lecture.getMaterials().addAll(createMaterial(dto.getMaterials(),lecture));
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
                // 추가(렉처아이디 & 부자재정보) -> 추가는 부자자 only
                if (modifyRequestDto.getAction().equals("add")) {
                    addRequest(modifyRequestDto);
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

    private List<Video> createVideo(List<VideoDto> videos, Lecture lecture) {
        List<Video> videoList = new ArrayList<>();
        for (VideoDto videoDto : videos) {
            Video video = Video.builder()
                    .course(lecture.getCourse())
                    .lecture(lecture)
                    .videoTitle(videoDto.getVideoTitle())
                    .videoUrl(videoDto.getVideoUrl())
                    .startDate(videoDto.getStartDate())
                    .endDate(videoDto.getEndDate())
                    .build();

            videoList.add(video);
        }

        return videoList;
    }

    private List<Material> createMaterial(List<MaterialDto> materials, Lecture lecture) {
        List<Material> materialList = new ArrayList<>();
        for (MaterialDto materialDto : materials) {
            Material material = Material.builder()
                    .course(lecture.getCourse())
                    .lecture(lecture)
                    .materialTitle(materialDto.getMaterialTitle())
                    .materialFile(materialDto.getMaterialFile())
                    .build();

            materialList.add(material);
        }
        return materialList;
    }

    private List<Assignment> createAssignment(List<AssignmentDto> assignments, Lecture lecture) {
        List<Assignment> assignmentList = new ArrayList<>();
        for (AssignmentDto assignmentDto : assignments) {
            Assignment assignment = Assignment.builder()
                    .course(lecture.getCourse())
                    .lecture(lecture)
                    .assignmentTitle(assignmentDto.getAssignmentTitle())
                    .assignmentDescription(assignmentDto.getAssignmentDescription())
                    .startDate(assignmentDto.getStartDate())
                    .endDate(assignmentDto.getEndDate())
                    .build();

            assignmentList.add(assignment);
        }
        return assignmentList;
    }


    private void addRequest(ModifyRequestDto modifyRequestDto) {

        Lecture lecture = lectureRepository.findById(modifyRequestDto.getId()).orElseThrow();

        if (modifyRequestDto.getType().equals("material")) {
            Material material = Material.builder()
                    .lecture(lecture)
                    .materialTitle(modifyRequestDto.getTitle())
                    .materialFile(modifyRequestDto.getItem())
                    .build();

            materialRepository.save(material);
        }

        if (modifyRequestDto.getType().equals("assignment")) {
            Assignment assignment = Assignment.builder()
                    .lecture(lecture)
                    .assignmentTitle(modifyRequestDto.getTitle())
                    .assignmentDescription(modifyRequestDto.getItem())
                    .startDate(modifyRequestDto.getStartDate())
                    .endDate(modifyRequestDto.getEndDate())
                    .build();

            assignmentRepository.save(assignment);

            // TODO 히스토리 유저 만큼의 submission 생성
        }

        if (modifyRequestDto.getType().equals("video")) {
            Video video = Video.builder()
                    .lecture(lecture)
                    .videoTitle(modifyRequestDto.getTitle())
                    .videoUrl(modifyRequestDto.getItem())
                    .startDate(modifyRequestDto.getStartDate())
                    .endDate(modifyRequestDto.getEndDate())
                    .build();

            videoRepository.save(video);
        }
    }

    private void updateRequest(ModifyRequestDto modifyRequestDto) {

        if (modifyRequestDto.getType().equals("lecture")) {
            Lecture lecture = lectureRepository.findById(modifyRequestDto.getId()).orElse(null);
            // 수정(전체 데이터 받아오기 수정안했으면 기존거 그대로 가져오기
            lecture.setLectureTitle(modifyRequestDto.getTitle());
            lecture.setLectureDescription(modifyRequestDto.getItem());
            lecture.setStartDate(modifyRequestDto.getStartDate());
            lecture.setEndDate(modifyRequestDto.getEndDate());
            lecture.setUpdatedAt(LocalDateTime.now());
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
            assignment.setUpdatedAt(LocalDate.now());
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



    private void deleteRequest(ModifyRequestDto modifyRequestDto) {
        if (modifyRequestDto.getType().equals("material")) {
            materialRepository.deleteById(modifyRequestDto.getId());
        }

        if (modifyRequestDto.getType().equals("assignment")) {
            assignmentRepository.deleteById(modifyRequestDto.getId());
        }

        if (modifyRequestDto.getType().equals("video")) {
            videoRepository.deleteById(modifyRequestDto.getId());
        }
    }

}

/**
 * public List<CurriculumDto> createCurriculum(Long userId, Long courseId, List<CurriculumDto> curriculumDtos) {
 *     if (!isCourseCreator(courseId, userId)) {
 *         throw new IllegalArgumentException("잘못된 사용자");
 *     }
 *
 *     // 강좌 불러오기
 *     Course course = courseRepository.findById(courseId)
 *             .orElseThrow(() -> new IllegalArgumentException("강좌를 찾을 수 없음"));
 *
 *     // 강의 생성
 *     List<Lecture> lectures = curriculumDtos.stream()
 *             .map(dto -> createLecture(dto, course))
 *             .collect(Collectors.toList());
 *
 *     lectureRepository.saveAll(lectures);
 *
 *     return curriculumDtos;
 * }
 *
 * private Lecture createLecture(CurriculumDto dto, Course course) {
 *     Lecture lecture = Lecture.builder()
 *             .course(course)
 *             .lectureTitle(dto.getLectureTitle())
 *             .lectureDescription(dto.getLectureDescription())
 *             .startDate(LocalDate.now())
 *             .endDate(LocalDate.now())
 *             .createdAt(LocalDateTime.now())
 *             .updatedAt(LocalDateTime.now())
 *             .build();
 *
 *     // 하위 엔티티 생성
 *     List<Video> videos = createVideos(dto.getVideos(), lecture);
 *     List<Material> materials = createMaterials(dto.getMaterials(), lecture);
 *     List<Assignment> assignments = createAssignments(dto.getAssignments(), lecture);
 *
 *     lecture.setVideos(videos);
 *     lecture.setMaterials(materials);
 *     lecture.setAssignments(assignments);
 *
 *     return lecture;
 * }
 *
 * private List<Video> createVideos(List<CurriculumDto.VideoDto> videoDtos, Lecture lecture) {
 *     return videoDtos.stream()
 *             .map(videoDto -> Video.builder()
 *                     .videoTitle(videoDto.getVideoTitle())
 *                     .videoUrl(videoDto.getVideoUrl())
 *                     .lecture(lecture)
 *                     .build())
 *             .collect(Collectors.toList());
 * }
 *
 * private List<Material> createMaterials(List<CurriculumDto.MaterialDto> materialDtos, Lecture lecture) {
 *     return materialDtos.stream()
 *             .map(materialDto -> Material.builder()
 *                     .materialTitle(materialDto.getMaterialTitle())
 *                     .materialFile(materialDto.getMaterialFile())
 *                     .lecture(lecture)
 *                     .build())
 *             .collect(Collectors.toList());
 * }
 *
 * private List<Assignment> createAssignments(List<CurriculumDto.AssignmentDto> assignmentDtos, Lecture lecture) {
 *     return assignmentDtos.stream()
 *             .map(assignmentDto -> Assignment.builder()
 *                     .assignmentTitle(assignmentDto.getAssignmentTitle())
 *                     .assignmentDescription(assignmentDto.getAssignmentDescription())
 *                     .startDate(assignmentDto.getStartDate())
 *                     .endDate(assignmentDto.getEndDate())
 *                     .createdAt(LocalDate.now())
 *                     .updatedAt(LocalDate.now())
 *                     .lecture(lecture)
 *                     .build())
 *             .collect(Collectors.toList());
 * }
 */