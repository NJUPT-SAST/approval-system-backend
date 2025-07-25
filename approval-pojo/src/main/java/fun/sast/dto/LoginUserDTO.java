package fun.sast.dto;

import lombok.Data;

@Data
public class LoginUserDTO {
    private String code;
    private String password;
}
