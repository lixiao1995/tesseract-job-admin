package admin.pojo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class TesseractRoleDO {
    private Integer roleId;
    @NotBlank
    private String roleName;
    private List<TesseractMenuDO> menuInfo;
}
