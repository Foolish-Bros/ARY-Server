package site.foolish.ary.member.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.foolish.ary.member.dto.MailRequest;
import site.foolish.ary.member.service.MailService;
import site.foolish.ary.response.StatusEnum;
import site.foolish.ary.response.dto.Message;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/mail")
public class MailController {

    private final MailService mailService;

    @PostMapping("/check")
    public ResponseEntity<Message> mailSend(@RequestBody MailRequest request) {
        int code = mailService.sendMail(request.getEmail());
        Message message = new Message();
        HttpHeaders headers = new HttpHeaders();

        message.setStatus(StatusEnum.OK);
        message.setMessage("인증 코드 전송 완료");
        message.setData(code);

        log.info(String.valueOf(code));

        return new ResponseEntity<>(message, headers, HttpStatus.OK);
    }
}
