package fun.sast.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("white_list")
public class WhiteList {
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 比赛ID 关联competition表的id字段 */
    private Integer comId;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> userCodes;
}
