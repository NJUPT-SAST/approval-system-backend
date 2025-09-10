package fun.sast.vo;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Map;

@Builder
public class CompetitionDetailVO {
    public static final Integer REVIEWED = 0;
    public static final Integer NOT_REVIEWED = 1;

    /** 封面url */
    private String cover;

    /** 活动ID编号 */
    private Long id;

    /**
     * 活动负责人学工号
     */
    private String userCode;

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
    private LocalDateTime regBeginTime;

    /** 报名结束时间 */
    private LocalDateTime regEndTime;

    /** 评审开始时间 */
    private LocalDateTime reviewBeginTime;

    /** 评审结束时间 */
    private LocalDateTime reviewEndTime;

    /** 审批关系 */
    private Map<String, String> reviewSettings;

    /** 活动提交开始时间 */
    private LocalDateTime submitBeginTime;

    /** 活动提交结束时间 */
    private LocalDateTime submitEndTime;

    /** 表单 schema */
    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private JSONObject table;

    /** 0 团队，1 个人 */
    private Integer type;
}
