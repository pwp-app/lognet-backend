package app.pwp.lognet.system.controller;

import app.pwp.lognet.app.form.GeneralSiteForm;
import app.pwp.lognet.app.model.Site;
import app.pwp.lognet.app.service.SiteService;
import app.pwp.lognet.system.form.GeneralUserForm;
import app.pwp.lognet.system.model.Role;
import app.pwp.lognet.system.model.User;
import app.pwp.lognet.system.service.RoleService;
import app.pwp.lognet.system.service.UserService;
import app.pwp.lognet.utils.auth.UserAuthUtils;
import app.pwp.lognet.utils.common.R;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Resource
    private UserService userService;
    @Resource
    private SiteService siteService;
    @Resource
    private RoleService roleService;
    @Resource
    private UserAuthUtils userAuthUtils;

    @GetMapping("/listUser")
    public R listUser(int page, int pageSize) {
        return R.success(userService.list(page, pageSize));
    }

    @PostMapping("/banUser")
    public R banUser(@RequestBody @Valid GeneralUserForm form) {
        Long uid = userAuthUtils.getUid();
        if (form.getUid().equals(uid)) {
            return R.error("无法操作自己");
        }
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

    @PostMapping("/unbanUser")
    public R unbanUser(@RequestBody @Valid GeneralUserForm form) {
        Long uid = userAuthUtils.getUid();
        if (form.getUid().equals(uid)) {
            return R.error("无法操作自己");
        }
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

    @PostMapping("/setUser")
    public R setUser(@RequestBody @Valid GeneralUserForm form) {
        Long uid = userAuthUtils.getUid();
        if (form.getUid().equals(uid)) {
            return R.error("无法操作自己");
        }
        User user = userService.getById(form.getUid());
        if (user == null) {
            return R.error("无法找到该用户");
        }
        Role role = roleService.getById(user.getRoleId());
        if ("user".equals(role.getName())) {
            return R.error("用户角色不需要变更");
        }
        Role user_role = roleService.getByName("user");
        user.setRoleId(user_role.getId());
        if (userService.update(user)) {
            return R.success("操作成功");
        } else {
            return R.error("操作失败");
        }
    }

    @PostMapping("/setAdmin")
    public R setAdmin(@RequestBody @Valid GeneralUserForm form) {
        Long uid = userAuthUtils.getUid();
        if (form.getUid().equals(uid)) {
            return R.error("无法操作自己");
        }
        User user = userService.getById(form.getUid());
        if (user == null) {
            return R.error("无法找到该用户");
        }
        Role role = roleService.getById(user.getRoleId());
        if ("admin".equals(role.getName())) {
            return R.error("用户角色不需要变更");
        }
        Role admin_role = roleService.getByName("admin");
        user.setRoleId(admin_role.getId());
        if (userService.update(user)) {
            return R.success("操作成功");
        } else {
            return R.error("操作失败");
        }
    }

    @GetMapping("/listSite")
    public R listSite(int page, int pageSize) {
        return R.success(siteService.list(page, pageSize));
    }

    @PostMapping("/banSite")
    public R banSite(@RequestBody @Valid GeneralSiteForm form) {
        Site site = siteService.getById(form.getId());
        if (site == null) {
            return R.error("无法找到该站点");
        }
        if (siteService.ban(site)) {
            return R.success("操作成功");
        } else {
            return R.error("操作失败");
        }
    }

    @PostMapping("/unbanSite")
    public R unbanSite(@RequestBody @Valid GeneralSiteForm form) {
        Site site = siteService.getById(form.getId());
        if (site == null) {
            return R.error("无法找到该站点");
        }
        if (siteService.unban(site)) {
            return R.success("操作成功");
        } else {
            return R.error("操作失败");
        }
    }
}
