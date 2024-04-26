package site.foolish.ary.controller.review;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.foolish.ary.domain.review.Review;
import site.foolish.ary.response.StatusEnum;
import site.foolish.ary.response.dto.Message;
import site.foolish.ary.service.review.CrawlingService;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/crawling")
public class CrawlingController {

    private final CrawlingService crawlingService;

    @GetMapping("/coupang")
    public ResponseEntity<Message> coupangCrawling(@RequestBody String url) throws IOException, InterruptedException {
        Message message = new Message();
        HttpHeaders headers = new HttpHeaders();

        List<Review> reviewList = crawlingService.crawling(url);

        message.setStatus(StatusEnum.OK);
        message.setMessage("크롤링 완료");
        message.setData(reviewList);

        return new ResponseEntity<>(message, headers, HttpStatus.OK);
    }
}
