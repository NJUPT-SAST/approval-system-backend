package fun.sast.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {

    /** 部门编号 */
    private Integer depId;

    /** 用户id编号 */
    private Integer id;

    /** 用户姓名 */
    private String name;

    /** 用户学号，和Apifox上的有冲突 */
    private String code;

    /** 用户密码 */
    private String password;

    /** 角色 */
    private Integer role;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Long createUser;

    private Long updateUser;
}
