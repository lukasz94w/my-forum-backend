package pl.lukasz94w.myforum.response.message;

import lombok.Getter;

import java.util.Map;

@Getter
public class BanResponse extends ErrorResponse {

    private final Map<String, Object> parametersMap;

    public BanResponse(String message, Map<String, Object> parametersMap) {
        super(message);
        this.parametersMap = parametersMap;
    }
}
