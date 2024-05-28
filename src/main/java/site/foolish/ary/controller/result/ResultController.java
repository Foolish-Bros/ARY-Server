package site.foolish.ary.controller.result;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import site.foolish.ary.domain.member.Member;
import site.foolish.ary.domain.result.Question;
import site.foolish.ary.domain.result.Result;
import site.foolish.ary.dto.result.LoadRequest;
import site.foolish.ary.dto.result.QuestionRequest;
import site.foolish.ary.dto.result.ResultCreateRequest;
import site.foolish.ary.response.StatusEnum;
import site.foolish.ary.response.dto.Message;
import site.foolish.ary.service.member.MemberService;
import site.foolish.ary.service.result.ResultService;

import java.util.Date;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/result")
public class ResultController {
    private final ResultService resultService;
    private final MemberService memberService;

    @GetMapping("/load")
    public ResponseEntity<Message> load(@RequestParam String id) {
        Message message = new Message();
        HttpHeaders headers = new HttpHeaders();

        Result result = resultService.loadResult(id);

        message.setStatus(StatusEnum.OK);
        message.setMessage("결과조회 완료");
        message.setSuccess(true);
        message.setData(result);

        return new ResponseEntity<>(message, headers, HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<Message> create(Authentication auth, @RequestBody ResultCreateRequest request) {
        Message message = new Message();
        HttpHeaders headers = new HttpHeaders();

        Member member = memberService.getLoginMemberByEmail(auth.getName());
        member.setPassword("hidden");

        Result result = resultService.createResult(member, request.getReviewId());

        message.setStatus(StatusEnum.OK);
        message.setMessage("생성 완료");
        message.setSuccess(true);
        message.setData(result);

        return new ResponseEntity<>(message, headers, HttpStatus.OK);
    }

    @PostMapping("/update")
    public ResponseEntity<Message> update(@RequestBody QuestionRequest request) {
        Message message = new Message();
        HttpHeaders headers = new HttpHeaders();

        Question question = Question.builder()
                .question(request.getQuestion())
                .answer(request.getAnswer())
                .createdAt(new Date())
                .build();

        Result result = resultService.updateResult(request.getResultId(), question);

        message.setStatus(StatusEnum.OK);
        message.setMessage("추가 완료");
        message.setSuccess(true);
        message.setData(result);

        return new ResponseEntity<>(message, headers, HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Message> delete(@RequestBody LoadRequest request) {
        Message message = new Message();
        HttpHeaders headers = new HttpHeaders();

        boolean isSuccess = resultService.deleteResult(request.getResultId());

        if (isSuccess) {
            message.setStatus(StatusEnum.OK);
            message.setMessage("삭제 완료");
            message.setSuccess(true);
            message.setData(null);
        } else {
            message.setStatus(StatusEnum.FAILED);
            message.setMessage("삭제 실패, 잠시 후 다시 시도해주세요.");
            message.setSuccess(false);
            message.setData(null);
        }

        return new ResponseEntity<>(message, headers, HttpStatus.OK);
    }

    @GetMapping("/recent")
    public ResponseEntity<Message> recent() {
        Message message = new Message();
        HttpHeaders headers = new HttpHeaders();

        message.setStatus(StatusEnum.OK);
        message.setMessage("조회 완료");
        message.setSuccess(true);
        message.setData(resultService.findRecentResults());

        return new ResponseEntity<>(message, headers, HttpStatus.OK);
    }

}
