package site.foolish.ary.controller.member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import site.foolish.ary.domain.review.ReviewList;
import site.foolish.ary.dto.member.EmailRequest;
import site.foolish.ary.dto.member.LoginRequest;
import site.foolish.ary.dto.member.JoinRequest;
import site.foolish.ary.domain.member.Member;
import site.foolish.ary.service.member.MemberService;
import site.foolish.ary.service.review.ReviewService;
import site.foolish.ary.util.JWTUtil;
import site.foolish.ary.response.StatusEnum;
import site.foolish.ary.response.dto.Message;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;
    private final ReviewService reviewService;
    private final JWTUtil jwtUtil;

    @GetMapping("/emailDuplicated")
    public ResponseEntity<Message> emailDuplicated(@RequestBody EmailRequest request) {
        Message message = new Message();
        HttpHeaders headers = new HttpHeaders();

        log.info(request.getEmail());

        if(memberService.checkEmailDuplicated((request.getEmail()))) {
            message.setStatus(StatusEnum.FAILED);
            message.setSuccess(false);
            message.setMessage("이메일이 존재합니다.");
            message.setData(null);
        } else {
            message.setStatus(StatusEnum.OK);
            message.setSuccess(true);
            message.setMessage("사용 가능한 이메일입니다.");
            message.setData(null);
        }

        return new ResponseEntity<>(message, headers, HttpStatus.OK);
    }

    @PostMapping("/join")
    public ResponseEntity<Message> join(@RequestBody JoinRequest joinRequest) {

        Message message = new Message();
        HttpHeaders headers = new HttpHeaders();

        if(!joinRequest.getPassword().equals(joinRequest.getPasswordCheck())) {
            // 비밀번호 = 비밀번호 체크 여부 확인
            message.setStatus(StatusEnum.FAILED);
            message.setMessage("비밀번호가 일치하지 않습니다");
            message.setSuccess(false);
            message.setData(null);
        } else {
            // 비밀번호 암호화 추가한 회원가입 로직으로 회원가입
            memberService.securityJoin(joinRequest);

            message.setStatus(StatusEnum.OK);
            message.setMessage("가입 성공");
            message.setSuccess(true);
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
            message.setStatus(StatusEnum.FAILED);
            message.setSuccess(false);
            message.setMessage("ID 또는 비밀번호가 일치하지 않습니다");
        } else {
            message.setStatus(StatusEnum.OK);
            message.setMessage("로그인 성공");
            message.setSuccess(true);
            message.setData(jwtUtil.createJwt(member.getEmail(), member.getRole().name(), 60 * 60 * 1000L));
        }

        return new ResponseEntity<>(message, headers, HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<Message> logout(Authentication auth) {

        Message message = new Message();
        HttpHeaders headers = new HttpHeaders();

        // TODO : Logout 구현

        return new ResponseEntity<>(message, headers, HttpStatus.OK);
    }

    @GetMapping("/info")
    public ResponseEntity<Message> memberInfo(Authentication auth) {
        Member loginMember = memberService.getLoginMemberByEmail(auth.getName());
        Message message = new Message();
        HttpHeaders headers = new HttpHeaders();

        message.setStatus(StatusEnum.OK);
        message.setMessage(loginMember.getName() + " 회원 정보");
        message.setData(loginMember);
        return new ResponseEntity<>(message, headers, HttpStatus.OK);
    }

    @GetMapping("/reviews")
    public ResponseEntity<Message> memberReviewsInfo(Authentication auth) {
        Message message = new Message();
        HttpHeaders headers = new HttpHeaders();

        Member loginMember = memberService.getLoginMemberByEmail(auth.getName());
        loginMember.setPassword("hidden");

        // 리뷰 내용들 빼고 리뷰 타이틀 등 핵심 내용만 가져오기
        List<ReviewList> reviewList = reviewService.getMemberReviewsInfo(loginMember);

        message.setStatus(StatusEnum.OK);
        message.setMessage(loginMember.getName() + " 회원 리뷰 정보");
        message.setData(reviewList);

        return new ResponseEntity<>(message, headers, HttpStatus.OK);
    }

}
