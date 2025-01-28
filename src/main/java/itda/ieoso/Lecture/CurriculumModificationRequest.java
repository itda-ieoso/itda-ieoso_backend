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
    //

    // add : 부자재 추가할때 -> 랙쳐아이디 받아와서 하기 (렉쳐는 add 필요없음 create만 함)
    private List<ModifyRequestDto> modifyRequestDto;      // 랙쳐아이디, 제목, 데이터 (material, video, assignnet 통합)

    // create : 필요성(id가 자동생성이기때문에 lecture생성후 바로 add로 넣을수없음 -> lecture생성후 저장->부자재 생성 가능한디?
    // add    : 아이디, 제목, 데이터, 타입, 행위 -> 아이디로 렉쳐 조회
    // update : 아이디, 제목, 데이터, 타입, 행위 -> 아이디로 타입에 해당하는값 조회
    // delete : 아이디,            타입, 행위 -> 아이디로 타입에 해당하는값 조회

    // 2단커밋으로할거면 add update delete로만 해도 될듯
    // lecture생성-. add로 부자재들 추가

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ModifyRequestDto {
        private Long id;
        private String type;    // material, video, assignment
        private String action;  // add, update, delete
        private String title;   // 선택 사항
        private String item;    // 선택 사항
        private LocalDate startDate;
        private LocalDate endDate;
    }

    // lecture    : 제목, 내용, 시작일, 종료일, 생성일, 업데이트일
    // material   : 제목, 내용,              생성일, 업데이트일
    // video      : 제목, 내용, 시작일, 종료일, 생성일, 업데이트일
    // assignment : 제목, 내용, 시작일, 종료일, 생성일, 업데이트일


    //private List<ModifyRequestDto> videoCreateDtos;         // 랙쳐아이디, 제목, 영상
    //private List<ModifyRequestDto> assignmentCreateDtos;    // 랙쳐아이디, 제목, 설명
    // update : 부자재 수정할때 -> 랙쳐 - 랙쳐아이디, 제목, 설명 / 부자재 -> 부자재아이디, 제목, 링크
    //private List<ModifyRequestDto> lectureCreateDtos;      // 랙쳐아이디, 제목, 설명    (제목, 설명은 선택)
    //private List<ModifyRequestDto> materialUpdateDtos;     // 부자재 아이디, 제목, 자료  (제목, 자료 선택)
    //private List<ModifyRequestDto> videoUpdateDtos;        // 부자재 아이디, 제목, 영상  (제목, 영상 선택)
    //private List<ModifyRequestDto> assignmentUpdateDtos;   // 부자재 아이디, 제목, 과제  (제목, 과제 선택)
    // delete : 렉쳐 삭제 -> 렉쳐아이디 / 부자재 삭제 - 부자재 아이디
    //List<Long> lectureDeleteDtos;      // 랙쳐 아이디만 -> 전체삭제 or 렉쳐만 삭제?
    //List<Long> materialDeleteDtos;     // 부자재 아이디만
    //List<Long> videoDeleteDtos;        // 부자재 아이디만
    //List<Long> assignmentDeleteDtos;   // 부자재 아이디만
    // 위치 이동은 어떻게 알지? = 순서를 위한 컬럼필요
    // create, update, add, delete에대한 type 필요


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

// lecture nuarable을 뺴고 courseid 를 ㄱ부자재에 추가하기. courseid에 연결되도록 저장
// order추가해서 courseid로 order순으로 불러오기
// add : 생성정보들만 -> lecture, material, video, assignment 생성 -> courseid필수
// update :