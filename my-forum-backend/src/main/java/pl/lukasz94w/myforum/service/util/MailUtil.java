package pl.lukasz94w.myforum.service.util;

public final class MailUtil {

    public static String constructResetPasswordLink(String token, String serverUrl) {
        String apiName = "/auth/change";
        String tokenWithValue = "?token=" + token;
        return serverUrl + apiName + tokenWithValue;
    }

    public static String constructConfirmLink(String token, String serverUrl) {
        String apiName = "/auth/activate";
        String tokenWithValue = "?token=" + token;
        return serverUrl + apiName + tokenWithValue;
    }
}
