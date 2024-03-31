package site.foolish.ary.domain.member.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "member")
//@Entity
@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Member {

//    @Id
//    private String id;

    private String email;
    private String password;
    private String name;
    private Role role;
}
