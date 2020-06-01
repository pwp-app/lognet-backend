package app.pwp.lognet.system.controller;

import app.pwp.lognet.system.form.MailValidateForm;
import app.pwp.lognet.system.form.UserLoginForm;
import app.pwp.lognet.system.form.UserRegisterForm;
import app.pwp.lognet.system.model.Role;
import app.pwp.lognet.system.model.User;
import app.pwp.lognet.system.model.UserResponse;
import app.pwp.lognet.system.service.RoleService;
import app.pwp.lognet.system.service.UserLoginLogService;
import app.pwp.lognet.utils.auth.ReCaptcha;
import app.pwp.lognet.system.service.UserService;
import app.pwp.lognet.utils.auth.ValidationUtils;
import app.pwp.lognet.utils.common.R;
import app.pwp.lognet.utils.geo.CZIPStringUtils;
import app.pwp.lognet.utils.mail.MailSendUtils;
import app.pwp.lognet.utils.mail.MailTemplate;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.subject.Subject;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

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
    @Resource
    private MailSendUtils mailSendUtils;

    @Resource
    private EhCacheCacheManager cacheManager;

    @PostMapping("/login")
    public R login(@RequestBody @Valid UserLoginForm form, HttpServletRequest req) throws Exception {
        Subject subject = SecurityUtils.getSubject();
        // 用户重复登录了，返回成功放行
        if (subject.isAuthenticated()){
            User user = (User) subject.getPrincipal();
            if (user.getId() != null) {
                user = userService.getById(user.getId());
            } else {
                user = userService.getByUsername(user.getUsername());
            }
            if (user != null) {
                UserResponse response = new UserResponse(user.getId(), user.getUsername(), user.getEmail(), roleService.getById(user.getRoleId()));
                return R.success("用户已登录", response);
            }
        }
        UsernamePasswordToken token = new UsernamePasswordToken(form.getUsername(), form.getPassword());
        token.setRememberMe(form.isRememberMe());
        try {
            subject.login(token);
        } catch (UnknownAccountException | IncorrectCredentialsException e) {
            return R.unauth("用户名或密码不正确");
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
        // 清理登录记录的缓存
        CacheManager manager = cacheManager.getCacheManager();
        Cache queryCache = manager.getCache("queryCache");
        queryCache.remove("user_loginlog_" + user.getId());
        return R.success("登录成功", response);
    }

    @PostMapping("/register")
    public R register(@RequestBody @Valid UserRegisterForm form) throws ExecutionException, InterruptedException {
        // 验证密码
        if (!form.getPassword().equals(form.getConfirmPassword())) {
            return R.badRequest("两次输入的密码不一致，请检查后重试");
        }
        // reCAPTCHA
        if (!reCaptcha.verify(form.getToken()).get()) {
            return R.badRequest("系统检测到您可能为机器人，注册失败");
        }
        // 检查占用
        if (userService.existsByUsername(form.getUsername())) {
            return R.error("很抱歉，该用户名已被占用");
        }
        if (userService.existsByMail(form.getEmail())) {
            return R.error("很抱歉，您填写的电子邮箱已被占用");
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
    public R sendValidation(String email) throws MessagingException {
        // 正则判断是否有效
        if (!Pattern.matches("^[_a-z0-9-]+(\\.[_a-z0-9-]+)*@[a-z0-9-]+(\\.[a-z0-9-]+)*(\\.[a-z]{2,})", email)) {
            return R.badRequest("请提交合法的参数");
        }
        CacheManager manager = cacheManager.getCacheManager();
        Cache validationCodeCache = manager.getCache("validationCodeCache");
        Cache validationSendCache = manager.getCache("validationSendCache");
        Cache validationRetryCache = manager.getCache("validationRetryCache");
        // 检查是否已经禁止
        try {
            Element retry_element = validationRetryCache.get("EMAIL_RETRY_" + email);
            if (retry_element != null) {
                Integer retry = (Integer) retry_element.getObjectValue();
                if (retry >= 10) {
                    return R.error("该帐户暂时不能进行验证，请稍后再试");
                }
            }
        } catch (IllegalStateException ise_retry) { /* do nothing */ }
        // 检查是否发送冷却
        if (validationSendCache.isKeyInCache("EMAIL_SEND_" + email)) {
            try {
                validationSendCache.getQuiet("EMAIL_SEND_" + email);
            } catch (IllegalStateException ise_send) {
                return R.error("不能重复发送，请稍后再试");
            }
        }
        // 检查是否为用户
        if (!userService.existsByMail(email)) {
            return R.error("提交的电子邮箱地址有误，请重试");
        }
        // 生成一个六位数
        String code = String.valueOf(ValidationUtils.generateCode());
        validationCodeCache.put(new Element("EMAIL_CODE_" + email, code));
        validationSendCache.put(new Element("EMAIL_SEND_" + email, null));
        HashMap<String, String> mailParams = new HashMap<>();
        mailParams.put("code", code);
        mailSendUtils.sendHTMLMail(email, "Lognet - 邮箱验证", MailTemplate.build("validation", mailParams));
        return R.success("发送成功");
    }

    @PostMapping("/validate")
    public R validate(@RequestBody @Valid MailValidateForm form) {
        // 初始化
        Integer retry = -1;
        CacheManager manager = cacheManager.getCacheManager();
        Cache validationCodeCache = manager.getCache("validationCodeCache");
        Cache validationRetryCache = manager.getCache("validationRetryCache");
        // 检查是否已经被禁止
        try {
            Element retry_element = validationRetryCache.get("EMAIL_RETRY_" + form.getEmail());
            if (retry_element != null) {
                retry = (Integer) retry_element.getObjectValue();
                if (retry >= 10) {
                    return R.error("该帐户暂时不能进行验证，请稍后再试");
                }
            }
        } catch (IllegalStateException ise_retry) { /* do nothing */ }
        String code;
        try {
            Element code_element = validationCodeCache.get("EMAIL_CODE_" + form.getEmail());
            if (code_element == null) {
                return R.error("找不到对应的验证码，请提交正确的参数");
            }
            code = (String) code_element.getObjectValue();
        } catch (IllegalStateException ise) {
            return R.error("找不到对应的验证码，请提交正确的参数");
        }
        // 检查用户是否存在
        User user = userService.getByMail(form.getEmail());
        if (user == null) {
            return R.error("提交的电子邮箱地址有误，请重试");
        }
        if (!code.equals(form.getCode())) {
            if (retry < 0) {
                retry = 1;
            } else {
                retry++;
            }
            validationRetryCache.put(new Element("EMAIL_RETRY_" + form.getEmail(), retry));
            return R.error("验证失败");
        }
        // 验证成功，销毁Code
        validationCodeCache.remove("EMAIL_CODE_" + form.getEmail());
        // 更新用户信息
        user.setRoleId(roleService.getByName("user").getId());
        if (userService.update(user)) {
            return R.success("验证成功");
        } else {
            return R.error("更新用户信息失败，请重试");
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
