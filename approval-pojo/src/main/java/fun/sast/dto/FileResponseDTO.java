package fun.sast.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class FileResponseDTO {
    private Integer id; // 文件id

    @JsonProperty("com_id")
    private Integer comId; // 比赛id

    @JsonProperty("user_id")
    private Integer userId; // 队长id（通过user_code查询得到）

    @JsonProperty("name")
    private String input; // 文件名（映射实体类的input字段）

    private String url; // 文件地址
}
