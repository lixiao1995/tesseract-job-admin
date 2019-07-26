package admin.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @description: 权限对象
 * @author: 李明
 * @company: 朴新教育
 * @version:
 * @date: 2019/7/25 17:42
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "Permission对象", description = "")
public class Permission {

    @ApiModelProperty(value = "菜单唯一code")
    private String menuCode;
    @ApiModelProperty(value = "按钮唯一code")
    private String btnCode;
    @ApiModelProperty(value = "唯一权限值")
    private String permissionValue;

    public String getPermissionValue() {
        return menuCode.concat("-").concat(btnCode);
    }

}
