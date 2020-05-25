package app.pwp.lognet.system.controller;

import app.pwp.lognet.utils.common.R;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingController {
    @GetMapping("/ping")
    public R ping() {
        return R.success();
    }
    @GetMapping("/ping/state")
    public R stateCheck() {
        Subject subject = SecurityUtils.getSubject();
        if (subject.isAuthenticated() || subject.isRemembered()){
            return R.success("用户已登录");
        } else {
            return R.unauth("用户未登录");
        }
    }
}
