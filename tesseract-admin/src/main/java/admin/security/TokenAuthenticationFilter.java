package admin.security;

import admin.constant.AdminConstant;
import admin.entity.TesseractToken;
import admin.pojo.VO.CommonResponseVO;
import admin.pojo.VO.UserAuthVO;
import admin.service.ITesseractUserService;
import com.alibaba.fastjson.JSON;
import io.netty.handler.codec.http.HttpHeaderValues;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import tesseract.exception.TesseractException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @description: 鉴权过滤器
 * @author: LeoLee
 * @company: ***
 * @version:
 * @date: 2019/7/9 18:07
 */
@Component
@Slf4j
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private ITesseractUserService tesseractUserService;
    @Autowired
    private UserDetailsService webUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader(AdminConstant.TOKEN);
        if (!StringUtils.isEmpty(token)) {
            try {
                TesseractToken userToken = tesseractUserService.getUserToken(token);
                if (userToken == null) {
                    responseNoToken(response, "用户token查询为空");
                    return;
                }
                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = webUserDetailsService.loadUserByUsername(userToken.getUserName());
                    // 设置用户权限
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.info("设置用户权限上下文: {}", userDetails.getUsername());
                }
                filterChain.doFilter(request, response);
            } catch (Exception e) {
                log.error(e.toString());
                if (e instanceof TesseractException) {
                    responseNoToken(response, ((TesseractException) e).getMsg());
                } else {
                    responseNoToken(response, "权限设置发生错误");
                }
            }
        } else {
            filterChain.doFilter(request, response);
        }

    }

    private void responseNoToken(HttpServletResponse response, String msg) throws IOException {
        CommonResponseVO responseVO = CommonResponseVO.fail(TesseractException.TOKEN_INVALID_STATUS, msg, null);
        String jsonString = JSON.toJSONString(responseVO);
        response.setStatus(200);
        response.setContentType(HttpHeaderValues.APPLICATION_JSON.toString());
        response.setCharacterEncoding("utf-8");
        response.setContentLength(jsonString.getBytes().length);
        PrintWriter writer = response.getWriter();
        writer.print(jsonString);
        writer.flush();
    }
}
