package tesseract.core.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 〈执行器执行Job情况〉
 *
 * @author nickel
 * @create 2019/7/6
 * @since 1.0.0
 */
@Data
public class TesseractAdminJobNotify implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long logId;

    private Integer jobDetailId;

    private Integer executorDetailId;

    private Integer fireJobId;

    private String exception;

    private Integer shardingIndex;
}
