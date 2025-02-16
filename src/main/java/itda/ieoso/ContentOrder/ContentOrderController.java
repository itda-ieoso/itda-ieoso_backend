package itda.ieoso.ContentOrder;

import itda.ieoso.Response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contentorders")
public class ContentOrderController {

    @Autowired
    ContentOrderService contentOrderService;

    // 순서 변경
    @PutMapping("/{courseId}/{lectureId}/reorder")
    public Response<?> updateOrderIndex(@PathVariable Long courseId,
                                        @PathVariable Long lectureId,
                                        @RequestBody ContentOrderDto.Request request) {
        contentOrderService.updateOrderIndex(courseId, lectureId, request);
        return Response.success("커리큘럼 순서변경",null);
    }


}
