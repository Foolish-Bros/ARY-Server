package site.foolish.ary.service.review;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.foolish.ary.domain.member.Member;
import site.foolish.ary.domain.review.Review;
import site.foolish.ary.domain.review.ReviewList;
import site.foolish.ary.repository.review.ReviewRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public List<Review> crawling(String url, Member member, int type) throws IOException, InterruptedException {

        List<Review> reviews = new ArrayList<>();

        switch (type) {
            case 1:
                reviews = coupangCrawling(url, member);
                break;
            case 2:
                reviews = elevenCrawling(url, member);
                break;
            case 3:
                reviews = auctionCrawling(url, member);
                break;
            default:
                break;
        }

        return reviews;
    }

    public List<Review> coupangCrawling(String baseUrl, Member member) throws IOException, InterruptedException {
        // 입력 받은 URL 주소의 product code 를 추출
        String[] parts = baseUrl.substring(baseUrl.lastIndexOf("/products/") + 1, baseUrl.indexOf("?")).split("/");
        String prodCode = parts[parts.length - 1];

        List<String> urls = new ArrayList<>();

        for (int page = 1; page <= 20; page++) {
            String tempUrl = "https://www.coupang.com/vp/product/reviews?productId=" + prodCode + "&page=" + page + "&size=5&sortBy=ORDER_SCORE_ASC&ratings=&q=&viRoleCode=3&ratingSummary=true";
            urls.add(tempUrl);
        }

        List<Review> reviews = new ArrayList<>();

        String title = "";

        // Crawling 실행되는 part

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
                Review data;

                // 평점
                int rating = 0;
                Element ratingElement = article.selectFirst("div.sdp-review__article__list__info__product-info__star-orange");
                if (ratingElement != null) {
                    rating = Integer.parseInt(ratingElement.attr("data-rating"));
                }

                // 구매자 상품명
                String prodName = article.selectFirst("div.sdp-review__article__list__info__product-info__name") != null ?
                        article.selectFirst("div.sdp-review__article__list__info__product-info__name").text().trim() : "-";

                // title 가져오기
                title = prodName.split(",")[0];

                // 헤드라인(타이틀)
                String headline = article.selectFirst("div.sdp-review__article__list__headline") != null ?
                        article.selectFirst("div.sdp-review__article__list__headline").text().trim() : "등록된 헤드라인이 없습니다";

                // 리뷰 내용
                String reviewContent = article.selectFirst("div.sdp-review__article__list__review > div") != null ?
                        article.selectFirst("div.sdp-review__article__list__review > div").text().trim() : "등록된 리뷰내용이 없습니다";

                data = Review.builder()
                        .productName(prodName)
                        .rate(rating)
                        .headline(headline)
                        .content(reviewContent)
                        .build();

                reviews.add(data);

                Thread.sleep(10);
            }

        }

        // Crawling 끝나는 부분

        // TODO: 별점 평균 가져오기

        ReviewList reviewList = ReviewList.builder()
                .member(member)
                .title(title)
                .reviews(reviews)
                .url(baseUrl)
                .createTime(new Date())
                .build();

        reviewRepository.save(reviewList);

        return reviews;
    }

    public List<Review> elevenCrawling(String baseUrl, Member member) throws IOException, InterruptedException {
        return null;
    }

    public List<Review> auctionCrawling(String baseUrl, Member member) throws IOException, InterruptedException {
        return null;
    }
}
