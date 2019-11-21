package admin.security;

import admin.entity.TesseractToken;
import admin.service.ITesseractTokenService;
import admin.service.ITesseractUserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import tesseract.exception.TesseractException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @description: 登出控制器
 * @author: 李明
 * @company: 朴新教育
 * @version:
 * @date: 2019/7/10 02:16
 */
@Component
public class TokenLogoutHandler implements LogoutHandler {

    @Autowired
    private ITesseractUserService userService;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String token = request.getHeader("X-Token");
        if (StringUtils.isEmpty(token)) {
            throw new TesseractException("token为空");
        }
        userService.userLogout(token);
    }
}
