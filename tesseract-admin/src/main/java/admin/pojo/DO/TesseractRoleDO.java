package admin.pojo.DO;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class TesseractRoleDO {
    private Integer roleId;
    @NotBlank
    private String roleName;

    @NotBlank
    private String roleDesc;

    private List<TesseractMenuDO> menuInfo;
}
