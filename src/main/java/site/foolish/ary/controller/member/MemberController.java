package site.foolish.ary.controller.member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import site.foolish.ary.domain.result.Result;
import site.foolish.ary.domain.review.ReviewList;
import site.foolish.ary.dto.member.LoginRequest;
import site.foolish.ary.dto.member.JoinRequest;
import site.foolish.ary.domain.member.Member;
import site.foolish.ary.dto.member.PasswordRequest;
import site.foolish.ary.service.member.MemberService;
import site.foolish.ary.service.result.ResultService;
import site.foolish.ary.service.review.ReviewService;
import site.foolish.ary.util.JWTUtil;
import site.foolish.ary.response.StatusEnum;
import site.foolish.ary.response.dto.Message;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;
    private final ReviewService reviewService;
    private final JWTUtil jwtUtil;
    private final ResultService resultService;

    @GetMapping("/emailDuplicated")
    public ResponseEntity<Message> emailDuplicated(@RequestParam String email) throws Exception {
        try {
            log.info("called emailDuplicated");
            Message message = new Message();
            HttpHeaders headers = new HttpHeaders();

            log.info(email);

            if (memberService.checkEmailDuplicated(email)) {
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
        }catch (Exception e){
            throw new Exception(e);
        }
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

    @PostMapping("/login")
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
            message.setSuccess(true);
            message.setMessage("로그인 성공");
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
        message.setSuccess(true);
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
        message.setSuccess(true);
        message.setMessage(loginMember.getName() + " 회원 리뷰 정보");
        message.setData(reviewList);

        return new ResponseEntity<>(message, headers, HttpStatus.OK);
    }

    @GetMapping("/results")
    public ResponseEntity<Message> memberResultsInfo(Authentication auth) {
        Message message = new Message();
        HttpHeaders headers = new HttpHeaders();

        Member loginMember = memberService.getLoginMemberByEmail(auth.getName());
        loginMember.setPassword("hidden");

        List<Result> results = resultService.findResultsByMember(loginMember);

        message.setStatus(StatusEnum.OK);
        message.setSuccess(true);
        message.setMessage(loginMember.getName() + " 회원 조회기록");
        message.setData(results);

        return new ResponseEntity<>(message, headers, HttpStatus.OK);
    }

    @PostMapping("/changePassword")
    public ResponseEntity<Message> changeMemberPassword(Authentication auth, @RequestBody PasswordRequest request) {
        Message message = new Message();
        HttpHeaders headers = new HttpHeaders();

        log.info(request.getOldPassword());
        log.info(request.getNewPassword());

        Member loginMember = memberService.getLoginMemberByEmail(auth.getName());

        boolean isSuccess = memberService.changePassword(loginMember, request.getOldPassword(), request.getNewPassword());

        if(isSuccess) {
            message.setStatus(StatusEnum.OK);
            message.setSuccess(true);
            message.setMessage(loginMember.getName() + " 회원 비밀번호 변경");
        } else {
            message.setStatus(StatusEnum.FAILED);
            message.setSuccess(false);
            message.setMessage("비밀번호가 틀렸습니다!");
        }

        return new ResponseEntity<>(message, headers, HttpStatus.OK);
    }

    @DeleteMapping("/withdrawal")
    public ResponseEntity<Message> withdrawal(Authentication auth) {
        Message message = new Message();
        HttpHeaders headers = new HttpHeaders();

        Member loginMember = memberService.getLoginMemberByEmail(auth.getName());

        boolean isSuccess = memberService.withdrawMember(loginMember);

        if(isSuccess) {
            message.setStatus(StatusEnum.OK);
            message.setSuccess(true);
            message.setMessage("회원 탈퇴하였습니다. 그동안 이용해주셔서 감사합니다.");
        } else {
            message.setStatus(StatusEnum.FAILED);
            message.setSuccess(false);
            message.setMessage("회원 탈퇴 실패, 잠시 후 다시 시도해주세요.");
        }


        return new ResponseEntity<>(message, headers, HttpStatus.OK);

    }

}
