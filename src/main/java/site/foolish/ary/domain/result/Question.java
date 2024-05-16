package site.foolish.ary.domain.result;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Question {
    private String question;
    private String answer;
    private Date createdAt;
}
