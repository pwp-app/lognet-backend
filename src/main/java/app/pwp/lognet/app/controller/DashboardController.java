package app.pwp.lognet.app.controller;

import app.pwp.lognet.app.ro.HotPath;
import app.pwp.lognet.app.service.ErrorLogService;
import app.pwp.lognet.app.service.MissionLogService;
import app.pwp.lognet.app.service.MissionService;
import app.pwp.lognet.app.service.SiteService;
import app.pwp.lognet.utils.auth.UserAuthUtils;
import app.pwp.lognet.utils.common.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {
    @Resource
    private SiteService siteService;
    @Resource
    private MissionService missionService;
    @Resource
    private ErrorLogService logService;
    @Resource
    private MissionLogService missionLogService;
    @Resource
    private UserAuthUtils userAuthUtils;

    @GetMapping("/fetchStats")
    public R fetchStats() {
        Long uid = userAuthUtils.getUid();
        if (uid == null) {
            return R.error("无法获取用户信息");
        }
        HashMap<String, Object> res = new HashMap<>();
        res.put("sites", siteService.countByUser(uid));
        res.put("runningMission", missionService.countRunningByUser(uid));
        res.put("todayGeneralLog", logService.countTodayByUser(uid));
        res.put("todayMissionLog", missionLogService.countTodayByUser(uid));
        return R.success(res);
    }

    @GetMapping("/listHotPath")
    public R listHotPath() {
        Long uid = userAuthUtils.getUid();
        if (uid == null) {
            return R.error("无法获取用户信息");
        }
        List<HotPath> res = logService.listHotPath(uid);
        return R.success(res);
    }

    @GetMapping("/listRunningMission")
    public R listRunningMission(int page, int pageSize) {
        Long uid = userAuthUtils.getUid();
        if (uid == null) {
            return R.error("无法获取用户信息");
        }
        return R.success(missionService.listRunningByUser(uid, page, pageSize));
    }
}
