package admin.pojo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class UserLoginDO {
    @NotNull
    private Integer id;
    @NotBlank
    private String password;
}
