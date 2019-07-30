package admin.security;

import admin.annotation.TokenNoCheck;
import admin.constant.AdminConstant;
import admin.service.ITesseractUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
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
        return super.preHandle(request, response, handler);
//        HandlerMethod handlerMethod = (HandlerMethod) handler;
//        TokenNoCheck annotation = handlerMethod.getMethod().getAnnotation(TokenNoCheck.class);
//        if (annotation != null) {
//            return super.preHandle(request, response, handler);
//        }
//        //检测token是否过期
//        String token = request.getHeader(AdminConstant.TOKEN);
//        if (StringUtils.isEmpty(token)) {
//            // 从 cookie 中 取token
//            if (request.getCookies() != null) {
//                for (Cookie cookie : request.getCookies()) {
//                    if (cookie.getName().equals(AdminConstant.TOKEN)) {
//                        token = cookie.getValue();
//                    }
//                }
//            }
//        }
//        // 如果token仍然为空
//        if (StringUtils.isEmpty(token)) {
//            throw new TesseractException("token 不能为空");
//        }
//        //校验
//        if (!tesseractUserService.checkToken(token)) {
//            throw new TesseractException("token 过期，请重新登录");
//        }
//
//        return super.preHandle(request, response, handler);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        super.afterCompletion(request, response, handler, ex);
    }
}
