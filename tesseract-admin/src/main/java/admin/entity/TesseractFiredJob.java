package admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author nickle
 * @since 2019-07-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "TesseractFiredJob对象", description = "")
public class TesseractFiredJob implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键，自增")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "关联触发器id")
    private Integer triggerId;


    @ApiModelProperty(value = "触发器名")
    private String triggerName;

    @ApiModelProperty(value = "执行类名")
    private String className;

    @ApiModelProperty(value = "任务id")
    private Integer jobId;

    @ApiModelProperty(value = "关联机器id")
    private Integer executorDetailId;

    @ApiModelProperty(value = "执行机器：ip:端口")
    private String socket;

    @ApiModelProperty(value = "创建时间")
    private Long createTime;

    @ApiModelProperty(value = "关联日志id")
    private Long logId;


    @ApiModelProperty(value = "重复次数")
    private Integer retryCount;

    @ApiModelProperty(value = "分片索引")
    private Integer shardingIndex;

}
