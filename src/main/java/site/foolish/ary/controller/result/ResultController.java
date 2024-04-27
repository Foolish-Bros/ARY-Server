package site.foolish.ary.controller.result;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.foolish.ary.domain.member.Member;
import site.foolish.ary.domain.result.Result;
import site.foolish.ary.response.dto.Message;
import site.foolish.ary.service.result.ResultService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/result")
public class ResultController {

    private final ResultService resultService;

    // 기록 불러올 때 사용
    public ResponseEntity<Message> getResultList(Member member) {
        return null;
    }

    // 개별 기록 불러올 때
    public ResponseEntity<Message> getResult(Member member, String id) {
        return null;
    }

    // 기록 저장할 때 사용
    public ResponseEntity<Message> addResult(Member member, Result result) {
        return null;
    }

    // 기록 업데이트할 때 사용
    public ResponseEntity<Message> updateResult(Member member, Result result) {
        return null;
    }

    // 삭제할 때
    public ResponseEntity<Message> deleteResult(Member member, Result result) {
        return null;
    }

}

