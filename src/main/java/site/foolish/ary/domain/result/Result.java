package site.foolish.ary.domain.result;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import site.foolish.ary.domain.review.Review;

import java.util.Date;
import java.util.List;

@Document(collection = "result")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Result {
    private String id;
    private List<String> questionList;
    private List<String> answerList;
    private List<Review> reviewList;
    private Date createdAt;
    private Date updatedAt;
}
