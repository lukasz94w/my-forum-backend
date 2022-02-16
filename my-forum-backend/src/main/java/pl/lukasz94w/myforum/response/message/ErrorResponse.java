package pl.lukasz94w.myforum.response.message;

import lombok.Getter;

import java.util.Map;

@Getter
public class ErrorResponse {

    private final String message;
    private Map<String, Object> parametersMap;

    public ErrorResponse(String message) {
        this.message = message;
    }

    public ErrorResponse(String message, Map<String, Object> parametersMap) {
        this.message = message;
        this.parametersMap = parametersMap;
    }
}
