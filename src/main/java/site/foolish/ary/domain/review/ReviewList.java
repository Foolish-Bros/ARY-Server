package site.foolish.ary.domain.review;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import site.foolish.ary.domain.member.Member;

import java.util.Date;
import java.util.List;

@Document(collection = "reviewList")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewList {
    private String id;
    private String url;
    private String mallName;
    private String title;
    private float totalRate;
    private Member member;
    private List<Review> reviews;
    private Date createdAt;
}
