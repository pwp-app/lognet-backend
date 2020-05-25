package app.pwp.lognet.system.controller;

import app.pwp.lognet.system.form.UserLogin;
import app.pwp.lognet.system.model.User;
import app.pwp.lognet.system.service.UserLoginLogService;
import app.pwp.lognet.utils.auth.ReCaptcha;
import app.pwp.lognet.system.service.UserService;
import app.pwp.lognet.utils.common.R;
import app.pwp.lognet.utils.geo.CZIPStringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/portal")
public class PortalController {
    @Resource
    private UserService userService;
    @Resource
    private UserLoginLogService userLoginLogService;
    @Resource
    private ReCaptcha reCaptcha;
    @PostMapping("/login")
    public R login(@Valid UserLogin form, HttpServletRequest req) throws Exception {
        Subject subject = SecurityUtils.getSubject();
        // 用户重复登录了，返回成功放行
        if (subject.isAuthenticated()){
            return R.success("用户已登录");
        }
        UsernamePasswordToken token = new UsernamePasswordToken(form.getUsername(), form.getPassword());
        token.setRememberMe(form.isRememberMe());
        try {
            subject.login(token);
        } catch (UnknownAccountException | IncorrectCredentialsException e) {
            return R.unauth("用户名或密码不正确");
        } catch (ExcessiveAttemptsException eae) {
            return R.error("登录错误次数过多，请稍后再试");
        } catch (AuthenticationException ae) {
            return R.error("服务器出现错误，登录失败");
        }
        // 验证Recaptcha
        if (!reCaptcha.verify(form.getToken()).get()) {
            return R.badRequest("系统检测到您可能为机器人，登录失败");
        }
        // 验证Subject是否为已登录状态
        if (!subject.isAuthenticated()) {
            return R.error("服务器出现错误，登录失败");
        }
        User user = userService.getByUsername(form.getUsername());
        // 从数据库内获取信息，检查用户可用状态
        if (!user.isEnabled()) {
            return R.unauth("该帐号已被停用，如需更多信息，请咨询管理员");
        }
        userLoginLogService.create(user.getId(), CZIPStringUtils.getIPAddress(req));
        return R.success("登录成功");
    }

    @GetMapping("/logout")
    public R logout() {
        Subject subject = SecurityUtils.getSubject();
        if (subject.isAuthenticated() || subject.isRemembered()){
            subject.logout();
            return R.success("登出成功");
        } else {
            return R.unauth("用户未登录");
        }
    }
}
