package fun.sast.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Review implements Serializable {

    private String id;

    /** 是否通过 */
    private boolean accept;

    /** 比赛id */
    private Long comId;

    /** 队长学号 */
    private String code;

    /** 审批人id */
    private Integer judgeId;

    /** 审批意见 */
    private String opinion;

    /** 队长id */
    private Integer userId;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Long createUser;

    private Long updateUser;
}
