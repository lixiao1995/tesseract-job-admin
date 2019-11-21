package tesseract.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TesseractException extends RuntimeException {
    /**
     * 状态常量
     */
    public static final int TOKEN_INVALID_STATUS = 505;
    public static final int DEFAULT_STATUS = 500;


    private String msg;
    private int status = DEFAULT_STATUS;

    public TesseractException(Integer status, String msg) {
        super(msg);
        this.msg = msg;
        this.status = status;
    }

    public TesseractException(String msg) {
        super(msg);
        this.msg = msg;
    }
}
