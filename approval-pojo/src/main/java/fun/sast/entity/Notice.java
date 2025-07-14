package fun.sast.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Notice implements Serializable {
    /** 活动id编号 */
    private Integer comId;

    /** 公告内容 */
    private String content;

    /** 公告id编号 */
    private Integer id;

    /** */
    private long role;

    /** 发出公告的时间 */
    private String time;

    /** 公告标题 */
    private String title;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Long createUser;

    private Long updateUser;
}
