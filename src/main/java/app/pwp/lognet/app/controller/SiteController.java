package app.pwp.lognet.app.controller;

import app.pwp.lognet.app.form.GeneralDeleteForm;
import app.pwp.lognet.app.form.SiteCreateForm;
import app.pwp.lognet.app.form.SiteUpdateForm;
import app.pwp.lognet.app.model.Site;
import app.pwp.lognet.app.service.ErrorLogService;
import app.pwp.lognet.app.service.MissionLogService;
import app.pwp.lognet.app.service.MissionService;
import app.pwp.lognet.app.service.SiteService;
import app.pwp.lognet.utils.auth.UserAuthUtils;
import app.pwp.lognet.utils.common.R;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.HashMap;

@RestController
@RequestMapping(value = "/site")
public class SiteController {
    @Resource
    private SiteService siteService;
    @Resource
    private MissionService missionService;
    @Resource
    private ErrorLogService errorLogService;
    @Resource
    private MissionLogService missionLogService;
    @Resource
    private UserAuthUtils userAuthUtils;

    @GetMapping("/list")
    public R list(int page, int pageSize) {
        if (page < 0 || pageSize < 0 || pageSize > 20) {
            return R.badRequest("请提交正确的参数");
        }
        Long uid = userAuthUtils.getUid();
        if (uid == null) {
            return R.error("用户信息获取失败");
        }
        return R.success(siteService.listByUser(uid, page, pageSize));
    }

    @GetMapping("/fetchOverall")
    public R fetchOverall() {
        Long uid = userAuthUtils.getUid();
        if (uid == null) {
            return R.error("无法获取用户信息");
        }
        HashMap<String, Object> res = new HashMap<>();
        res.put("total", siteService.countByUser(uid));
        res.put("recentGeneralLog", errorLogService.countRecentByUser(uid));
        res.put("recentMissionLog", missionLogService.countRecentByUser(uid));
        return R.success(res);
    }

    @GetMapping("/fetchInfo")
    public R fetchInfo(String id) {
        // 数据校验
        if (id == null || id.length() < 1) {
            return R.badRequest("请提交正确的参数");
        }
        Site site = siteService.getById(id);
        if (site == null) {
            return R.error("无法获取对应的站点信息");
        }
        Long uid = userAuthUtils.getUid();
        if (uid == null) {
            return R.error("无法获取用户信息");
        }
        if (!site.getUid().equals(uid)) {
            return R.unauth("无权访问");
        }
        return R.success(site);
    }

    @GetMapping("/fetchStats")
    public R fetchStats(String id) {
        // 数据校验
        if (id == null || id.length() < 1) {
            return R.badRequest("请提交正确的参数");
        }
        // 权限检查
        Site site = siteService.getById(id);
        if (site == null) {
            return R.error("无法获取对应的站点信息");
        }
        Long uid = userAuthUtils.getUid();
        if (uid == null) {
            return R.error("无法获取用户信息");
        }
        if (!site.getUid().equals(uid)) {
            return R.unauth("无权访问");
        }
        // 获取数据
        HashMap<String, Object> res = new HashMap<>();
        res.put("total", missionService.countBySite(id));
        res.put("running", missionService.countRunningBySite(id));
        res.put("totalLog", errorLogService.countBySite(id));
        res.put("recentLog", errorLogService.countRecentBySite(id));
        return R.success(res);
    }

    @PostMapping("/add")
    public R add(@RequestBody @Valid SiteCreateForm form) {
        Site site = new Site();
        Long uid = userAuthUtils.getUid();
        if (uid == null) {
            return R.error("无法获取当前用户的信息");
        }
        // 检查重复（同一User下不能有两个一样的域名）
        if (siteService.domainExists(uid, form.getDomain())) {
            return R.error("该域名已经存在，请勿重复添加");
        }
        site.setUid(uid);
        site.setDomain(form.getDomain());
        site.setDescription(form.getDesc());
        if (siteService.add(site)) {
            return R.success("添加成功");
        } else {
            return R.error("服务器错误，添加失败");
        }
    }

    @PostMapping("/update")
    public R update(@RequestBody @Valid SiteUpdateForm form) {
        // 获取对应的site
        Site site = siteService.getById(form.getId());
        if (site == null) {
            return R.error("无法获取站点的信息");
        }
        // 权限控制
        Long uid = userAuthUtils.getUid();
        if (uid == null) {
            return R.error("无法获取当前用户的信息");
        }
        if (!site.getUid().equals(uid)) {
            return R.unauth("无权访问");
        }
        // 更新信息
        site.setDescription(form.getDesc());
        if (siteService.update(site)) {
            return R.success("信息更新成功");
        } else {
            return R.error("服务器错误，信息更新失败");
        }
    }

    @PostMapping("/delete")
    public R delete(@RequestBody @Valid GeneralDeleteForm form) {
        Site site = siteService.getById(form.getId());
        if (site == null) {
            return R.error("找不到对应的站点");
        }
        // 权限控制
        Long uid = userAuthUtils.getUid();
        if (uid == null) {
            return R.error("无法获取当前用户的信息");
        }
        if (!site.getUid().equals(uid)) {
            return R.unauth("无权访问");
        }
        try {
            if (siteService.delete(site)) {
                return R.success("删除成功");
            } else {
                return R.error("服务器错误，删除失败");
            }
        } catch (RuntimeException e) {
            return R.error(e.getMessage());
        }
    }
}