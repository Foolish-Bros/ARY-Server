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
import site.foolish.ary.dto.review.CrawlingRequest;
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

    @GetMapping("/crawling")
    public ResponseEntity<Message> coupangCrawling(Authentication auth, @RequestBody CrawlingRequest crawlingRequest) throws IOException, InterruptedException, ParseException {
        Message message = new Message();
        HttpHeaders headers = new HttpHeaders();

        Member member = memberService.getLoginMemberByEmail(auth.getName());
        member.setPassword("hidden");

        ReviewList reviewList = reviewService.crawling(crawlingRequest.getUrl(), member, crawlingRequest.getType());

        message.setStatus(StatusEnum.OK);
        message.setMessage("Crawling Succeed");
        message.setData(reviewList);

        return new ResponseEntity<>(message, headers, HttpStatus.OK);
    }

    @PostMapping("/delete")
    public ResponseEntity<Message> deleteReview(@RequestBody String id) throws ParseException {
        Message message = new Message();
        HttpHeaders headers = new HttpHeaders();



        message.setStatus(StatusEnum.OK);
        message.setMessage("Crawling Succeed");
        message.setData(null);

        return null;
    }


}
