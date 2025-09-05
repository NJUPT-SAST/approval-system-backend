package fun.sast.vo;

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
public class NoticeOperateVO implements Serializable{
    private Integer id;
    private Integer comId;
    /** 公告标题 */
    private String title;

    /** 公告内容 */
    private String content;

    /** 接受公告的角色 */
    private Integer role;

    /** 发出公告的时间 */
    private LocalDateTime time;

}
