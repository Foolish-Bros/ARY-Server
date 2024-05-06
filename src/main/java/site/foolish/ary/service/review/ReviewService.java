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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    // Divide crawling by url
    public ReviewList crawling(String url, Member member, int type) throws IOException, InterruptedException, ParseException {

        ReviewList reviews = new ReviewList();

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

    // TODO : 쿠팡(제목, 별점평균) 11번가(별점평균) 코드 짜기

    /**
     * 쿠팡 크롤링
     */
    public ReviewList coupangCrawling(String baseUrl, Member member) throws IOException, InterruptedException, ParseException {
        // 입력 받은 URL 주소의 product code 를 추출
        String[] parts = baseUrl.substring(baseUrl.lastIndexOf("/products/") + 1, baseUrl.indexOf("?")).split("/");
        String prodCode = parts[parts.length - 1];

        List<String> urls = new ArrayList<>();

        for (int page = 1; page <= 20; page++) {
            String tempUrl = "https://www.coupang.com/vp/product/reviews?productId=" + prodCode + "&page=" + page + "&size=5&sortBy=ORDER_SCORE_ASC&ratings=&q=&viRoleCode=3&ratingSummary=true";
            urls.add(tempUrl);
        }

        List<Review> reviews = new ArrayList<>();

        Document soupMain = Jsoup.connect(baseUrl)
                .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36")
                .header("authority", "weblog.coupang.com")
                .header("scheme", "https")
                .header("origin", "https://www.coupang.com")
                .header("Sec-ch-ua-mobile", "?0")
                .header("Sec-ch-ua-platform", "macOS")
                .header("Cookie", "PCID=31489593180081104183684; _fbp=fb.1.1644931520418.1544640325; gd1=Y; X-CP-PT-locale=ko_KR; MARKETID=31489593180081104183684; sid=03ae1c0ed61946c19e760cf1a3d9317d808aca8b; overrideAbTestGroup=%5B%5D; x-coupang-origin-region=KOREA; x-coupang-accept-language=ko_KR;")
                .header("referer", "https://www.coupang.com")
                .get();

        String title = soupMain.select("h2.prod-buy-header__title").text().trim();
        float totalRate = Float.parseFloat(soupMain.select("span.rds-rating-score").text().trim());

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
                        Objects.requireNonNull(article.selectFirst("div.sdp-review__article__list__info__product-info__name")).text().trim() : "-";

                // 헤드라인(타이틀)
                String headline = article.selectFirst("div.sdp-review__article__list__headline") != null ?
                        Objects.requireNonNull(article.selectFirst("div.sdp-review__article__list__headline")).text().trim() : "등록된 헤드라인이 없습니다";

                // 리뷰 내용
                String reviewContent = article.selectFirst("div.sdp-review__article__list__review > div") != null ?
                        Objects.requireNonNull(article.selectFirst("div.sdp-review__article__list__review > div")).text().trim() : "등록된 리뷰내용이 없습니다";

                // Date
                String dateString  = article.selectFirst("div.sdp-review__article__list__info__product-info__reg-date") != null ?
                        article.selectFirst("div.sdp-review__article__list__info__product-info__reg-date").text().trim() : "-";

                SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd");
                Date date = formatter.parse(dateString);

                data = Review.builder()
                        .productName(prodName)
                        .rate(rating)
                        .headline(headline)
                        .content(reviewContent)
                        .date(date)
                        .build();

                reviews.add(data);

                Thread.sleep(10);
            }

        }

        // Crawling 끝나는 부분

        ReviewList reviewList = ReviewList.builder()
                .member(member)
                .title(title)
                .totalRate(totalRate)
                .reviews(reviews)
                .url(baseUrl)
                .createdAt(new Date())
                .build();

        reviewRepository.save(reviewList);

        return reviewList;
    }

    /**
     * 11번가 크롤링
     */
    public ReviewList elevenCrawling(String baseUrl, Member member) throws IOException, InterruptedException, ParseException {
        // 입력 받은 URL 주소의 product code 를 추출
        String[] parts = baseUrl.substring(baseUrl.lastIndexOf("/products/") + 1, baseUrl.indexOf("?")).split("/");
        String prodCode = parts[parts.length - 1];

        List<String> urls = new ArrayList<>();

        for (int page = 1; page <= 20; page++) {
            String tempUrl = "https://www.11st.co.kr/product/SellerProductDetail.tmall?method=getProductReviewList&prdNo=" + prodCode + "&page=" + page + "&pageTypCd=first&reviewDispYn=Y";
            urls.add(tempUrl);
        }

        List<Review> reviews = new ArrayList<>();

        Document soupMain = Jsoup.connect(baseUrl)
                .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36")
                .header("authority", "www.11st.co.kr")
                .header("scheme", "https")
                .header("origin", "https://www.11st.co.kr/")
                .header("Sec-ch-ua-mobile", "?0")
                .header("Sec-ch-ua-platform", "macOS")
                .header("Cookie", "_gcl_au=1.1.2002688257.1714358136; _fbp=fb.2.1714358135779.1926387357; _ga=GA1.1.1080026663.1714358136; _ga_6VBF5N51X2=GS1.1.1714358136.1.0.1714358136.60.0.0; PCID=17143581363649815761088; XSRF-TOKEN=47a7d600-1d00-5de8-74ba-353cdca4394e; TP=scrnChk%7CY; AUID=AUID_kE9oh3ri4QqEpPXWmCHmtQ; TT=CONN_IP_LOC%7CDOM; RCPD=2604446520; PCID_FRV=true; DMP_UID=(DMPC)447c4767-0f46-4f33-8de6-60851c7823e5; JSESSIONID=E72AD1958407FDF23BD442A0F17497D4.Tomcat")
                .header("referer", "https://www.11st.co.kr/")
                .get();

        String title = soupMain.select("h1.title").text().trim();
        float totalRate = 0;
        String totalRateString = soupMain.selectFirst("span.c_seller_grade") != null ?
                soupMain.selectFirst("div.meta span.c_seller_grade").text().trim() : "0";
        if(!totalRateString.isEmpty()) {
            String num = totalRateString.replaceAll("\\D", "");
            totalRate = Float.parseFloat(num.split("")[1]+ "." + num.split("")[2]);
        }

        // Crawling 실행되는 부분

        for(String url : urls) {
            Document soup = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36")
                    .header("authority", "www.11st.co.kr")
                    .header("scheme", "https")
                    .header("origin", "https://www.11st.co.kr/")
                    .header("Sec-ch-ua-mobile", "?0")
                    .header("Sec-ch-ua-platform", "macOS")
                    .header("Cookie", "_gcl_au=1.1.2002688257.1714358136; _fbp=fb.2.1714358135779.1926387357; _ga=GA1.1.1080026663.1714358136; _ga_6VBF5N51X2=GS1.1.1714358136.1.0.1714358136.60.0.0; PCID=17143581363649815761088; XSRF-TOKEN=47a7d600-1d00-5de8-74ba-353cdca4394e; TP=scrnChk%7CY; AUID=AUID_kE9oh3ri4QqEpPXWmCHmtQ; TT=CONN_IP_LOC%7CDOM; RCPD=2604446520; PCID_FRV=true; DMP_UID=(DMPC)447c4767-0f46-4f33-8de6-60851c7823e5; JSESSIONID=E72AD1958407FDF23BD442A0F17497D4.Tomcat")
                    .header("referer", "https://www.11st.co.kr/")
                    .get();

            Elements articles = soup.select("div.review_list ul li");

            for(Element article : articles) {
                Review data;

                // product Name
                String prodName = title;
                prodName+= article.selectFirst("div.cfix div.bbs_cont p.option_txt")  != null ?
                        article.selectFirst("div.cfix div.bbs_cont p.option_txt").text().trim() : "제품명이 없습니다.";

                // headline
                // 11번가 review에는 headline이 존재 x
                // String headline = "-";

                // rate
                int rate = 0;
                String rateString = article.selectFirst("span.selr_star") != null ?
                        article.selectFirst("span.selr_star").text().trim() : "0";
                if(!rateString.isEmpty()) {
                    rate = Integer.parseInt(rateString.replaceAll("\\D", "").split("")[1]);
                }

                // content
                String content = article.selectFirst("span.summ_conts") != null ?
                        article.selectFirst("span.summ_conts").text().trim() : "등록된 내용이 없습니다.";

                // date
                String dateString  = article.selectFirst("span.date") != null ?
                        article.selectFirst("span.date").text().trim() : "-";

                SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd");
                Date date = formatter.parse(dateString);

                data = Review.builder()
                        .productName(prodName)
                        .rate(rate)
                        .headline("-")
                        .content(content)
                        .date(date)
                        .build();

                reviews.add(data);

                Thread.sleep(10);
            }

        }

        // Crawling end

        ReviewList reviewList = ReviewList.builder()
                .member(member)
                .title(title)
                .totalRate(totalRate)
                .reviews(reviews)
                .url(baseUrl)
                .createdAt(new Date())
                .build();

        reviewRepository.save(reviewList);

        return reviewList;
    }

    public ReviewList auctionCrawling(String baseUrl, Member member) throws IOException, InterruptedException, ParseException {
        String[] parts = baseUrl.substring(baseUrl.lastIndexOf("?itemno=") + 1).split("=");
        String prodCode = parts[parts.length - 1];

        log.info(prodCode);

        List<String> urls = new ArrayList<>();

        for (int page = 1; page <= 20; page++) {
            String tempUrl = "https://amtour.auction.co.kr/Item/GetReviewList?itemNo=" + prodCode + "&filter=&sort=popular&pageIndex=" + page + "&tourType=";
            urls.add(tempUrl);
        }

        List<Review> reviews = new ArrayList<>();

        Document soupMain = Jsoup.connect(baseUrl)
                .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36")
                .header("authority", "www.auction.co.kr")
                .header("scheme", "https")
                .header("origin", "http://www.auction.co.kr/")
                .header("Sec-ch-ua-mobile", "?0")
                .header("Sec-ch-ua-platform", "macOS")
                .header("Cookie", "_gcl_au=1.1.2002688257.1714358136; _fbp=fb.2.1714358135779.1926387357; _ga=GA1.1.1080026663.1714358136; _ga_6VBF5N51X2=GS1.1.1714358136.1.0.1714358136.60.0.0; PCID=17143581363649815761088; XSRF-TOKEN=47a7d600-1d00-5de8-74ba-353cdca4394e; TP=scrnChk%7CY; AUID=AUID_kE9oh3ri4QqEpPXWmCHmtQ; TT=CONN_IP_LOC%7CDOM; RCPD=2604446520; PCID_FRV=true; DMP_UID=(DMPC)447c4767-0f46-4f33-8de6-60851c7823e5; JSESSIONID=E72AD1958407FDF23BD442A0F17497D4.Tomcat")
                .header("referer", "http://www.auction.co.kr/")
                .get();

        String title = soupMain.select("h1.itemtit").text().trim();

        String totalRateString = soupMain.select("p.text__value").text();

        String ratePart = totalRateString.substring(totalRateString.lastIndexOf("중 ") + 2).split("점")[0];

        float totalRate = Float.parseFloat(ratePart);

        log.info(String.valueOf(totalRate));

        // Crawling 실행되는 부분

        for(String url : urls) {
            Document soup = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36")
                    .header("authority", "www.auction.co.kr")
                    .header("scheme", "https")
                    .header("origin", "http://www.auction.co.kr/")
                    .header("Sec-ch-ua-mobile", "?0")
                    .header("Sec-ch-ua-platform", "macOS")
                    .header("Cookie", "_gcl_au=1.1.2002688257.1714358136; _fbp=fb.2.1714358135779.1926387357; _ga=GA1.1.1080026663.1714358136; _ga_6VBF5N51X2=GS1.1.1714358136.1.0.1714358136.60.0.0; PCID=17143581363649815761088; XSRF-TOKEN=47a7d600-1d00-5de8-74ba-353cdca4394e; TP=scrnChk%7CY; AUID=AUID_kE9oh3ri4QqEpPXWmCHmtQ; TT=CONN_IP_LOC%7CDOM; RCPD=2604446520; PCID_FRV=true; DMP_UID=(DMPC)447c4767-0f46-4f33-8de6-60851c7823e5; JSESSIONID=E72AD1958407FDF23BD442A0F17497D4.Tomcat")
                    .header("referer", "http://www.auction.co.kr/")
                    .get();

            Elements articles = soup.select("body li");
//            log.info(String.valueOf(articles.get(1)));

            for(Element article : articles) {
                Review data;

                // 평점
                int rating;
                String ratingPercent = article.select("div.box__info div.box__star span.image__star-fill").attr("style");
                switch (ratingPercent) {
                    case "width:100%":
                        rating = 5;
                        break;
                    case "width:80%":
                        rating = 4;
                        break;
                    case "width:60%":
                        rating = 3;
                        break;
                    case "width:40%":
                        rating = 2;
                        break;
                    case "width:20%":
                        rating = 1;
                        break;
                    case "width:0%":
                        rating = 0;
                        break;
                    default:
                        continue;
                }

                // 구매자 상품명
                String prodName = title + article.select("span.text__option-selected").text().trim();

                // 헤드라인(타이틀)
                String headline = "-";

                // 리뷰 내용
                article.select("div.box__review-text p.text");
                String reviewContent = article.select("div.box__review-text p.text").text().trim();

                // Date
                String dateString  = article.select("span.text__date").text().trim();

                SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd");
                Date date = formatter.parse(dateString);

                data = Review.builder()
                        .productName(prodName)
                        .rate(rating)
                        .headline(headline)
                        .content(reviewContent)
                        .date(date)
                        .build();

                reviews.add(data);

                Thread.sleep(10);
            }

        }

        // Crawling end

        ReviewList reviewList = ReviewList.builder()
                .member(member)
                .title(title)
                .totalRate(totalRate)
                .reviews(reviews)
                .url(baseUrl)
                .createdAt(new Date())
                .build();

        reviewRepository.save(reviewList);

        return reviewList;
    }
}
