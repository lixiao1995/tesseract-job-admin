package admin.pojo.VO;

import admin.entity.TesseractMenuResource;
import lombok.Data;

import java.util.List;

/**
 * @description: 菜单交互实体
 * @author: 李明
 * @company: 朴新教育
 * @version:
 * @date: 2019/7/12 12:30
 */
@Data
public class MenuVO {
    private PageVO pageInfo;
    private List<TesseractMenuResource> menuList;

}
