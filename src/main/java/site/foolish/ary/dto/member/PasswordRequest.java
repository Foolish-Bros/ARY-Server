package site.foolish.ary.dto.member;

import lombok.Data;

@Data
public class PasswordRequest {
    private String oldPassword;
    private String newPassword;
}
