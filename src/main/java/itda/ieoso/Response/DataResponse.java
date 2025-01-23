package itda.ieoso.Response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DataResponse<T> extends BasicResponse {
    private int count;
    private T data;

    public DataResponse() {
        setSuccess(true);
    }

    public DataResponse(T data) {
        this();
        this.data = data;
        if (data instanceof List<?>) {
            this.count = ((List<?>) data).size();
        } else this.count = 1;
    }

    public DataResponse(T data, String message) {
        this(data);
        setMessage(message);
    }
}
