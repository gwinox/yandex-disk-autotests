package api;

import lombok.Getter;

@Getter
public class ApiResponse<T> {
    private final int statusCode;
    private final String contentType;
    private final T body;

    public ApiResponse(int statusCode, String contentType, T body) {
        this.statusCode = statusCode;
        this.contentType = contentType;
        this.body = body;
    }
}