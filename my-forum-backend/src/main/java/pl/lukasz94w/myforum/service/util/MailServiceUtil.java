package pl.lukasz94w.myforum.service.util;

import org.springframework.stereotype.Component;

@Component
public final class MailServiceUtil {

    public String constructResetPasswordLink(String token, String serverUrl) {
        String apiName = "/auth/change";
        String tokenWithValue = "?token=" + token;
        return serverUrl + apiName + tokenWithValue;
    }

    public String constructConfirmLink(String token, String serverUrl) {
        String apiName = "/auth/activate";
        String tokenWithValue = "?token=" + token;
        return serverUrl + apiName + tokenWithValue;
    }
}
