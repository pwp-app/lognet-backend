package app.pwp.lognet.system.controller;

import app.pwp.lognet.utils.common.R;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/error")
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController {
    @RequestMapping("/")
    public R handleError(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (statusCode == 500) {
            return R.error("发生了一个未知的服务器错误");
        } else if (statusCode == 401) {
            return R.unauth("请先登录");
        } else if (statusCode == 403) {
            return R.forbidden("您没有权限访问");
        } else if (statusCode == 404) {
            return R.notfound();
        } else {
            return R.error("发生了一个未知的服务器错误");
        }
    }

    @RequestMapping("/auth")
    public R handleAuthError() {
        return R.unauth("请先登录");
    }

    @Override
    public String getErrorPath() {
        return "/error/";
    }
}
