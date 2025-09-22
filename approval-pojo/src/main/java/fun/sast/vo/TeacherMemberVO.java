package fun.sast.vo;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 指导老师VO类 用于表示团队中的指导老师信息 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TeacherMemberVO implements Serializable {
    /** 老师ID */
    private Integer id;

    /** 老师编码 */
    private String code;

    /** 老师姓名 */
    private String name;

    /** 学院ID */
    private Integer depId;

    /** 学院名称 */
    private String departmentName;

    /** 联系方式 */
    private String contact;
}
