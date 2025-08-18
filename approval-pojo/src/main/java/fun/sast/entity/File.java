package fun.sast.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class File implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 比赛ID */
    @TableField("com_id")
    private Long comId;

    /** 队长学号 */
    @TableField("user_code")
    private String userCode;

    /** 输入框名 */
    private String input;

    /** 作品地址 */
    private String url;
}
