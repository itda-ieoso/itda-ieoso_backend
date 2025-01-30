package itda.ieoso.Lecture;

import itda.ieoso.Assignment.Assignment;
import itda.ieoso.Material.Material;
import itda.ieoso.Video.Video;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CurriculumModificationRequest {

    // create : 커리큘럼생성 기존거 사용 -> 렉쳐와 그외에것 새로 생성할때
    private List<CurriculumDto> curriculumDtos;

    // add : 부자재 추가할때 -> 랙쳐아이디 받아와서 하기 (렉쳐는 add 필요없음 create만 함)
    private List<ModifyRequestDto> modifyRequestDto;

    // create : 필요성(id가 자동생성이기때문에 lecture생성후 바로 add로 넣을수없음 -> lecture생성후 저장->부자재 생성 가능한디?
    // add    : 아이디, 제목, 데이터, 타입, 행위 -> 아이디로 렉쳐 조회
    // update : 아이디, 제목, 데이터, 타입, 행위 -> 아이디로 타입에 해당하는값 조회
    // delete : 아이디,            타입, 행위 -> 아이디로 타입에 해당하는값 조회


    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ModifyRequestDto {
        private Long id;
        private String type;            // material, video, assignment
        private String action;          // add, update, delete
        private String title;           // 선택 사항
        private String item;            // 선택 사항
        private LocalDate startDate;    // 선택 사항
        private LocalDate endDate;      // 선택 사항
    }

    // lecture    : 제목, 내용, 시작일, 종료일, 생성일, 업데이트일
    // material   : 제목, 내용,              생성일, 업데이트일
    // video      : 제목, 내용, 시작일, 종료일, 생성일, 업데이트일
    // assignment : 제목, 내용, 시작일, 종료일, 생성일, 업데이트일



}

/**
 *  @PostMapping("/bulk")
 *     public void modifyLectures(@RequestBody List<LectureModificationRequest> requests) {
 *         for (LectureModificationRequest request : requests) {
 *             Lecture lecture = lectureRepository.findByTitle(request.getLectureTitle())
 *                     .orElseGet(() -> {
 *                         Lecture newLecture = new Lecture(request.getLectureTitle(), request.getLectureDescription());
 *                         return lectureRepository.save(newLecture);
 *                     });
 *
 *             // Update Lecture Description
 *             if (request.getLectureDescription() != null) {
 *                 lecture.setDescription(request.getLectureDescription());
 *                 lectureRepository.save(lecture);
 *             }
 *
 *             // Handle Video modifications
 *             if (request.getVideosToAdd() != null) {
 *                 request.getVideosToAdd().forEach(video -> {
 *                     video.setLecture(lecture);
 *                     videoRepository.save(video);
 *                 });
 *             }
 *             if (request.getVideosToRemove() != null) {
 *                 videoRepository.deleteByLectureAndTitleIn(lecture, request.getVideosToRemove());
 *             }
 *
 *             // Handle Material modifications
 *             if (request.getMaterialsToAdd() != null) {
 *                 request.getMaterialsToAdd().forEach(material -> {
 *                     material.setLecture(lecture);
 *                     materialRepository.save(material);
 *                 });
 *             }
 *             if (request.getMaterialsToRemove() != null) {
 *                 materialRepository.deleteByLectureAndTitleIn(lecture, request.getMaterialsToRemove());
 *             }
 *
 *             // Handle Assignment modifications
 *             if (request.getAssignmentsToAdd() != null) {
 *                 request.getAssignmentsToAdd().forEach(assignment -> {
 *                     assignment.setLecture(lecture);
 *                     assignmentRepository.save(assignment);
 *                 });
 *             }
 *             if (request.getAssignmentsToRemove() != null) {
 *                 assignmentRepository.deleteByLectureAndTitleIn(lecture, request.getAssignmentsToRemove());
 *             }
 *         }
 *     }
 */
