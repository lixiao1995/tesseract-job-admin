package admin.pojo.VO;

import admin.entity.TesseractMenuResource;

import java.io.Serializable;
import java.util.List;

/**
 * @description: 按钮资源类
 * @author: 李明
 * @company: 朴新教育
 * @version:
 * @date: 2019/7/10 10:07
 */
public class TesseractBtnResourceVo implements Serializable {
    private List<String> roles;
    private List<TesseractMenuResource> menuList;
    private List<String> btnList;

    private String avatar;
    private String introduction;
}
