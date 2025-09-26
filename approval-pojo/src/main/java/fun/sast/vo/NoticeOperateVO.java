package fun.sast.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class NoticeOperateVO implements Serializable {

    private Integer id;

    @JsonProperty("com_id")
    private Integer comId;

    /** 公告标题 */
    private String title;

    /** 公告内容 */
    private String content;

    /** 接受公告的角色 */
    private Integer role;

    /** 发出公告的时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime time;
}
