package fun.sast.vo;

import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 比赛报名信息VO类 用于替代Map<String, Object>作为getComSignUpInfo方法的返回类型 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CompetitionSignUpInfoVO implements Serializable {
    /** 操作是否成功 */
    private Boolean success;

    /** 比赛ID */
    private Long comId;

    /** 比赛名称 */
    private String comName;

    /** 比赛类型（0团队赛 1个人赛） */
    private Integer type;

    /** 是否是团队赛 */
    private Boolean isTeam;

    /** 是否已审核 */
    private Integer isReviewed;

    /** 最大团队人数 */
    private Integer maxTeamMembers;

    /** 最小团队人数 */
    private Integer minTeamMembers;

    /** 报名开始时间 */
    private String regBeginTime;

    /** 报名结束时间 */
    private String regEndTime;

    /** 提交开始时间 */
    private String submitBeginTime;

    /** 提交结束时间 */
    private String submitEndTime;

    /** 是否已报名 */
    private Boolean hasSignedUp;

    /** 团队ID（如果已报名） */
    private Integer teamId;

    /** 团队名称（如果已报名） */
    private String teamName;

    /** 队长编码（如果已报名） */
    private String captain;

    /** 团队成员列表（如果已报名） */
    private List<TeamMemberVO> members;

    /** 指导老师列表（如果已报名） */
    private List<TeacherMemberVO> teacherMember;

    /** 团队创建时间（如果已报名） */
    private String createTime;

    /** 团队更新时间（如果已报名） */
    private String updateTime;
}
