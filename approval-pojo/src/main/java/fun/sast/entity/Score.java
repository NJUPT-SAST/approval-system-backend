package fun.sast.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("score")
public class Score implements Serializable {
    /** 活动id编号 */
    private Integer comId;

    private String id;

    /** 评委id编号 */
    private Integer judgeId;

    /** 评审意见 */
    @TableField(value = "t_option")
    private String option;

    /** 打分 */
    private Integer score;

    /** 团队是队长id编号，单人是用户id编号 */
    private String userId;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Long createUser;

    private Long updateUser;
}
