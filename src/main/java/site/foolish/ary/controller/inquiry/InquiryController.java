package site.foolish.ary.controller.inquiry;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.foolish.ary.domain.member.Member;
import site.foolish.ary.dto.inquiry.InquiryRequest;
import site.foolish.ary.response.StatusEnum;
import site.foolish.ary.response.dto.Message;
import site.foolish.ary.service.inquiry.InquiryService;
import site.foolish.ary.service.member.MemberService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/inquiry")
public class InquiryController {

    private final InquiryService inquiryService;
    private final MemberService memberService;

    @PostMapping("/question")
    private ResponseEntity<Message> sendQuestion(Authentication auth, @RequestBody InquiryRequest request) {
        Message message = new Message();
        HttpHeaders headers = new HttpHeaders();

        Member loginMember = memberService.getLoginMemberByEmail(auth.getName());

        message.setStatus(StatusEnum.OK);
        message.setSuccess(true);
        message.setMessage("문의 사항을 남겼습니다. 관리자 확인 후 메일로 답변드리겠습니다.");
        message.setData(inquiryService.inquirySave(loginMember, request.getContent()));

        return new ResponseEntity<>(message, headers, HttpStatus.OK);
    }
}
