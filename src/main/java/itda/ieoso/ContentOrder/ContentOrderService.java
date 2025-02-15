package itda.ieoso.ContentOrder;


import itda.ieoso.Course.Course;
import itda.ieoso.Course.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ContentOrderService {
    private final ContentOrderRepository contentOrderRepository;
    private final CourseRepository courseRepository;

    // 순서 생성
    public ContentOrder createContentOrder(Course course, String contentType, Long contentId) {

        double maxOrderIndex = contentOrderRepository.findByCourseOrderByOrderIndexAsc(course)
                .stream()
                .mapToDouble(ContentOrder::getOrderIndex)
                .max()
                .orElse(0.0);

        ContentOrder contentOrder = new ContentOrder(course, contentType, contentId, maxOrderIndex+1);

        return contentOrderRepository.save(contentOrder);
    }


    // 순서 조회
//    public ContentOrder getContentOrders(Long courseId, Long userId, ContentOrderDto request) {
//        ContentOrder beforeContent = contentOrderRepository.findById(request.beforeContentId)
//                .orElseThrow(()-> new RuntimeException("이전항목을 찾을수없음"));
//
//        ContentOrder afterContent = contentOrderRepository.findById(request.afterContentId)
//                .orElseThrow(()-> new RuntimeException("다음항목을 찾을수없음"));
//
//        double newOrderIndex = (beforeContent.getOrderIndex() + afterContent.getOrderIndex()) / 2.0;
//
//        ContentOrder newContent = new ContentOrder(type, newOrderIndex,courseId);
//        return contentOrderRepository.save(newContent);
//
//    }

    // 순서 변경
    public void updateOrderIndex(Long courseId, ContentOrderDto.Request request) {
        ContentOrder movingContent = contentOrderRepository.findById(request.contentOrderId())
                .orElseThrow(()-> new IllegalArgumentException("순서를 찾을수없습니다."));

        ContentOrder targetContent = contentOrderRepository.findById(request.targetContentOrderId())
                .orElseThrow(()-> new IllegalArgumentException("순서를 찾을수없습니다."));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(()-> new IllegalArgumentException("강좌를 찾을수없습닏."));

        // TODO 강의개설자 검증

        // 해당 course의 모든 콘텐츠 순서 불러오기
        List<ContentOrder> contentOrderList = contentOrderRepository.findByCourseOrderByOrderIndexAsc(course);

        // 새 index 계산
        double newIndex = calculateNewIndex(contentOrderList, movingContent, targetContent);

        movingContent.setOrderIndex(newIndex);
        contentOrderRepository.save(movingContent);
    }

    private double calculateNewIndex(List<ContentOrder> contentOrderList, ContentOrder movingContent, ContentOrder targetContent) {
        int targetIndex = contentOrderList.indexOf(targetContent);
        int movingIndex = contentOrderList.indexOf(movingContent);

        if (movingIndex != -1) {
            contentOrderList.remove(movingContent);
        }

        contentOrderList.add(targetIndex, movingContent);

        double prevIndex = (targetIndex > 0) ? contentOrderList.get(targetIndex - 1).getOrderIndex() : 0.0;
        double nextIndex = (targetIndex < contentOrderList.size() - 1) ? contentOrderList.get(targetIndex + 1).getOrderIndex() : prevIndex+1.0;// 결함
        return (prevIndex + nextIndex) / 2.0;
    }

    // 순서 삭제
    public void deleteContentOrder(Long contentId, String contentType) {
        contentOrderRepository.deleteByContentIdAndContentType(contentId, contentType);
    }

}
