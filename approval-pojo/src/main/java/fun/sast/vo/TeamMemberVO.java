package fun.sast.vo;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 团队成员VO类 用于表示团队中的成员信息 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TeamMemberVO implements Serializable {
    /** 成员ID */
    private Integer id;

    /** 成员编码 */
    private String code;

    /** 成员姓名 */
    private String name;

    /** 学院ID */
    private Integer depId;

    /** 学院名称 */
    private String departmentName;

    /** 专业 */
    private String major;

    /** 联系方式 */
    private String contact;
}
