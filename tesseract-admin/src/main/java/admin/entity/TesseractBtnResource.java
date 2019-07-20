package admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 *
 * </p>
 *
 * @author nickle
 * @since 2019-07-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "TesseractBtnResource对象", description = "")
public class TesseractBtnResource implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键，自增")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "按钮名称")
    private String btnName;

    @ApiModelProperty(value = "所属菜单ID")
    private Integer menuId;

    @ApiModelProperty(value = "所属菜单名")
    private String menuName;

    @ApiModelProperty(value = "所属菜单路径")
    private String menuPath;

    @ApiModelProperty(value = "创建人ID")
    private Integer createUserId;

    @ApiModelProperty(value = "创建人姓名")
    private String createUserName;

    @ApiModelProperty(value = "更新人ID")
    private Integer updateUserId;

    @ApiModelProperty(value = "更新人姓名")
    private String updateUserName;

    @ApiModelProperty(value = "状态码，保留字段")
    private Integer status;

    @ApiModelProperty(value = "创建时间")
    private Long createTime;

    @ApiModelProperty(value = "更新时间")
    private Long updateTime;


}
