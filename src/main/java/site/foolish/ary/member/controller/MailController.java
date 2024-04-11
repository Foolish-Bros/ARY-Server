package site.foolish.ary.member.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.foolish.ary.member.dto.MailRequest;
import site.foolish.ary.member.service.MailService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/mail")
public class MailController {

    private final MailService mailService;

    @PostMapping("/check")
    public int mailSend(@RequestBody MailRequest request) {
        int number = mailService.sendMail(request.getEmail());

        log.info(String.valueOf(number));

        return number;
    }
}
