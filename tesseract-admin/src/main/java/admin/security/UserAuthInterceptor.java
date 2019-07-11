package admin.security;

import admin.constant.AdminConstant;
import admin.pojo.UserAuthVO;
import admin.service.ITesseractUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import tesseract.exception.TesseractException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @description: 权限拦截器，根据token获取用户信息，存放进本地线程变量
 * @author: 李明
 * @company: 朴新教育
 * @version:
 * @date: 2019/7/11 12:37
 */
@Slf4j
public class UserAuthInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private ITesseractUserService tesseractUserService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //此处为业务代码，可以忽略
        String token = request.getHeader(AdminConstant.TOKEN);
        if (StringUtils.isEmpty(token)) {
            // 从 cookie 中 取token
            if (request.getCookies() != null) {
                for (Cookie cookie : request.getCookies()) {
                    if (cookie.getName().equals(AdminConstant.TOKEN)) {
                        token = cookie.getValue();
                    }
                }
            }
        }
        // 如果token仍然为空
        if(StringUtils.isEmpty(token)){
            throw new TesseractException("token 不能为空");
        }
        if(UserContextHolder.getUser() == null){
            UserAuthVO userAuthInfo = tesseractUserService.getUserAuthInfo(token);
            // 保存用户信息到本地线程变量
            UserContextHolder.putUser(userAuthInfo);
        }

        return super.preHandle(request, response, handler);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 上下文属性值清除，防止内存泄漏
        // RequestContextHolder 在执行完毕之后，会自行调用remove
        UserContextHolder.clear();
        super.afterCompletion(request, response, handler, ex);
    }
}
