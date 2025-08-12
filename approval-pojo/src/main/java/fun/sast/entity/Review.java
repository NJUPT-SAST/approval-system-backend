package fun.sast.entity;

import com.alibaba.excel.annotation.format.DateTimeFormat;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Review implements Serializable {
    /** 是否通过 */
    private boolean accept;

    /** 活动id */
    private Integer comId;

    private String id;

    /** 审批人id */
    private Integer judgeId;

    /** 审批意见 */
    private String option;

    /** 队长id */
    private Integer userId;

    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    private Long createUser;

    private Long updateUser;
}
