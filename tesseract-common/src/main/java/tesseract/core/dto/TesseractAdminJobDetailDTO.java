package tesseract.core.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author lyp
 */
@Data
public class TesseractAdminJobDetailDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String className;

    private String triggerName;
}
