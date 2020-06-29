package app.pwp.lognet.system.controller;

import app.pwp.lognet.system.form.BindValidateForm;
import app.pwp.lognet.system.form.ChangeBindForm;
import app.pwp.lognet.system.form.ChangePasswordForm;
import app.pwp.lognet.system.model.User;
import app.pwp.lognet.system.model.UserLoginLog;
import app.pwp.lognet.system.model.UserResponse;
import app.pwp.lognet.system.service.RoleService;
import app.pwp.lognet.system.service.UserLoginLogService;
import app.pwp.lognet.system.service.UserService;
import app.pwp.lognet.utils.auth.UserAuthUtils;
import app.pwp.lognet.utils.auth.ValidationUtils;
import app.pwp.lognet.utils.common.R;
import app.pwp.lognet.utils.mail.MailSendUtils;
import app.pwp.lognet.utils.mail.MailTemplate;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.subject.Subject;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;
    @Resource
    private UserLoginLogService userLoginLogService;
    @Resource
    private RoleService roleService;
    @Resource
    private UserAuthUtils userAuthUtils;
    @Resource
    private MailSendUtils mailSendUtils;
    @Resource
    private EhCacheCacheManager cacheManager;

    @GetMapping("/fetchInfo")
    public R fetchInfo() {
        User user;
        try {
            user = userAuthUtils.getUser();
        } catch (RuntimeException e) {
            return R.error(e.getMessage());
        }
        // 返回
        UserResponse res = new UserResponse(user.getId(), user.getUsername(), user.getEmail(), roleService.getById(user.getRoleId()));
        return R.success(res);
    }

    @GetMapping("/fetchLoginLog")
    public R fetchLoginLog() {
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        if (user.getId() == null) {
            return R.error("获取失败");
        }
        List<UserLoginLog> list = userLoginLogService.getRecent(user.getId());
        if (list == null) {
            return R.error("获取失败");
        }
        return R.success(list);
    }

    @PostMapping("/changePassword")
    public R changePassword(@RequestBody @Valid ChangePasswordForm form) {
        if (!form.getNewPassword().equals(form.getNewConfirmPassword())) {
            return R.badRequest("输入的新密码和确认密码不一致");
        }
        User user;
        try {
            user = userAuthUtils.getUser();
        } catch (RuntimeException e) {
            return R.error(e.getMessage());
        }
        String oldPasswordHash = new SimpleHash("SHA-256", form.getOldPassword(), user.getSalt(), 32).toHex();
        if (!user.getPassword().equals(oldPasswordHash)) {
            return R.error("旧密码不正确");
        }
        String salt = new SecureRandomNumberGenerator().nextBytes().toHex();
        String newPasswordHash = new SimpleHash("SHA-256", form.getNewPassword(), salt, 32).toHex();
        user.setSalt(salt);
        user.setPassword(newPasswordHash);
        if (userService.update(user)) {
            // 更改成功，强行登出让用户重新登录
            Subject subject = SecurityUtils.getSubject();
            if (subject.isAuthenticated() || subject.isRemembered()) {
                subject.logout();
            }
            return R.success("更改成功");
        } else {
            return R.error("更改失败");
        }
    }

    @PostMapping("/changeBind")
    public R changeBind(@RequestBody @Valid ChangeBindForm form) throws MessagingException {
        User user;
        try {
            user = userAuthUtils.getUser();
        } catch (RuntimeException e) {
            return R.error(e.getMessage());
        }
        if (user == null) {
            return R.error("无法获取用户信息");
        }
        // 检查邮箱占用
        if (userService.existsByMail(form.getNewMail())) {
            return R.error("该邮箱已被占用");
        }
        String passwordHash = new SimpleHash("SHA-256", form.getPassword(), user.getSalt(), 32).toHex();
        if (!user.getPassword().equals(passwordHash)) {
            return R.error("用户密码不正确");
        }
        CacheManager manager = cacheManager.getCacheManager();
        Cache validationCodeCache = manager.getCache("validationCodeCache");
        Cache validationSendCache = manager.getCache("validationSendCache");
        Cache validationRetryCache = manager.getCache("validationRetryCache");
        // 检查是否已经禁止
        try {
            Element retry_element = validationRetryCache.get("EMAIL_RETRY_BIND_" + form.getNewMail());
            if (retry_element != null) {
                Integer retry = (Integer) retry_element.getObjectValue();
                if (retry >= 10) {
                    return R.error("该邮箱暂时不能进行绑定，请稍后再试");
                }
            }
        } catch (IllegalStateException ise_retry) { /* do nothing */ }
        if (validationSendCache.isKeyInCache("EMAIL_SEND_BIND_" + form.getNewMail())) {
            try {
                validationSendCache.getQuiet("EMAIL_SEND_BIND_" + form.getNewMail());
            } catch (IllegalStateException ise_send) {
                return R.error("不能重复发送，请稍后再试");
            }
        }
        String code = String.valueOf(ValidationUtils.generateCode());
        validationCodeCache.put(new Element("EMAIL_CODE_BIND_" + user.getId().toString() + "_" + form.getNewMail(), code));
        validationSendCache.put(new Element("EMAIL_SEND_BIND_" + form.getNewMail(), null));
        HashMap<String, String> mailParams = new HashMap<>();
        mailParams.put("code", code);
        mailParams.put("username", user.getUsername());
        mailSendUtils.sendHTMLMail(form.getNewMail(), "Lognet - 绑定邮箱", MailTemplate.build("validation_bind", mailParams));
        return R.success("发送成功");
    }

    @PostMapping("/bindValidate")
    public R bindValidate(@RequestBody @Valid BindValidateForm form) {
        // 用户信息获取
        User user;
        try {
            user = userAuthUtils.getUser();
        } catch (RuntimeException e) {
            return R.error(e.getMessage());
        }
        if (user == null) {
            return R.error("无法获取用户信息");
        }
        // 缓存检查
        CacheManager manager = cacheManager.getCacheManager();
        Cache validationCodeCache = manager.getCache("validationCodeCache");
        Cache validationRetryCache = manager.getCache("validationRetryCache");
        // 检查是否已经禁止
        Integer retry = -1;
        try {
            Element retry_element = validationRetryCache.get("EMAIL_RETRY_BIND_" + form.getMail());
            if (retry_element != null) {
                retry = (Integer) retry_element.getObjectValue();
                if (retry >= 10) {
                    return R.error("该邮箱暂时不能进行绑定，请稍后再试");
                }
            }
        } catch (IllegalStateException ise_retry) { /* do nothing */ }
        // 验证码检查
        String cacheCode;
        try {
            Element code_element = validationCodeCache.get("EMAIL_CODE_BIND_" + user.getId().toString() + "_" + form.getMail());
            if (code_element == null) {
                return R.error("找不到对应的验证码，请提交正确的参数");
            }
            cacheCode = (String) code_element.getObjectValue();
        } catch (IllegalStateException ise) {
            return R.error("找不到对应的验证码，请提交正确的参数");
        }
        if (!cacheCode.equals(form.getCode())) {
            if (retry < 0) {
                retry = 1;
            } else {
                retry++;
            }
            validationRetryCache.put(new Element("EMAIL_RETRY_BIND_" + form.getMail(), retry));
            return R.error("提交的验证码有误，请重试");
        }
        // 验证成功，销毁Code
        validationCodeCache.remove("EMAIL_CODE_BIND_" + user.getId().toString() + "_" + form.getMail());
        // 更新用户信息
        user.setEmail(form.getMail());
        if (userService.update(user)) {
            return R.success("更换绑定成功");
        } else {
            return R.error("更换绑定失败");
        }
    }
}