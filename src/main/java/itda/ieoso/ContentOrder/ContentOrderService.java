package itda.ieoso.ContentOrder;


import itda.ieoso.Course.Course;
import itda.ieoso.Course.CourseRepository;
import itda.ieoso.Lecture.Lecture;
import itda.ieoso.Lecture.LectureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ContentOrderService {
    private final ContentOrderRepository contentOrderRepository;
    private final CourseRepository courseRepository;
    private final LectureRepository lectureRepository;

    // 순서 생성
    public ContentOrder createContentOrder(Course course, Lecture lecture, String contentType, Long contentId) {
        int maxOrderIndex = contentOrderRepository.findByCourse_CourseIdAndLecture_LectureIdOrderByOrderIndexAsc(course.getCourseId(), lecture.getLectureId())
                .stream()
                .mapToInt(ContentOrder::getOrderIndex)
                .max()
                .orElse(0);

        ContentOrder contentOrder = new ContentOrder(course, lecture, contentType, contentId, maxOrderIndex+1);

        return contentOrderRepository.save(contentOrder);
    }

    // 순서 변경
    public void updateOrderIndex(Long courseId, Long lectureId, ContentOrderDto.Request request) {
        ContentOrder movingContent = contentOrderRepository.findById(request.contentOrderId())
                .orElseThrow(()-> new IllegalArgumentException("순서를 찾을수없습니다."));

        ContentOrder targetContent = contentOrderRepository.findById(request.targetContentOrderId())
                .orElseThrow(()-> new IllegalArgumentException("순서를 찾을수없습니다."));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(()-> new IllegalArgumentException("강좌를 찾을수없습닏."));

        // TODO 강의개설자 검증

        // 해당 course의 모든 콘텐츠 순서 불러오기
        List<ContentOrder> contentOrderList = contentOrderRepository.findByCourse_CourseIdAndLecture_LectureIdOrderByOrderIndexAsc(courseId, lectureId);

        // 새 index 계산
        int newIndex = calculateNewIndex(contentOrderList, movingContent, targetContent);

        movingContent.setOrderIndex(newIndex);
        contentOrderRepository.save(movingContent);

        normalizeIndexes(contentOrderList);
    }

    private int calculateNewIndex(List<ContentOrder> contentOrderList, ContentOrder movingContent, ContentOrder targetContent) {
        int targetIndex = contentOrderList.indexOf(targetContent);
        int movingIndex = contentOrderList.indexOf(movingContent);

        if (movingIndex != -1) {
            contentOrderList.remove(movingContent);
        }

        contentOrderList.add(targetIndex, movingContent);

        int prevIndex = (targetIndex > 0) ? contentOrderList.get(targetIndex - 1).getOrderIndex() : 0;
        int nextIndex = (targetIndex < contentOrderList.size() - 1) ? contentOrderList.get(targetIndex + 1).getOrderIndex() : prevIndex+1;// 결함

        if (targetIndex == contentOrderList.size() - 1) {
            nextIndex = prevIndex + 1;
        }

        return (prevIndex + nextIndex) / 2;
    }

    private void normalizeIndexes(List<ContentOrder> contentOrderList) {
        // 인덱스를 1부터 시작하는 정수로 재정렬
        for (int i = 0; i < contentOrderList.size(); i++) {
            contentOrderList.get(i).setOrderIndex(i + 1);  // 1부터 시작하는 순서로 재설정
        }
        // 변경된 인덱스를 저장
        contentOrderRepository.saveAll(contentOrderList);
    }

    // 순서 삭제
    public void deleteContentOrder(Long contentId, String contentType) {
        contentOrderRepository.deleteByContentIdAndContentType(contentId, contentType);
    }

}
