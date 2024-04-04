package site.foolish.ary.config.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import site.foolish.ary.domain.member.entity.Role;
import site.foolish.ary.domain.member.filter.JWTFilter;
import site.foolish.ary.domain.member.filter.LoginFilter;
import site.foolish.ary.domain.member.util.JWTUtil;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationConfiguration configuration;
    private final JWTUtil jwtUtil;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    // Security Filter 메소드
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // Spring Security jwt 로그인 설정

        // csrf : 사이트 위변조 방지 설정(Spring Security에는 자동으로 설정되어 있음)
        // csrf 기능 켜져있으면 post 요청을 보낼때 csrf 토큰도 보내줘야 로그인이 진행됨
        // TODO 개발단계 끝나고 csrf 다시 활성화
        http
                .csrf(AbstractHttpConfigurer::disable);

        // formLogin 형식 disable 설정 --> POSTMAN test
        http
                .formLogin(AbstractHttpConfigurer::disable);

        // http basic 인증 방식 disable 설정
        http
                .httpBasic(AbstractHttpConfigurer::disable);

        // 경로별 permission 설정
        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/member/**").permitAll()
                        .requestMatchers("/member/admin").hasRole(Role.ADMIN.name())
                        .anyRequest().authenticated()
                );

        // session 설정
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // 새로 만든 로그인 필터를 원래의 (UsernamePasswordAuthenticationFilter)의 자리에 넣음
        http
                .addFilterAt(new LoginFilter(authenticationManager(configuration), jwtUtil), UsernamePasswordAuthenticationFilter.class);

        // 로그인 필터 이전에 JWTFilter 를 넣음
        http
                .addFilterBefore(new JWTFilter(jwtUtil), LoginFilter.class);

        return http.build();
    }

    /**
     * BCrypt password encoder를 리턴하는 메소드
     * @return BCrypt password encoder
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
//        configuration.setAllowedMethods(Arrays.asList("GET","POST","PUT","DELETE","OPTIONS"));
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//        return source;
//    }

//    @Bean
//    public WebSecurityCustomizer webSecurityCustomizer() {
//        return (web) -> web.ignoring()
//                .requestMatchers("/member/**")
//                .requestMatchers("/")
//                .requestMatchers("/resources/**");
//    }
}

