package site.foolish.ary.domain.member.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import site.foolish.ary.domain.member.dto.JoinRequest;
import site.foolish.ary.domain.member.dto.LoginRequest;
import site.foolish.ary.domain.member.entity.Member;
import site.foolish.ary.domain.member.service.MemberService;
import site.foolish.ary.domain.member.util.JWTUtil;

import java.util.Collection;
import java.util.Iterator;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;
    private final JWTUtil jwtUtil;

    @GetMapping("")
    public String home(Model model) {

        model.addAttribute("loginType", "security-login");
        model.addAttribute("pageName", "ARY Member Login");

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iter = authorities.iterator();
        GrantedAuthority auth = iter.next();
        String role = auth.getAuthority();

        Member loginMember = memberService.getLoginMemberByEmail(email);

        log.info(email);

        if(loginMember.getEmail() != null) {
            model.addAttribute("name", loginMember.getName());
        }

        return "home";
    }

    @GetMapping("/join")
    public String joinPage(Model model) {

        model.addAttribute("loginType", "security-login");
        model.addAttribute("pageName", "ARY Member Login");

        // 회원가입을 위해서 model 통해서 joinRequest 전달
        model.addAttribute("joinRequest", new JoinRequest());

        return "join";
    }

    @PostMapping("/join")
    public String join(@RequestBody JoinRequest joinRequest,
                       BindingResult bindingResult, Model model) {

        model.addAttribute("loginType", "security-login");
        model.addAttribute("pageName", "ARY Member Login");

        // ID 중복 여부 확인
        if(memberService.checkEmailDuplicated((joinRequest.getEmail()))) {
            return "email 이 존재합니다.";
        }

        // 비밀번호 = 비밀번호 체크 여부 확인
        if(!joinRequest.getPassword().equals(joinRequest.getPasswordCheck())) {
            return "비밀번호가 일치하지 않습니다.";
        }

        // 비밀번호 암호화 추가한 회원가입 로직으로 회원가입
        memberService.securityJoin(joinRequest);

        // 회원가입 시 홈 화면으로 이동
        return "redirect:/member";
    }

    @GetMapping("/login")
    public String loginPage(@RequestBody LoginRequest loginRequest) {

        Member member = memberService.login(loginRequest);

        if(member == null) {
            return "ID 또는 비밀번호가 일치하지 않습니다";
        }

        return jwtUtil.createJwt(member.getEmail(), member.getRole().name(), 60 * 60 * 1000L);
    }

    @GetMapping("/info")
    public Member memberInfo(Authentication auth, Model model) {


        model.addAttribute("loginType", "security-login");
        model.addAttribute("pageName", "ARY Member Login");

        Member loginMember = memberService.getLoginMemberByEmail(auth.getName());

        model.addAttribute("member", loginMember);
        return loginMember;
    }

    @GetMapping("/admin")
    public String adminPage(Model model) {

        model.addAttribute("loginType", "security-login");
        model.addAttribute("pageName", "ARY Member Login");

        return "admin";
    }

}
