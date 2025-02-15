package itda.ieoso.Material;

import itda.ieoso.Course.Course;
import itda.ieoso.Course.CourseRepository;
import itda.ieoso.CourseAttendees.CourseAttendees;
import itda.ieoso.CourseAttendees.CourseAttendeesRepository;
import itda.ieoso.CourseAttendees.CourseAttendeesStatus;
import itda.ieoso.Exception.CustomException;
import itda.ieoso.Exception.ErrorCode;
import itda.ieoso.File.S3Service;
import itda.ieoso.Lecture.Lecture;
import itda.ieoso.Lecture.LectureRepository;
import itda.ieoso.MaterialHistory.MaterialHistory;
import itda.ieoso.MaterialHistory.MaterialHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
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
    private final S3Service s3Service;

    // material 생성
    @Transactional
    public MaterialDto.Response createMaterial(Long courseId, Long lectureId, Long userId, String materialTitle, MultipartFile file) {
        // course 조회
        Course course = courseRepository.findById(courseId)
                .orElseThrow(()-> new CustomException(ErrorCode.COURSE_NOT_FOUND));

        // lecture 조회
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(()-> new CustomException(ErrorCode.LECTURE_NOT_FOUND));

        // 권한 검증
        if (!course.getUser().getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.COURSE_PERMISSION_DENIED);
        }

        // 파일 업로드
        String fileUrl = null;
        if (file != null && !file.isEmpty()) {
            try {
                File convertedFile = s3Service.convertMultipartFileToFile(file);
                fileUrl = s3Service.uploadFile("materials", file.getOriginalFilename(), convertedFile);
            } catch (IOException e) {
                throw new CustomException(ErrorCode.FILE_UPLOAD_FAILED);
            }
        }

        // material 생성
        Material material = Material.builder()
                .course(course)
                .lecture(lecture)
                .materialTitle(materialTitle)
                .materialFile(fileUrl)
                .materialHistories(new ArrayList<>())
                .build();

        // material 저장
        materialRepository.save(material);

        // materialHistory 생성
        addMaterialHistoryToMaterial(course, material);

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
    public MaterialDto.Response updateMaterial(Long courseId, Long materialId, Long userId, String materialTitle, MultipartFile file) {
        // course 조회
        Course course = courseRepository.findById(courseId)
                .orElseThrow(()-> new CustomException(ErrorCode.COURSE_NOT_FOUND));

        // 권한 검증
        if (!course.getUser().getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.COURSE_PERMISSION_DENIED);
        }

        // material 조회
        Material material = materialRepository.findByCourseAndMaterialId(course, materialId);
        if (material == null) {
            throw new CustomException(ErrorCode.MATERIAL_NOT_FOUND);
        }

        // 파일 업로드 (새 파일이 있으면 업로드 후 기존 파일 대체)
        String fileUrl = material.getMaterialFile();
        if (file != null && !file.isEmpty()) {
            try {
                File convertedFile = s3Service.convertMultipartFileToFile(file);
                fileUrl = s3Service.uploadFile("materials", file.getOriginalFilename(), convertedFile);
            } catch (IOException e) {
                throw new CustomException(ErrorCode.FILE_UPLOAD_FAILED);
            }
        }

        // material 수정
        if (materialTitle!=null) material.setMaterialTitle(materialTitle);
        material.setMaterialFile(fileUrl);
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
    @Transactional
    public MaterialDto.deleteResponse deleteMaterial(Long courseId, Long materialId, Long userId) {
        // course 조회
        Course course = courseRepository.findById(courseId)
                .orElseThrow(()-> new CustomException(ErrorCode.COURSE_NOT_FOUND));

        // 권한 검증
        if (!course.getUser().getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.COURSE_PERMISSION_DENIED);
        }

        // material 조회
        Material material = materialRepository.findByCourseAndMaterialId(course, materialId);
        if (material == null) {
            throw new CustomException(ErrorCode.MATERIAL_NOT_FOUND);
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
