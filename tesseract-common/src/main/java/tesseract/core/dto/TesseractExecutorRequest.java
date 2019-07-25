package tesseract.core.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class TesseractExecutorRequest {
    @NotNull
    private String className;

    private Integer shardingIndex;

    @NotNull
    private Long logId;

    @NotNull
    private Integer triggerId;

    @NotNull
    private Integer jobId;


    @NotNull
    private Integer executorDetailId;
}
