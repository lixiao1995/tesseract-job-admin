package admin.pojo.VO;

import lombok.Data;

@Data
public class PageVO {
    private Long currentPage;
    private Long pageSize;
    private Long total;
}
