package app.pwp.lognet.utils.auth;

import app.pwp.lognet.system.model.User;
import app.pwp.lognet.system.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class UserAuthUtils {
    @Resource
    private UserService userService;

    public User getUser() {
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        if (user == null) {
            throw new RuntimeException("无法获取用户信息");
        }
        if (user.getId() != null) {
            user = userService.getById(user.getId());
        } else {
            if (user.getUsername() == null) {
                throw new RuntimeException("无法获取用户信息");
            }
            user = userService.getByUsername(user.getUsername());
        }
        if (user == null) {
            throw new RuntimeException("无法获取用户信息");
        }
        return user;
    }

    public Long getUid() {
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        if (user == null || user.getId() == null) {
            return null;
        }
        return user.getId();
    }
}
