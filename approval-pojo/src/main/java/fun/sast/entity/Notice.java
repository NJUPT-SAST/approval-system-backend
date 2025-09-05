package fun.sast.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Notice implements Serializable {
    /** 公告id编号 */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /** 活动id编号 */
    private Integer comId;

    /** 公告内容 */
    private String content;

    /** 接收公告的角色 */
    private Integer role;

    /** 发出公告的时间 */
    private LocalDateTime time;

    /** 公告标题 */
    private String title;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Long createUser;

    private Long updateUser;

    @TableLogic // 逻辑删除
    private boolean isDeleted = false;

}
