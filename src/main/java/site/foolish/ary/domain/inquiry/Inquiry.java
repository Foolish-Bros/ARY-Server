package site.foolish.ary.domain.inquiry;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import site.foolish.ary.domain.member.Member;

import java.util.Date;

@Document(collection = "inquiry")
//@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Inquiry {
    private String id;
    private Member member;
    private String content;
    private Date createdAt;
}
