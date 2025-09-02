package fun.sast.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Work implements Serializable {
    private Long id;

    /** 比赛ID */
    private Long comId;

    /** 队长学号 */
    private String userCode;

    /** 作品名称 */
    private String workName;

    /** 由XRender渲染的表单数据 */
    private String schemaContent;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Long createUser;

    private Long updateUser;
}
