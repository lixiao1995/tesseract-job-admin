package admin.pojo.VO;

import admin.entity.TesseractLog;
import lombok.Data;

import java.util.List;

@Data
public class LogVO {
    private PageVO pageInfo;
    private List<TesseractLog> logList;
}
