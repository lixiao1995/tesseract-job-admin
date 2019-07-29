package admin.pojo.VO;

import admin.entity.TesseractBtnResource;
import lombok.Data;

import java.util.List;

@Data
public class BtnVO {
    private PageVO pageInfo;
    private List<TesseractBtnResource> btnList;
}
