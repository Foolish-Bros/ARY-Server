package site.foolish.ary.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import site.foolish.ary.domain.member.Role;
import site.foolish.ary.domain.member.oauth.CustomOauth2UserDetails;
import site.foolish.ary.util.JWTUtil;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JWTUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {

        CustomOauth2UserDetails oAuth2User = (CustomOauth2UserDetails)authentication.getPrincipal();
        log.info(oAuth2User.getUsername());

        // role 추출
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        // JWTUtil에 token 생성 요청
        String token = jwtUtil.createJwt(oAuth2User.getUsername(), role, 60*60*1000L);

        // 쿠키 생성
        Cookie cookie = new Cookie("token", token);
        cookie.setDomain("localhost");
        cookie.setSecure(false); // HTTP에서도 쿠키를 전송하도록 설정 (개발/테스트 환경에서만 사용)
        cookie.setPath("/"); // 쿠키의 유효 경로 설정
        cookie.setMaxAge(3600); // 쿠키의 유효 기간 설정 (초 단위, 여기서는 1시간)

        response.addCookie(cookie);
        response.sendRedirect("http://localhost:3000");
    }
}
