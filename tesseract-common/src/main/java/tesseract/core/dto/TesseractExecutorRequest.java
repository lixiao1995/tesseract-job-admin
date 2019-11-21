package tesseract.core.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 任务执行请求
 *
 * @author nickle
 */
@Data
public class TesseractExecutorRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private String className;

    private Integer shardingIndex;

    private Object param;

    private Long logId;

    private Integer fireJobId;
}
