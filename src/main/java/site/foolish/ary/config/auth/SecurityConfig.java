package site.foolish.ary.config.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import site.foolish.ary.domain.member.Role;
import site.foolish.ary.filter.member.JWTFilter;
import site.foolish.ary.filter.member.LoginFilter;
import site.foolish.ary.util.JWTUtil;

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

        // TODO 개발단계 끝나고 csrf 다시 활성화
        http
                .csrf(AbstractHttpConfigurer::disable);

        // 경로별 permission 설정
        http
                .authorizeHttpRequests((auth) -> auth
//                        .requestMatchers("/member/**").permitAll()
                        .requestMatchers("/member/info").authenticated()
                        .requestMatchers("/member/admin").hasRole(Role.ADMIN.name())
                        .anyRequest().permitAll()
                );

        // 폼 로그인 방식 설정
        http
                .formLogin((auth) -> auth.loginPage("/member/login")
                        .loginProcessingUrl("/member/loginProc")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/member")
                        .failureUrl("/member")
                        .permitAll());

        // OAuth 2.0 로그인 방식 설정
        http
                .oauth2Login((auth) -> auth.loginPage("/member/login")
                        .defaultSuccessUrl("/member")
                        .failureUrl("/member/login")
                        .permitAll());

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

