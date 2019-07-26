package admin.pojo;

import lombok.Data;

@Data
public class UserLoginVO {
    private Integer userId;
    private String token;
    private Boolean passwordInitial;
}
