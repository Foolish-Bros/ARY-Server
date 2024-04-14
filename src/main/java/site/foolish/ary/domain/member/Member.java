package site.foolish.ary.domain.member;

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
//    private String username;

    private String email;
    private String password;
    private String name;

    @Enumerated(EnumType.STRING)
    private Role role;

    // provider : google 이 들어감
    public String provider;

    // providerId : google 로그인 한 유저의 고유 ID가 들어감
    private String providerId;
}
