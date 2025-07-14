package fun.sast.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Score implements Serializable {
    /** 活动id编号 */
    private String comId;

    private String id;

    /** 评委id编号 */
    private Integer judgeId;

    /** 评审意见 */
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
