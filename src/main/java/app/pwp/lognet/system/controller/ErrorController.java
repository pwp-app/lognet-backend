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
            return R.error("Unknown internal error occurred.");
        } else if (statusCode == 401) {
            return R.unauth("Login first.");
        } else if (statusCode == 403) {
            return R.forbidden("You're not allowed to access here.");
        } else if (statusCode == 404) {
            return R.noufound();
        } else {
            return R.error("Unknown internal error occurred.");
        }
    }

    @RequestMapping("/auth")
    public R handleAuthError() {
        return R.unauth("Login first.");
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }
}
