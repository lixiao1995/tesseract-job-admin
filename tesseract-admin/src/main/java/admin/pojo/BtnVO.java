package admin.pojo;

import admin.entity.TesseractBtnResource;
import admin.entity.TesseractRole;
import lombok.Data;

import java.util.List;

@Data
public class BtnVO {
    private PageVO pageInfo;
    private List<TesseractBtnResource> btnList;
}
