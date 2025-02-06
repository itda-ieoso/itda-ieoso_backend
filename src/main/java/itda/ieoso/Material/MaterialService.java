package itda.ieoso.Material;

import itda.ieoso.Course.Course;
import itda.ieoso.Course.CourseRepository;
import itda.ieoso.CourseAttendees.CourseAttendees;
import itda.ieoso.CourseAttendees.CourseAttendeesRepository;
import itda.ieoso.CourseAttendees.CourseAttendeesStatus;
import itda.ieoso.Lecture.Lecture;
import itda.ieoso.Lecture.LectureRepository;
import itda.ieoso.MaterialHistory.MaterialHistory;
import itda.ieoso.MaterialHistory.MaterialHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MaterialService {

    private final MaterialRepository materialRepository;
    private final CourseRepository courseRepository;
    private final LectureRepository lectureRepository;
    private final CourseAttendeesRepository courseAttendeesRepository;
    private final MaterialHistoryRepository materialHistoryRepository;

    // material 생성
    @Transactional
    public MaterialDto.Response createMaterial(Long courseId, Long lectureId, Long userId, MaterialDto.createRequest request) {
        // course 조회
        Course course = courseRepository.findById(courseId)
                .orElseThrow(()-> new IllegalArgumentException("course를 찾을수없습니다."));

        // lecture 조회
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(()-> new IllegalArgumentException("lecture를 찾을수없습니다."));

        // 권한 검증
        if (!course.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("강의 개설자가 아닙니다.");
        }

        // material 생성
        Material material = Material.builder()
                .course(course)
                .lecture(lecture)
                .materialTitle(request.materialTitle())
                .materialFile(request.materialFile())
                .materialHistories(new ArrayList<>())
                .build();

        // material 저장
        materialRepository.save(material);

        // materialHistory 생성
        addMaterialHistoryToMaterial(course,material);

        // 반환
        MaterialDto.Response response = MaterialDto.Response.builder()
                .materialId(material.getMaterialId())
                .materialTitle(material.getMaterialTitle())
                .materialFile(material.getMaterialFile())
                .build();

        return response;
    }

    // material 업데이트
    @Transactional
    public MaterialDto.Response updateMaterial(Long courseId, Long materialId, Long userId, MaterialDto.updateRequest request) {
        // course 조회
        Course course = courseRepository.findById(courseId)
                .orElseThrow(()-> new IllegalArgumentException("강좌를 찾을수없습니다."));

        // 권한 검증
        if (!course.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("강의 개설자가 아닙니다.");
        }

        // material 조회
        Material material = materialRepository.findByCourseAndMaterialId(course, materialId);
        if (material == null) {
            throw new IllegalArgumentException("material를 찾을수없습니다.");
        }

        // material 수정
        if (request.materialTitle()!=null) material.setMaterialTitle(request.materialTitle());
        if (request.materialFile()!=null) material.setMaterialFile(request.materialFile());
        materialRepository.save(material);

        // 반환
        MaterialDto.Response response = MaterialDto.Response.builder()
                .materialId(material.getMaterialId())
                .materialTitle(material.getMaterialTitle())
                .materialFile(material.getMaterialFile())
                .build();

        return response;

    }

    // material 삭제
    public MaterialDto.deleteResponse deleteMaterial(Long courseId, Long materialId, Long userId) {
        // course 조회
        Course course = courseRepository.findById(courseId)
                .orElseThrow(()-> new IllegalArgumentException("강좌를 찾을수없습니다."));

        // 권한 검증
        if (!course.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("강의 개설자가 아닙니다.");
        }

        // material 조회
        Material material = materialRepository.findByCourseAndMaterialId(course, materialId);
        if (material == null) {
            throw new IllegalArgumentException("material을 찾을수없습니다.");
        }

        // materialHistory 삭제
        materialHistoryRepository.deleteAllByMaterial(material);

        // material 삭제
        materialRepository.delete(material);

        // 반환
        MaterialDto.deleteResponse response = MaterialDto.deleteResponse.builder()
                .videoId(materialId)
                .message("material 삭제 완료")
                .build();
        return response;

    }

    // video 조회
    public void getMaterial() {

    }

    // video 목록 조회
    public void getMaterials() {

    }

    private void addMaterialHistoryToMaterial(Course course, Material material) {
        // course내의 모든 courseAttendees 조회
        List<CourseAttendees> attendees = courseAttendeesRepository.findAllByCourse(course);

        // history 생성
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
        materialRepository.save(material);

    }
}
