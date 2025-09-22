package fun.sast.vo;

import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 团队信息VO类 用于替代Map<String, Object>作为getTeamInfo方法的返回类型 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TeamInfoVO implements Serializable {
    /** 操作是否成功 */
    private Boolean success;

    /** 团队ID */
    private Integer teamId;

    /** 团队名称 */
    private String teamName;

    /** 队长编码 */
    private String captain;

    /** 团队成员列表 */
    private List<TeamMemberVO> teamMember;
}
