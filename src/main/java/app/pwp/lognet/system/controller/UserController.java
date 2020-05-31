package app.pwp.lognet.system.controller;

import app.pwp.lognet.system.form.ChangePasswordForm;
import app.pwp.lognet.system.model.User;
import app.pwp.lognet.system.model.UserLoginLog;
import app.pwp.lognet.system.model.UserResponse;
import app.pwp.lognet.system.service.RoleService;
import app.pwp.lognet.system.service.UserLoginLogService;
import app.pwp.lognet.system.service.UserService;
import app.pwp.lognet.utils.auth.UserAuthUtils;
import app.pwp.lognet.utils.common.R;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
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

    @GetMapping("/fetchInfo")
    public R fetchInfo() {
        User user;
        try {
            user = userAuthUtils.getUser();
        } catch (RuntimeException e) {
            return R.error(e.getMessage());
        }
        // 返回
        UserResponse res = new UserResponse(user.getId(), user.getUsername(), user.getEmail(), roleService.getById(user.getId()));
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
            return R.success("更改成功");
        } else {
            return R.error("更改失败");
        }
    }
}