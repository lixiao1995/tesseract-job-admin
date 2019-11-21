package tesseract.core.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class TesseractStopTaskRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer fireJobId;
}
