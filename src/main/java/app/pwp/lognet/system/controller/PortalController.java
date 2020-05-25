package app.pwp.lognet.system.controller;

import app.pwp.lognet.LognetApplication;
import app.pwp.lognet.system.form.UserLogin;
import app.pwp.lognet.system.form.UserRegister;
import app.pwp.lognet.system.model.Role;
import app.pwp.lognet.system.model.User;
import app.pwp.lognet.system.model.UserResponse;
import app.pwp.lognet.system.service.RoleService;
import app.pwp.lognet.system.service.UserLoginLogService;
import app.pwp.lognet.utils.auth.ReCaptcha;
import app.pwp.lognet.system.service.UserService;
import app.pwp.lognet.utils.common.R;
import app.pwp.lognet.utils.geo.CZIPStringUtils;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.*;

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
    private RoleService roleService;
    @Resource
    private ReCaptcha reCaptcha;

    private CacheManager cacheManager = CacheManager.create(LognetApplication.class.getClassLoader().getResourceAsStream("ehcache.xml"));
    private Cache cache = cacheManager.getCache("validationCodeCache");

    @PostMapping("/login")
    public R login(@RequestBody @Valid UserLogin form, HttpServletRequest req) throws Exception {
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
        // reCAPTCHA
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
        // 构造返回的数据
        UserResponse response = new UserResponse(user.getId(), user.getUsername(), user.getEmail(), roleService.getById(user.getRoleId()));
        return R.success("登录成功", response);
    }

    @PostMapping("/register")
    public R register(@RequestBody @Valid UserRegister form) throws ExecutionException, InterruptedException {
        // 验证密码
        if (!form.getPassword().equals(form.getConfirmPassword())) {
            return R.badRequest("两次输入的密码不一致，请检查后重试");
        }
        // reCAPTCHA
        if (!reCaptcha.verify(form.getToken()).get()) {
            return R.badRequest("系统检测到您可能为机器人，注册失败");
        }
        // 构造User
        User user = new User();
        user.setUsername(form.getUsername());
        String salt = new SecureRandomNumberGenerator().nextBytes().toHex();
        user.setPassword(new SimpleHash("SHA-256", form.getPassword(), salt, 32).toHex());
        user.setSalt(salt);
        Role role = roleService.getByName("guest");
        user.setRoleId(role.getId());
        user.setEmail(form.getEmail());
        user.setEnabled(true);
        if (!userService.create(user)) {
            return R.error("服务器错误，注册失败");
        }
        return R.success("注册成功");
    }

    @GetMapping("/sendValidation")
    public R sendValidation(String email) {
        if (cache.isKeyInCache("EMAIL_VALID_" + email)) {
            return R.error("不能重复发送，请稍后再试");
        }

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
