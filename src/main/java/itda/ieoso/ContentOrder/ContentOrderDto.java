package itda.ieoso.ContentOrder;

public class ContentOrderDto {
    public record Request(
            Long contentOrderId,        // 위치변경할 객체의 id
            Long targetContentOrderId   // 변경된 객체가 들어갈 자리의 기존객체id(밀려나는 객체)
    ) {}

    public record Response(
            Long contentOrderId,
            String contentType,
            Object contentData
    ) {}
}
