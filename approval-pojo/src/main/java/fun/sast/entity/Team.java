package fun.sast.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("team")
public class Team implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 比赛ID */
    @TableField("com_id")
    private Long comId;

    /** 队伍名称（仅团队赛） */
    private String name;

    /** 队长学号 */
    private String captain;

    /** 成员JSON，不含队长 */
    private String member;

    /** 指导老师JSON */
    private String teacher;

    /** 创建用户ID */
    private Long createUser;

    /** 更新用户ID */
    private Long updateUser;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
