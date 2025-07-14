package fun.sast.vo;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class UserLoginVO implements Serializable {
    private String token;
    private String name;
    private Integer depId;
    private Integer role;
}
