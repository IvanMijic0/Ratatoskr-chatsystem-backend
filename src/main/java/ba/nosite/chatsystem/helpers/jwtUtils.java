package ba.nosite.chatsystem.helpers;

import org.apache.commons.lang3.StringUtils;

public class jwtUtils {
    public static String extractJwtFromHeader(String jwt) {
        return StringUtils.substring(jwt, 7);
    }
}

