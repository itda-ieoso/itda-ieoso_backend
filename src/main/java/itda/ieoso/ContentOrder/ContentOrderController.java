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
    @PutMapping("/{courseId}/reorder")
    public Response<?> updateOrderIndex(@PathVariable Long courseId,
                                        @RequestBody ContentOrderDto.Request request) {
        contentOrderService.updateOrderIndex(courseId, request);
        return Response.success("커리큘럼 순서변경",null);
    }


}
