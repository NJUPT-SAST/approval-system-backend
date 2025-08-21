package fun.sast.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 验证码实体 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerifyCodeDTO implements Serializable {
    /** 验证码Key */
    private String key;

    /** 验证码图片，字节数组 */
    private byte[] image;

    /** 验证码文本值 */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String text;
}
