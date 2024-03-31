package site.foolish.ary.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import site.foolish.ary.domain.member.entity.Member;
import site.foolish.ary.domain.member.entity.Role;

@Getter
@Setter
@NoArgsConstructor
public class JoinRequest {

    @NotBlank(message = "email을 입력하세요.")
    private String email;

    @NotBlank(message = "비밀번호를 입력하세요.")
    private String password;
    private String passwordCheck;

    @NotBlank(message = "이름을 입력하세요.")
    private String name;

    public Member toEntity() {
        return Member.builder()
                .email(this.email)
                .password(this.password)
                .name(this.name)
                .role(Role.USER)
                .build();
    }
}
