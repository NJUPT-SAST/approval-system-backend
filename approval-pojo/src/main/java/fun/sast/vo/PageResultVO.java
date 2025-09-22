package fun.sast.vo;

import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 分页结果VO类 用于替代Map<String, Object>作为分页查询结果的返回类型 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PageResultVO<T> implements Serializable {
    /** 总记录数 */
    private Long total;

    /** 当前页数据列表 */
    private List<T> records;

    /** 当前页码 */
    private Integer pageNum;

    /** 每页记录数 */
    private Integer pageSize;
}
