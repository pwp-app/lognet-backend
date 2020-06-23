package app.pwp.lognet.system.controller;

import app.pwp.lognet.app.form.GeneralSiteForm;
import app.pwp.lognet.app.service.SiteService;
import app.pwp.lognet.system.form.GeneralUserForm;
import app.pwp.lognet.system.model.User;
import app.pwp.lognet.system.service.UserService;
import app.pwp.lognet.utils.common.R;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Resource
    private UserService userService;
    @Resource
    private SiteService siteService;

    public R listUser(int page, int pageSize) {
        return R.success(userService.list(page, pageSize));
    }

    public R banUser(@RequestBody @Valid GeneralUserForm form) {
        User user = userService.getById(form.getUid());
        if (user == null) {
            return R.error("无法找到该用户");
        }
        user.setEnabled(false);
        if (userService.update(user)) {
            return R.success("操作成功");
        } else {
            return R.error("操作失败");
        }
    }

    public R unbanUser(@RequestBody @Valid GeneralUserForm form) {
        User user = userService.getById(form.getUid());
        if (user == null) {
            return R.error("无法找到该用户");
        }
        user.setEnabled(true);
        if (userService.update(user)) {
            return R.success("操作成功");
        } else {
            return R.error("操作失败");
        }
    }

    public R listSite(int page, int pageSize) {

    }

    public R banSite(@RequestBody @Valid GeneralSiteForm form) {

    }
}
