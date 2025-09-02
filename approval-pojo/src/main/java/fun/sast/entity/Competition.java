package fun.sast.entity;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class Competition implements Serializable {
    public static final Integer REVIEWED = 0;
    public static final Integer NOT_REVIEWED = 1;

    /** 封面url */
    private String cover;

    /** 活动ID编号 */
    private Integer id;

    /** 比赛介绍 */
    private String introduce;

    /** 是否已经审批。 */
    private Integer isReview;

    /** 团队人数限制 */
    private Integer maxTeamMembers;

    /** 团队人数限制 */
    private Integer minTeamMembers;

    /** 比赛名称 */
    private String name;

    /** 报名开始时间 */
    private String regBeginTime;

    /** 报名结束时间 */
    private String regEndTime;

    /** 评审开始时间 */
    private String reviewBeginTime;

    /** 评审结束时间 */
    private String reviewEndTime;

    // TODO
    private Object reviewSettings;

    /** 活动提交开始时间 */
    private String submitBeginTime;

    /** 活动提交结束时间 */
    private String submitEndTime;

    /** 表单 schema */
    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private JSONObject table;

    /** 0 团队，1 个人 */
    private Integer type;

    /** 活动负责人学号 */
    private Integer userCode;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Long createUser;

    private Long updateUser;
}
