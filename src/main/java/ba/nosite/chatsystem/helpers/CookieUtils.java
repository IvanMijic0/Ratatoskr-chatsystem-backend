package ba.nosite.chatsystem.helpers;

import ba.nosite.chatsystem.core.services.authServices.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CookieUtils {
    public static String extractCookieFromJwt(HttpServletRequest request, String cookieKey) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookieKey.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public static void setJwtCookie(
            HttpServletResponse response,
            String jwt,
            JwtService jwtService
    ) {
        long maxAge = jwtService.extractExpiration(jwt).getTime() - System.currentTimeMillis();

        Cookie jwtCookie = new Cookie("jwt", jwt);
        jwtCookie.setMaxAge((int) (maxAge / 1000));
        jwtCookie.setSecure(false); // TODO --> For testing
        jwtCookie.setPath("/api/v1");
        jwtCookie.setHttpOnly(true);

        response.addCookie(jwtCookie);
    }

    public static void setJwtCookie(
            HttpServletResponse response,
            String jwt,
            String cookieName,
            JwtService jwtService
    ) {
        long maxAge = jwtService.extractExpiration(jwt).getTime() - System.currentTimeMillis();

        Cookie jwtCookie = new Cookie(cookieName, jwt);
        jwtCookie.setMaxAge((int) (maxAge / 1000));
        jwtCookie.setSecure(false); // TODO --> For testing
        jwtCookie.setHttpOnly(true);

        response.addCookie(jwtCookie);
    }
}
