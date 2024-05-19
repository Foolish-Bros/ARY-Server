package site.foolish.ary.controller.review;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import site.foolish.ary.domain.member.Member;
import site.foolish.ary.domain.review.Review;
import site.foolish.ary.domain.review.ReviewList;
import site.foolish.ary.dto.review.CrawlingMoreRequest;
import site.foolish.ary.dto.review.CrawlingRequest;
import site.foolish.ary.dto.review.ReviewDeleteRequest;
import site.foolish.ary.dto.review.ReviewIdRequest;
import site.foolish.ary.response.StatusEnum;
import site.foolish.ary.response.dto.Message;
import site.foolish.ary.service.member.MemberService;
import site.foolish.ary.service.review.ReviewService;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/review")
public class ReviewController {

    private final ReviewService reviewService;
    private final MemberService memberService;

    @PostMapping("/crawling")
    public ResponseEntity<Message> crawling(Authentication auth, @RequestBody CrawlingRequest crawlingRequest) throws IOException, InterruptedException, ParseException {
        Message message = new Message();
        HttpHeaders headers = new HttpHeaders();

        Member member = memberService.getLoginMemberByEmail(auth.getName());
        member.setPassword("hidden");

        ReviewList reviewList = reviewService.crawling(crawlingRequest.getUrl(), member, crawlingRequest.getType());

        message.setStatus(StatusEnum.OK);
        message.setSuccess(true);
        message.setMessage("Crawling Succeed");
        message.setData(reviewList);

        return new ResponseEntity<>(message, headers, HttpStatus.OK);
    }

    @PostMapping("/crawling/more")
    public ResponseEntity<Message> crawlingMore(@RequestBody CrawlingMoreRequest request) throws IOException, InterruptedException, ParseException {
        Message message = new Message();
        HttpHeaders headers = new HttpHeaders();

        ReviewList reviewList = reviewService.crawling(request.getId(), request.getTimes());

        message.setStatus(StatusEnum.OK);
        message.setSuccess(true);
        message.setMessage("Crawled for" + request.getTimes() + " Succeed");
        message.setData(reviewList);

        return new ResponseEntity<>(message, headers, HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Message> deleteReview(@RequestBody ReviewDeleteRequest request) throws ParseException {
        Message message = new Message();
        HttpHeaders headers = new HttpHeaders();

        log.info(request.getId());

        if(reviewService.deleteReview(request.getId())) {
            message.setStatus(StatusEnum.OK);
            message.setSuccess(true);
            message.setMessage("Delete Succeed");
        } else {
            message.setStatus(StatusEnum.FAILED);
            message.setSuccess(false);
            message.setMessage("Delete Failed");
        }

        return new ResponseEntity<>(message, headers, HttpStatus.OK);
    }

    @GetMapping("/load")
    public ResponseEntity<Message> crawlingMore(@RequestBody ReviewIdRequest request) {
        Message message = new Message();
        HttpHeaders headers = new HttpHeaders();

        ReviewList reviewList = reviewService.getReviewById(request.getId());

        message.setStatus(StatusEnum.OK);
        message.setSuccess(true);
        message.setMessage("리뷰정보 조회 완료");
        message.setData(reviewList);

        return new ResponseEntity<>(message, headers, HttpStatus.OK);
    }

}
