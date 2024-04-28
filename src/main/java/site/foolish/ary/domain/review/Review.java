package site.foolish.ary.domain.review;


import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import site.foolish.ary.domain.member.Member;

@Document(collection = "review")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    private String productName;
    private String headline;
    private double rate;
    private String content;
}
