package tesseract.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TesseractHeartbeatRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private String socket;

    Integer activeCount;

    Integer corePoolSize;

    Integer maximumPoolSize;

    Integer poolSize;

    Integer queueSize;
}
