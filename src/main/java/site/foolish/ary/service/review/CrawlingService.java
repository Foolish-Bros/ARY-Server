package site.foolish.ary.service.review;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.foolish.ary.domain.review.Review;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CrawlingService {

    public List<Review> crawling(String url) throws IOException, InterruptedException {

        // 입력 받은 URL 주소의 product code 를 추출
        String prodCode = url.substring(url.lastIndexOf("/products/") + 1, url.indexOf("?"));

        List<String> urls = new ArrayList<>();
        for (int page = 1; page <= 10; page++) {
            String tempUrl = "https://www.coupang.com/vp/product/reviews?productId=" + prodCode + "&page=" + page + "&size=5&sortBy=ORDER_SCORE_ASC&ratings=&q=&viRoleCode=3&ratingSummary=true";
            urls.add(tempUrl);
        }

        return coupangCrawling(urls, prodCode);
    }

    public List<Review> coupangCrawling(List<String> urls, String prodCode) throws IOException, InterruptedException {
        List<Review> reviews = new ArrayList<>();


        for(String url : urls) {
            Document soup = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.4.1 Safari/605.1.15")
                    .header("scheme","https")
                    .header("accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
                    .header("accept-encoding","gzip,deflate,br")
                    .header("accept-language","ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7,es;q=0.6")
                    .header("cache-control","no-cache")
                    .header("pragma","no-cache")
                    .header("upgrade-insecure-requests","1")
                    .ignoreContentType(true).timeout(10000)
                    .get();

            Elements articles = soup.select("article.sdp-review__article__list");

            for (Element article : articles) {
                Review data = new Review();

                // 평점
                int rating = 0;
                Element ratingElement = article.selectFirst("div.sdp-review__article__list__info__product-info__star-orange");
                if (ratingElement != null) {
                    rating = Integer.parseInt(ratingElement.attr("data-rating"));
                }

                // 구매자 상품명
                String prodName = article.selectFirst("div.sdp-review__article__list__info__product-info__name") != null ?
                        article.selectFirst("div.sdp-review__article__list__info__product-info__name").text().trim() : "-";

                // 헤드라인(타이틀)
                String headline = article.selectFirst("div.sdp-review__article__list__headline") != null ?
                        article.selectFirst("div.sdp-review__article__list__headline").text().trim() : "등록된 헤드라인이 없습니다";

                // 리뷰 내용
                String reviewContent = article.selectFirst("div.sdp-review__article__list__review > div") != null ?
                        article.selectFirst("div.sdp-review__article__list__review > div").text().trim().replaceAll("\\s", "") : "등록된 리뷰내용이 없습니다";

                Review.builder()
                        .productName(prodName)
                        .rate(rating)
                        .headline(headline)
                        .content(reviewContent)
                        .build();

                reviews.add(data);

                Thread.sleep(10);
            }
        }

        return reviews;
    }

}
