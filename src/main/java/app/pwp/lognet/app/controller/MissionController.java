package app.pwp.lognet.app.controller;

import app.pwp.lognet.app.form.GeneralDeleteForm;
import app.pwp.lognet.app.form.GeneralMissionForm;
import app.pwp.lognet.app.form.MissionCreateForm;
import app.pwp.lognet.app.form.MissionUpdateForm;
import app.pwp.lognet.app.model.Mission;
import app.pwp.lognet.app.service.MissionLogService;
import app.pwp.lognet.app.service.MissionService;
import app.pwp.lognet.app.service.SiteService;
import app.pwp.lognet.utils.auth.UserAuthUtils;
import app.pwp.lognet.utils.common.R;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Date;
import java.util.HashMap;

@RestController
@RequestMapping("/mission")
public class MissionController {
    @Resource
    private SiteService siteService;
    @Resource
    private MissionService missionService;
    @Resource
    private MissionLogService missionLogService;
    @Resource
    private UserAuthUtils userAuthUtils;

    private R checkAuth(String id) {
        Long uid = userAuthUtils.getUid();
        if (uid == null) {
            return R.error("无法获取用户信息");
        }
        Long missionUid = missionService.getUserId(id);
        if (missionUid == null) {
            return R.error("无法获取任务信息");
        }
        if (!uid.equals(missionUid)) {
            return R.unauth("无权访问");
        }
        return R.success();
    }

    private R checkAuthBySite(String siteId) {
        Long uid = userAuthUtils.getUid();
        if (uid == null) {
            return R.error("无法获取用户信息");
        }
        Long siteUid = siteService.getUserId(siteId);
        if (siteUid == null) {
            return R.error("无法获取站点信息");
        }
        if (!uid.equals(siteUid)) {
            return R.unauth("无权访问");
        }
        return R.success();
    }

    @GetMapping("/list")
    public R list(String siteId, int page, int pageSize) {
        // 参数校验
        if (siteId == null || siteId.length() < 1 || page < 0 || pageSize < 0 || pageSize > 20) {
            return R.badRequest("请提交正确的参数");
        }
        // 权限检查
        R check = checkAuthBySite(siteId);
        if (check.getCode() != 200) {
            return check;
        }
        return R.success(missionService.listBySite(siteId, page, pageSize));
    }

    @GetMapping("/fetchInfo")
    public R fetchInfo(String id) {
        // 数据校验
        if (id == null || id.length() < 1) {
            return R.badRequest("请提交正确的参数");
        }
        // 权限检查
        Long uid = userAuthUtils.getUid();
        if (uid == null) {
            return R.error("无法获取用户信息");
        }
        if (!missionService.getUserId(id).equals(uid)) {
            return R.unauth("无权访问");
        }
        // 获取
        Mission mission = missionService.getById(id);
        if (mission == null) {
            return R.error("无法获取对应的任务信息");
        }
        return R.success(mission);
    }

    @GetMapping("/fetchStats")
    public R fetchStats(String id) {
        // 数据校验
        if (id == null || id.length() < 1) {
            return R.badRequest("请提交正确的参数");
        }
        // 权限检查
        Long uid = userAuthUtils.getUid();
        if (uid == null) {
            return R.error("无法获取用户信息");
        }
        if (!missionService.getUserId(id).equals(uid)) {
            return R.unauth("无权访问");
        }
        // 获取统计数据
        HashMap<String, Object> res = new HashMap<>();
        res.put("total", missionLogService.countTotal(id));
        res.put("today", missionLogService.countToday(id));
        res.put("totalError", missionLogService.countError(id));
        res.put("todayError", missionLogService.countTodayError(id));
        return R.success(res);
    }

    @PostMapping("/create")
    public R create(@RequestBody @Valid MissionCreateForm form) {
        // 权限检查
        R check = checkAuthBySite(form.getSiteId());
        if (check.getCode() != 200) {
            return check;
        }
        // 构造任务对象
        Mission mission = new Mission();
        mission.setSiteId(form.getSiteId());
        mission.setName(form.getName());
        mission.setDescription(form.getDesc());
        mission.setStartTime(form.getStartTime());
        mission.setEndTime(form.getEndTime());
        // 创建任务
        if (missionService.create(mission)) {
            return R.success("创建成功");
        } else {
            return R.error("服务器错误，创建失败");
        }
    }

    @PostMapping("/update")
    public R update(@RequestBody @Valid MissionUpdateForm form) {
        // 校验
        if (form.getEndTime() != null) {
            if (form.getEndTime().getTime() <= new Date().getTime()) {
                return R.badRequest("请提交正确的日期");
            }
        }
        if (form.getStartTime() != null && form.getEndTime() != null) {
            if (form.getEndTime().getTime() <= form.getStartTime().getTime()) {
                return R.badRequest("请提交正确的日期");
            }
        }
        // 获取任务对象
        Mission mission = missionService.getById(form.getId());
        if (mission == null) {
            return R.error("无法获取任务信息");
        }
        // 权限检查
        R check = checkAuthBySite(mission.getSiteId());
        if (check.getCode() != 200) {
            return check;
        }
        // 进行更新
        mission.setName(form.getName());
        mission.setDescription(form.getDesc());
        mission.setStartTime(form.getStartTime());
        mission.setEndTime(form.getEndTime());
        if (missionService.update(mission)) {
            return R.success("更新成功");
        } else {
            return R.error("服务器错误，更新失败");
        }
    }

    @PostMapping("/enable")
    public R enable(@RequestBody @Valid GeneralMissionForm form) {
        R check = checkAuth(form.getId());
        if (check.getCode() != 200) {
            return check;
        }
        Mission mission = missionService.getById(form.getId());
        if (mission == null) {
            return R.error("指定的任务不存在");
        }
        if (mission.isEnabled()) {
            return R.error("任务已经是启用状态");
        }
        mission.setEnabled(true);
        if (missionService.update(mission)) {
            return R.success("任务启用成功");
        } else {
            return R.error("服务器错误，任务启用失败");
        }
    }

    @PostMapping("/disable")
    public R disable(@RequestBody @Valid GeneralMissionForm form) {
        R check = checkAuth(form.getId());
        if (check.getCode() != 200) {
            return check;
        }
        Mission mission = missionService.getById(form.getId());
        if (mission == null) {
            return R.error("指定的任务不存在");
        }
        if (!mission.isEnabled()) {
            return R.error("任务已经是禁用状态");
        }
        mission.setEnabled(false);
        if (missionService.update(mission)) {
            return R.success("任务禁用成功");
        } else {
            return R.error("服务器错误，任务启用失败");
        }
    }

    @PostMapping("/delete")
    public R delete(@RequestBody @Valid GeneralDeleteForm form) {
        // 检查权限
        R check = checkAuth(form.getId());
        if (check.getCode() != 200) {
            return check;
        }
        // 检查存在
        if (!missionService.exists(form.getId())) {
            return R.error("指定的任务不存在");
        }
        if (missionService.deleteById(form.getId())) {
            return R.success("删除成功");
        } else {
            return R.error("服务器错误，删除失败");
        }
    }
}
