package site.foolish.ary.domain.member.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import site.foolish.ary.domain.member.dto.JoinRequest;
import site.foolish.ary.domain.member.dto.LoginRequest;
import site.foolish.ary.domain.member.entity.Member;
import site.foolish.ary.domain.member.service.MemberService;

@Slf4j
@RestController
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/join")
    public String join(@RequestBody JoinRequest joinRequest) {
        log.info(joinRequest.getEmail());
        log.info(joinRequest.getPassword());
        log.info(joinRequest.getName());

        if(!memberService.checkEmailDuplicated(joinRequest.getEmail())) {
            memberService.join(joinRequest);
            return "joined!";
        } else {
            return "This email already exists!";
        }
    }

    @GetMapping("/login")
    public Member login(@RequestBody LoginRequest loginRequest) {
        return memberService.login(loginRequest);
    }
}
