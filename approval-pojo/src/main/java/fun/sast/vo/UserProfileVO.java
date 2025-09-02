package fun.sast.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserProfileVO implements Serializable {
    private String name;
    private String code;

    @JsonProperty("college")
    private String departmentName;

    private String major;
    private String contact;
}
