package fun.sast.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 验证码实体 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerifyCode implements Serializable {
    /** 验证码Key */
    private String key;

    /** 验证码图片，base64压缩后的字符串 */
    private String image;

    /** 验证码文本值 */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String text;
}
