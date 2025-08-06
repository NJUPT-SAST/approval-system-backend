package fun.sast.controller.publicController;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;


@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UserResponse {
    private String code;
    private String name;
    private String college;
    private String major;
    private String contact;
}
