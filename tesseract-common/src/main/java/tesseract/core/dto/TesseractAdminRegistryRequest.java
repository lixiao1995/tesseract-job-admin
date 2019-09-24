package tesseract.core.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class TesseractAdminRegistryRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    private String ip;
    private Integer port;
    private List<TesseractAdminJobDetailDTO> tesseractAdminJobDetailDTOList;
}
