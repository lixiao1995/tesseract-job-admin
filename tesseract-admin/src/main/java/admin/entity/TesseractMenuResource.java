package admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
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
@ApiModel(value = "TesseractMenuResource对象", description = "")
public class TesseractMenuResource implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键，自增")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "菜单名称")
    private String name;
    /**
     * parent=0为根结点
     */
    @ApiModelProperty(value = "父级菜单ID")
    private Integer parentId;

    @ApiModelProperty(value = "父级菜单名字")
    private String parentName;

    @ApiModelProperty(value = "菜单路由地址")
    private String path;

    @ApiModelProperty(value = "默认转发路由")
    private String redirect;


    @ApiModelProperty(value = "路径匹配模式,保留字段")
    private String urlPattern;

    @ApiModelProperty(value = "菜单的图标")
    private String metaIcon;

    @ApiModelProperty(value = "路由标题")
    private String metaTitle;

    @ApiModelProperty(value = "是否缓存")
    private String metaNoCache;


    @ApiModelProperty(value = "菜单级别，1-一级菜单")
    private Integer level;

    @ApiModelProperty(value = "菜单描述")
    private String menuDesc;

    @ApiModelProperty(value = "菜单顺序")
    private Integer menuOrder;

    @ApiModelProperty(value = "创建人ID")
    private Integer createUserId;

    @ApiModelProperty(value = "创建人姓名")
    private String createUserName;

    @ApiModelProperty(value = "更新人ID")
    private Integer updateUserId;

    @ApiModelProperty(value = "更新人姓名")
    private String updateUserName;

    @ApiModelProperty(value = "是否删除，0-未删除，1-删除")
    @TableField(value = "del_flag")
    private Integer delFlag;

    @ApiModelProperty(value = "状态码，保留字段")
    private Integer status;

    @ApiModelProperty(value = "创建时间")
    private Long createTime;

    @ApiModelProperty(value = "更新时间")
    private Long updateTime;

    @ApiModelProperty(value = "是否一直显示，即使没有子菜单")
    @TableField(value = "always_show_flag")
    private Integer alwaysShowFlag;

}
