package fun.sast.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Team {

    /** 队伍id */
    private Long id;

    /**
     * 比赛ID
     */
    @TableField("com_id")
    private Long comId;

    /**
     * 队伍名称（仅团队赛）
     */
    private String name;

    /** 队长id */
    private Integer userId;

    /**
     * 成员JSON，不含队长
     */
    private String member;

    /**
     * 指导老师JSON
     */
    private String teacher;
}
