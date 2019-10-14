package admin.pojo.VO;

import admin.entity.TesseractFiredJob;
import lombok.Data;

import java.util.List;

@Data
public class FiredTriggerVO {
    private PageVO pageInfo;
    private List<TesseractFiredJob> firedTriggerList;
}
