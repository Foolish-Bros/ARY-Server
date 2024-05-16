package site.foolish.ary.domain.result;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import site.foolish.ary.domain.member.Member;

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
    private Member member;
    private List<Question> questionList;
    private String reviewId;
    private Date createdAt;
    private Date updatedAt;
}
