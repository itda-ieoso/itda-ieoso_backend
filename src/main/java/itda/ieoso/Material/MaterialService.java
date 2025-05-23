package itda.ieoso.Material;

import itda.ieoso.ContentOrder.ContentOrder;
import itda.ieoso.ContentOrder.ContentOrderService;
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
import itda.ieoso.User.User;
import itda.ieoso.User.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
    private final ContentOrderService contentOrderService;
    private final UserRepository userRepository;

    // SecurityContext에서 현재 사용자 조회
    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // 현재 로그인한 사용자의 이메일 가져오기
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    // material 생성
    @Transactional
    public MaterialDto.Response createMaterial(Long courseId, Long lectureId, Long userId) {

        User authenticatedUser = getAuthenticatedUser();
        // course 조회
        Course course = courseRepository.findById(courseId)
                .orElseThrow(()-> new CustomException(ErrorCode.COURSE_NOT_FOUND));

        // lecture 조회
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(()-> new CustomException(ErrorCode.LECTURE_NOT_FOUND));

        // 권한 검증
        if (!course.getUser().getUserId().equals(authenticatedUser.getUserId())) {
            throw new CustomException(ErrorCode.COURSE_PERMISSION_DENIED);
        }

        // 파일 업로드
//        String fileUrl = null;
//        String originalFilename = null;
//        String fileSize = null;
//
//        if (file != null && !file.isEmpty()) {
//            try {
//                File convertedFile = s3Service.convertMultipartFileToFile(file);
//                fileUrl = s3Service.uploadFile("materials", file.getOriginalFilename(), convertedFile);
//                originalFilename = file.getOriginalFilename();
//                fileSize = formatFileSize(file.getSize());
//            } catch (IOException e) {
//                throw new CustomException(ErrorCode.FILE_UPLOAD_FAILED);
//            }
//        }
//
//        LocalDate endDate = course.getEndDate();
//
//        if (startDate.toLocalDate().isBefore(course.getStartDate())) {
//            throw new CustomException(ErrorCode.INVALID_DATE_RANGE);
//        }

        // material 생성
        Material material = Material.builder()
                .course(course)
                .lecture(lecture)
                .materialTitle(null)
                .materialFile(null)
                .originalFilename(null)
                .fileSize(null)
                .startDate(null)
                .endDate(LocalDateTime.of(course.getEndDate(), LocalTime.of(23, 59, 59)))
                .materialHistories(new ArrayList<>())
                .build();

        // material 저장
        materialRepository.save(material);

        // contentOrder 생성
        ContentOrder contentOrder = contentOrderService.createContentOrder(course, lecture,"material", material.getMaterialId());

        // materialHistory 생성
        addMaterialHistoryToMaterial(course, material);

        // 반환
        MaterialDto.Response response = MaterialDto.Response.of(material,contentOrder);

        return response;
    }

    // material 업데이트
    @Transactional
    public MaterialDto.Response updateMaterial(Long courseId, Long materialId, Long userId, String materialTitle, MultipartFile file, LocalDateTime startDate) {
        User authenticatedUser = getAuthenticatedUser();
        // course 조회
        Course course = courseRepository.findById(courseId)
                .orElseThrow(()-> new CustomException(ErrorCode.COURSE_NOT_FOUND));

        // 권한 검증
        if (!course.getUser().getUserId().equals(authenticatedUser.getUserId())) {
            throw new CustomException(ErrorCode.COURSE_PERMISSION_DENIED);
        }

        // material 조회
        Material material = materialRepository.findByCourseAndMaterialId(course, materialId);
        if (material == null) {
            throw new CustomException(ErrorCode.MATERIAL_NOT_FOUND);
        }

        // 파일 업로드 (새 파일이 있으면 업로드 후 기존 파일 대체)
        String fileUrl = material.getMaterialFile();
        String originalFilename = material.getOriginalFilename();
        String fileSize = material.getFileSize();

        if (file != null && !file.isEmpty()) {
            try {
                File convertedFile = s3Service.convertMultipartFileToFile(file);
                fileUrl = s3Service.uploadFile("materials", file.getOriginalFilename(), convertedFile);
                originalFilename = file.getOriginalFilename();
                fileSize = formatFileSize(file.getSize());
            } catch (IOException e) {
                throw new CustomException(ErrorCode.FILE_UPLOAD_FAILED);
            }
        }

        // material 수정
        if (materialTitle!=null) material.setMaterialTitle(materialTitle);
        material.setMaterialFile(fileUrl);
        material.setOriginalFilename(originalFilename);
        material.setFileSize(fileSize);

//        if (startDate != null) {
//            if (startDate.toLocalDate().isBefore(course.getStartDate())) {
//                throw new CustomException(ErrorCode.INVALID_DATE_RANGE);
//            }
//
//        }
        if (startDate != null) material.setStartDate(startDate);

        materialRepository.save(material);

        // 반환
        MaterialDto.Response response = MaterialDto.Response.of(material);

        return response;

    }

    private String formatFileSize(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1048576) { // 1KB 이상
            return String.format("%.2f KB", bytes / 1024.0);
        } else if (bytes < 1073741824) { // 1MB 이상
            return String.format("%.2f MB", bytes / 1048576.0);
        } else { // 1GB 이상
            return String.format("%.2f GB", bytes / 1073741824.0);
        }
    }

    // material 삭제
    @Transactional
    public MaterialDto.deleteResponse deleteMaterial(Long courseId, Long materialId, Long userId) {
        User authenticatedUser = getAuthenticatedUser();
        // course 조회
        Course course = courseRepository.findById(courseId)
                .orElseThrow(()-> new CustomException(ErrorCode.COURSE_NOT_FOUND));

        // 권한 검증
        if (!course.getUser().getUserId().equals(authenticatedUser.getUserId())) {
            throw new CustomException(ErrorCode.COURSE_PERMISSION_DENIED);
        }

        // material 조회
        Material material = materialRepository.findByCourseAndMaterialId(course, materialId);
        if (material == null) {
            throw new CustomException(ErrorCode.MATERIAL_NOT_FOUND);
        }

        String fileUrl = material.getMaterialFile();


        if (fileUrl != null && !fileUrl.isBlank()) {
            s3Service.moveFileToDeleteFolder(fileUrl);
        }

        // materialHistory 삭제
        materialHistoryRepository.deleteAllByMaterial(material);

        // contentOrder 삭제
        contentOrderService.deleteContentOrder(materialId, "material");

        // material 삭제
        materialRepository.delete(material);

        // 반환
        MaterialDto.deleteResponse response = MaterialDto.deleteResponse.builder()
                .materialId(materialId)
                .message("material 삭제 완료")
                .build();
        return response;

    }

    // materialHistory 생성
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

    // fileUrl을 통해 materialId를 조회하고, materialHistory 상태 업데이트
    public void updateMaterialHistoryStatus(Long materialId) throws UnsupportedEncodingException {

        User authenticatedUser = getAuthenticatedUser();
        if (authenticatedUser == null) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        // materialId로 Material 조회
        Optional<Material> materialOptional = materialRepository.findById(materialId);
        if (materialOptional.isEmpty()) {
            throw new CustomException(ErrorCode.MATERIAL_NOT_FOUND);
        }
        Material material = materialOptional.get(); // material 객체 가져오기

        if (material.getCourse() == null) {
            throw new CustomException(ErrorCode.COURSE_NOT_FOUND);
        }

        Optional<Course> courseOptional = courseRepository.findById(material.getCourse().getCourseId());
        if (courseOptional.isEmpty()) {
            throw new CustomException(ErrorCode.COURSE_NOT_FOUND);
        }

        Course course = courseOptional.get();
        Long courseId = course.getCourseId();

        // materialId에 해당하는 materialHistory 상태 조회
        Optional<CourseAttendees> courseAttendees = courseAttendeesRepository.findByCourse_CourseIdAndUser_UserId(courseId, authenticatedUser.getUserId());
        if (courseAttendees.isEmpty()) {
            throw new CustomException(ErrorCode.COURSEATTENDEES_PERMISSION_DENIED);
        }

        Long courseAttendeesId = courseAttendees.get().getCourseAttendeesId();
        Optional<MaterialHistory> materialHistoryOptional = materialHistoryRepository.findByMaterial_MaterialIdAndCourseAttendees_CourseAttendeesId(materialId, courseAttendeesId);
        if (materialHistoryOptional.isEmpty()) {
            throw new CustomException(ErrorCode.MATERIALHISTORY_NOT_FOUND);
        }

        MaterialHistory materialHistory = materialHistoryOptional.get();

        if (!materialHistory.isMaterialHistoryStatus()) {
            materialHistory.setMaterialHistoryStatus(true);  // 상태 변경
            materialHistoryRepository.save(materialHistory);
        }
    }
}
