package admin.pojo.DO;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UserDO {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
}
