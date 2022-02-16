package pl.lukasz94w.myforum.response.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class SuccessResponse {
    private final String message;
}
