package fun.sast.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WhiteList {
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 比赛ID 关联competition表的id字段 */
    private Long comId;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> userCodes;
}
