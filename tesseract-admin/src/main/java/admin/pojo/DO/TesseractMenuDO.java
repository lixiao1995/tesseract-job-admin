package admin.pojo.DO;

import lombok.Data;

import java.util.List;

@Data
public class TesseractMenuDO {
    private Integer menuId;
    private String menuName;
    private List<BtnDO> btnList;
}
