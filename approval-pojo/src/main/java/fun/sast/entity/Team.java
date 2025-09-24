package fun.sast.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Team implements Serializable {

    /** 队伍ID */
    private Long id;

    /** 比赛ID */
    private Long comId;

    /** 队伍名称（仅团队赛） */
    private String name;

    /** 队长学号 */
    private String captain;

    /** 成员JSON，不含队长 */
    private String member;

    /** 指导老师JSON */
    private String teacher;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Long createUser;

    private Long updateUser;
}
