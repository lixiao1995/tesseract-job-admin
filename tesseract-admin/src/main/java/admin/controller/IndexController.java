package admin.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @program: tesseract-job
 * @description: 转发到主页
 * @author: nickle
 * @create: 2019-08-16 10:23
 **/
@Controller
public class IndexController {
    @RequestMapping("/index")
    @PreAuthorize("permitAll")
    public String index() {
        return "redirect:/static/index.html";
    }
}
