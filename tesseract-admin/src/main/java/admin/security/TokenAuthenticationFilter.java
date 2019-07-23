package admin.security;

import admin.constant.AdminConstant;
import admin.pojo.UserAuthVO;
import admin.service.ITesseractUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @description: 鉴权过滤器
 * @author: LeoLee
 * @company: ***
 * @version:
 * @date: 2019/7/9 18:07
 */
@Component
public class TokenAuthenticationFilter extends OncePerRequestFilter {


    @Autowired
    private ITesseractUserService tesseractUserService;
    @Autowired
    private UserDetailsService webUserDetailsService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String token = request.getHeader(AdminConstant.TOKEN);
        String servletPath = request.getServletPath();
        try {
            if (!StringUtils.isEmpty(token)) {
                // final String authToken = authHeader.substring(tokenHead.length()); // The part after "Bearer "
                // if (authToken != null && redisTemplate.hasKey(authToken)) {
                //String username = redisTemplate.opsForValue().get(authToken);
                UserAuthVO userAuthVO = tesseractUserService.getUserAuthInfo(token);
                // 如果上面解析 token 成功并且拿到了 username 并且本次会话的权限还未被写入
                if (userAuthVO != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                    UserDetails userDetails = webUserDetailsService.loadUserByUsername(userAuthVO.getName());
                    // 设置用户权限
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    // UserContextHolder2.putUser(userAuthVO);
                    // 验证正常,生成authentication
                    logger.info("authenticated user " + userAuthVO.getName() + ", setting security context");
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } finally {
            filterChain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
        System.out.println("Filter执行完毕...");
        super.destroy();
    }
}
