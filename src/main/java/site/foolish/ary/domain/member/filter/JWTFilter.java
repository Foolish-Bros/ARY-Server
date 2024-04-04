package site.foolish.ary.domain.member.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import site.foolish.ary.domain.member.entity.CustomUserDetails;
import site.foolish.ary.domain.member.entity.Member;
import site.foolish.ary.domain.member.entity.Role;
import site.foolish.ary.domain.member.util.JWTUtil;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // request에서 Authorization 헤더 찾기
        String authorization = request.getHeader("Authorization");

        // Authorization 헤더 검즘
        // Authorization 헤더가 비어있거나 "Bearer " 로 시작하지 않은 경우
        if(authorization == null || !authorization.startsWith("Bearer ")) {
            log.warn("token null");
            // 토큰이 유효하지 않음 --> request 와 response 를 다음 필터로 넘겨줌
            filterChain.doFilter(request, response);

            return;
        }

        // Authorization 에서 Bearer 떼기
        String token = authorization.split(" ")[1];

        // token 소멸 시간 검증
        // 유효기간이 만료한 경우
        if(jwtUtil.isExpired(token)) {
            log.warn("token expired");
            filterChain.doFilter(request, response);

            return;
        }

        // 이 단계에서는 최종적으로 token 검증 완료됨 --> 일시적인 session 생성
        // session 에 user 정보 설정
        String email = jwtUtil.getEmail(token);
        String role = jwtUtil.getRole(token);

        Member member = new Member();
        member.setEmail(email);
        // 매번 요청마다 DB 조회해서 password 초기화할 필요 X --> 정확한 비밀번호를 넣을 필요 없음
        // 따라서, 임시 비밀번호 설정
        member.setPassword("temp password");
        // TODO setRole 수정???????????????????????????????
        member.setRole(Role.USER);

        // UserDetails 에 회원 정보 객체 담기
        CustomUserDetails customUserDetails = new CustomUserDetails(member);

        // Spring Security 인증 토큰 생성
        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

        // 세션에 사용자 등록
        SecurityContextHolder.getContext().setAuthentication(authToken);

        // 다음 필터
        filterChain.doFilter(request, response);

    }
}
