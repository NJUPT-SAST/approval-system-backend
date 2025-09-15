package fun.sast.entity;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class Competition implements Serializable {

    /** 封面url */
    @JsonProperty("cover")
    @JSONField(name = "cover")
    private String cover;

    /** 活动ID编号 */
    private Long id;

    /** 比赛介绍 */
    @JSONField(name = "introduce")
    private String introduce;

    /** 是否已经审批。 */
    @JSONField(name = "is_review")
    private Integer isReview;

    /** 团队人数限制 */
    @JSONField(name = "max_team_members")
    private Integer maxTeamMembers;

    /** 团队人数限制 */
    @JSONField(name = "min_team_members")
    private Integer minTeamMembers;

    /** 活动负责人学工号 */
    @JSONField(name = "user_code")
    private String userCode;

    /** 比赛名称 */
    @JSONField(name = "name")
    private String name;

    /** 报名开始时间 */
    @JSONField(name = "reg_begin_time")
    private LocalDateTime regBeginTime;

    /** 报名结束时间 */
    @JSONField(name = "reg_end_time")
    private LocalDateTime regEndTime;

    /** 评审开始时间 */
    @JSONField(name = "review_begin_time")
    private LocalDateTime reviewBeginTime;

    /** 评审结束时间 */
    @JSONField(name = "review_end_time")
    private LocalDateTime reviewEndTime;

    /** 审批关系 */
    @JSONField(name = "review_settings")
    private Map<String, String> reviewSettings;

    /** 活动提交开始时间 */
    @JSONField(name = "submit_begin_time")
    private LocalDateTime submitBeginTime;

    /** 活动提交结束时间 */
    @JSONField(name = "submit_end_time")
    private LocalDateTime submitEndTime;

    /** 表单 schema */
    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private JSONObject table;

    /** 0 团队，1 个人 */
    @JSONField(name = "type")
    private Integer type;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Long createUser;

    private Long updateUser;
}
