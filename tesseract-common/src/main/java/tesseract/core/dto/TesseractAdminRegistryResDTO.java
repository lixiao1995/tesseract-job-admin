package tesseract.core.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Data
public class TesseractAdminRegistryResDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<String> notTriggerNameList;
    private List<String> noExecutorList;
    private List<String> repeatJobList;
}
