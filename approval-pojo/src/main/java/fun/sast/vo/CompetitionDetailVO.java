package fun.sast.vo;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 比赛详情VO类 用于替代Map<String, Object>作为getComInfo方法的返回类型 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CompetitionDetailVO implements Serializable {
    /** 比赛名称 */
    private String name;

    /** 封面图片URL */
    private String cover;

    /** 比赛介绍 */
    private String introduce;

    /** 比赛状态（0未开始 1进行中 2已结束） */
    private Integer status;

    /** 报名开始时间 */
    private String regBegin;

    /** 报名结束时间 */
    private String regEnd;

    /** 提交开始时间 */
    private String submitBegin;

    /** 提交结束时间 */
    private String submitEnd;

    /** 评审开始时间 */
    private String reviewBegin;

    /** 评审结束时间 */
    private String reviewEnd;

    /** 比赛类型（0团队赛 1个人赛） */
    private Integer type;

    /** 最小团队人数 */
    private Integer minTeamMembers;

    /** 最大团队人数 */
    private Integer maxTeamMembers;

    /** 是否已报名 */
    private Boolean isSigned;

    /** 是否是团队队长 */
    private Boolean isCaptain;

    /** 比赛ID */
    private Long id;
}
