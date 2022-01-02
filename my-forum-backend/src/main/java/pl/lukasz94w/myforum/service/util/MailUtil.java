package pl.lukasz94w.myforum.service.util;

public final class MailUtil {
    public static String constructResetPasswordLink(String token) {
        //temporary solution, in proper way: get angular server address
        //from HttpServletRequest via getRemoteAddr() or getRemoteHost() method
        String serverUrl = "http://localhost:4200";
        String apiName = "/auth/change";
        String tokenWithValue = "?token=" + token;
        return serverUrl + apiName + tokenWithValue;
    }
}
