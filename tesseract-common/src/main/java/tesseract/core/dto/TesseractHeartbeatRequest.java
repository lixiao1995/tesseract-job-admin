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

    private Integer port;

    private Integer activeCount;

    private Integer corePoolSize;

    private Integer maximumPoolSize;

    private Integer poolSize;

    private Integer queueSize;
}
