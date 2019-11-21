package tesseract.core.context;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExecutorContext {
    private Integer shardingIndex;
    private Object param;
}
