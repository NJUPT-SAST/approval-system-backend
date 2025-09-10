package fun.sast.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@TableName("team")
public class Team implements Serializable {

    @TableId(type = IdType.AUTO)
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

    /**
     * 队长学号
     */
    private String captain;

    /**
     * 成员JSON，不含队长
     */
    private String member;

    /**
     * 指导老师JSON
     */
    private String teacher;
}
