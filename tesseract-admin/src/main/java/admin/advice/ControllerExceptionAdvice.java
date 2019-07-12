package admin.advice;

import admin.pojo.CommonResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tesseract.exception.TesseractException;

@RestControllerAdvice
@Slf4j
public class ControllerExceptionAdvice {

    /**
     * Authorization   授权，鉴权
     * @param e
     * @return
     */
    @ExceptionHandler(value = {AccessDeniedException.class, AuthorizationServiceException.class})
    public CommonResponseVO accessDeniedExceptionHandler(Exception e) {
        e.printStackTrace();
        log.error(e.getMessage());
        return CommonResponseVO.fail(401, "鉴权失败", null);
    }


    /**
     *
     * Authentication  身份验证
     * @param e
     * @return
     */
    @ExceptionHandler(AuthenticationException.class)
    public CommonResponseVO authenticationExceptionHandler(Exception e) {
        e.printStackTrace();
        log.error(e.getMessage());
        return CommonResponseVO.fail(401, "身份验证失败", null);
    }


    @ExceptionHandler(TesseractException.class)
    public CommonResponseVO tesseractExceptionExceptionHandler(TesseractException e) {
        e.printStackTrace();
        log.error(e.getMsg());
        return CommonResponseVO.fail(e.getStatus(), e.getMsg(), null);
    }

    @ExceptionHandler(Exception.class)
    public CommonResponseVO commonExceptionHandler(Exception e) {
        e.printStackTrace();
        log.error(e.getMessage());
        return CommonResponseVO.FAIL;
    }



}
