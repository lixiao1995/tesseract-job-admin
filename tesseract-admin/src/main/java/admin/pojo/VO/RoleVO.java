package admin.pojo.VO;

import admin.entity.TesseractRole;
import lombok.Data;

import java.util.List;

/**
 * @description: 角色交互实体
 * @author: 李明
 * @company: 朴新教育
 * @version:
 * @date: 2019/7/12 15:36
 */
@Data
public class RoleVO {
    private PageVO pageInfo;
    private List<TesseractRole> roleList;
}
