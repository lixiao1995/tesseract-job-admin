package admin.security;

import admin.pojo.UserAuthVO;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * @description: 保存用户登录信息
 * @author: 李明
 * @company: ***
 * @version:
 * @date: 2019/7/11 12:28
 */
public class UserContextHolder {

    // 注意: 自定义的 ThreadLocal 存在内存泄露问题，

    private static final ThreadLocal<UserAuthVO> threadLocal = new ThreadLocal<>();

    /**
     * 将用户信息放入
     * @param userAuthVO
     */
    public static void putUser(UserAuthVO userAuthVO) {
        threadLocal.set(userAuthVO);
    }

    /**
     * 获取用户信息
     * @return
     */
    public static UserAuthVO getUser() {
        return threadLocal.get();
    }


    public static void clear(){
        threadLocal.remove();
    }

}
