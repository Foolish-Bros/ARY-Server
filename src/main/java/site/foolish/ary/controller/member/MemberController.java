package site.foolish.ary.controller.member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import site.foolish.ary.dto.member.LoginRequest;
import site.foolish.ary.dto.member.JoinRequest;
import site.foolish.ary.domain.member.Member;
import site.foolish.ary.service.member.MemberService;
import site.foolish.ary.util.JWTUtil;
import site.foolish.ary.response.StatusEnum;
import site.foolish.ary.response.dto.Message;

import java.nio.charset.StandardCharsets;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;
    private final JWTUtil jwtUtil;

    @PostMapping("/join")
    public ResponseEntity<Message> join(@RequestBody JoinRequest joinRequest,
                                        BindingResult bindingResult, Model model) {

        model.addAttribute("loginType", "security-login");
        model.addAttribute("pageName", "ARY Member Login");

        Message message = new Message();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));

        if(memberService.checkEmailDuplicated((joinRequest.getEmail()))) {
            // ID 중복 여부 확인
            message.setStatus(StatusEnum.OK);
            message.setMessage("이메일이 존재합니다.");
            message.setData(null);
        } else if(!joinRequest.getPassword().equals(joinRequest.getPasswordCheck())) {
            // 비밀번호 = 비밀번호 체크 여부 확인
            message.setStatus(StatusEnum.OK);
            message.setMessage("비밀번호가 일치하지 않습니다");
            message.setData(null);
        } else {
            // 비밀번호 암호화 추가한 회원가입 로직으로 회원가입
            memberService.securityJoin(joinRequest);

            message.setStatus(StatusEnum.OK);
            message.setMessage("가입 성공");
            message.setData(joinRequest);
        }

        return new ResponseEntity<>(message, headers, HttpStatus.OK);
    }

    @GetMapping("/login")
    public ResponseEntity<Message> login(@RequestBody LoginRequest loginRequest) {

        Member member = memberService.login(loginRequest);
        Message message = new Message();
        HttpHeaders headers = new HttpHeaders();

        if(member == null) {
            message.setStatus(StatusEnum.OK);
            message.setMessage("ID 또는 비밀번호가 일치하지 않습니다");
            message.setData(null);
        } else {
            message.setStatus(StatusEnum.OK);
            message.setMessage("로그인 성공");
            message.setData(jwtUtil.createJwt(member.getEmail(), member.getRole().name(), 60 * 60 * 1000L));
        }

        return new ResponseEntity<>(message, headers, HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<Message> logout(Authentication auth) {

        Message message = new Message();
        HttpHeaders headers = new HttpHeaders();



        return new ResponseEntity<>(message, headers, HttpStatus.OK);
    }

    @GetMapping("/info")
    public ResponseEntity<Message> memberInfo(Authentication auth, Model model) {

        model.addAttribute("loginType", "security-login");
        model.addAttribute("pageName", "ARY Member Login");

        Member loginMember = memberService.getLoginMemberByEmail(auth.getName());
        Message message = new Message();
        HttpHeaders headers = new HttpHeaders();

        message.setStatus(StatusEnum.OK);
        message.setMessage(loginMember.getName() + " 회원 정보");
        message.setData(loginMember);

        model.addAttribute("member", loginMember);
        return new ResponseEntity<>(message, headers, HttpStatus.OK);
    }

    @GetMapping("/admin")
    public String adminPage(Model model) {

        model.addAttribute("loginType", "security-login");
        model.addAttribute("pageName", "ARY Member Login");

        return "admin";
    }

}
