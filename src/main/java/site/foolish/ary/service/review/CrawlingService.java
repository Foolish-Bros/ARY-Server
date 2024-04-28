package site.foolish.ary.service.review;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.foolish.ary.domain.review.Review;
import site.foolish.ary.repository.review.ReviewRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CrawlingService {

    private final ReviewRepository reviewRepository;

    public List<Review> crawling(String url) throws IOException, InterruptedException {

        // 입력 받은 URL 주소의 product code 를 추출
        String[] parts = url.substring(url.lastIndexOf("/products/") + 1, url.indexOf("?")).split("/");
        String prodCode = parts[parts.length - 1];

        log.info(prodCode);

        List<String> urls = new ArrayList<>();
        for (int page = 1; page <= 2; page++) {
            // String tempUrl = "https://www.coupang.com/vp/products/6489163501?itemId=14238595532&vendorItemId=81483824120&src=0&spec=0&addtag=400&ctag=6489163501&lptag=%22%22&itime=20240428172417&wPcid=17090510975508450099880&wRef=&wTime=20240428172417&redirect=landing&isAddedCart=";
            String tempUrl = "https://www.coupang.com/vp/product/reviews?productId=" + prodCode + "&page=" + page + "&size=5&sortBy=ORDER_SCORE_ASC&ratings=&q=&viRoleCode=3&ratingSummary=true";
            urls.add(tempUrl);
        }

        return coupangCrawling(urls, prodCode);
    }

    public List<Review> coupangCrawling(List<String> urls, String prodCode) throws IOException, InterruptedException {
        List<Review> reviews = new ArrayList<>();


        for(String url : urls) {
            Document soup = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36")
                    .header("authority", "weblog.coupang.com")
                    .header("scheme", "https")
                    .header("origin", "https://www.coupang.com")
                    .header("Sec-ch-ua-mobile", "?0")
                    .header("Sec-ch-ua-platform", "macOS")
                    .header("Cookie", "PCID=31489593180081104183684; _fbp=fb.1.1644931520418.1544640325; gd1=Y; X-CP-PT-locale=ko_KR; MARKETID=31489593180081104183684; sid=03ae1c0ed61946c19e760cf1a3d9317d808aca8b; overrideAbTestGroup=%5B%5D; x-coupang-origin-region=KOREA; x-coupang-accept-language=ko_KR;")
                    .header("referer", "https://www.coupang.com")
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

    public List<Review> elevenCrawling(List<String> urls, String prodCode) throws IOException, InterruptedException {
        reviewRepository.save(null);
        return null;
    }

    public List<Review> auctionCrawling(List<String> urls, String prodCode) throws IOException, InterruptedException {
        return null;
    }
}
