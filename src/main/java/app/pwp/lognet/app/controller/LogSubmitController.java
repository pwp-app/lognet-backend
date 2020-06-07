package app.pwp.lognet.app.controller;

import app.pwp.lognet.app.form.GeneralLogSubmitForm;
import app.pwp.lognet.app.form.MissionLogSubmitForm;
import app.pwp.lognet.app.model.ErrorLog;
import app.pwp.lognet.app.model.Mission;
import app.pwp.lognet.app.model.MissionLog;
import app.pwp.lognet.app.model.Site;
import app.pwp.lognet.app.service.ErrorLogService;
import app.pwp.lognet.app.service.MissionLogService;
import app.pwp.lognet.app.service.MissionService;
import app.pwp.lognet.app.service.SiteService;
import app.pwp.lognet.utils.auth.UserAuthUtils;
import app.pwp.lognet.utils.common.R;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Date;

@RestController
@RequestMapping("/submit")
public class LogSubmitController {
    @Resource
    private SiteService siteService;
    @Resource
    private ErrorLogService errorLogService;
    @Resource
    private MissionService missionService;
    @Resource
    private MissionLogService missionLogService;
    @Resource
    private UserAuthUtils userAuthUtils;

    @PostMapping("/general")
    public R generalSubmit(@RequestBody @Valid GeneralLogSubmitForm form) {
        // 验证appKey
        Site site = siteService.getByKey(form.getAppKey());
        // 验证可用性
        if (!site.isEnabled()) {
            return R.error("站点已被禁用");
        }
        // 构造
        ErrorLog log = new ErrorLog();
        log.setPath(form.getPath());
        log.setContent(form.getContent());
        if (errorLogService.create(log)) {
            return R.success("提交成功");
        } else {
            return R.error("服务器错误，提交失败");
        }
    }

    @PostMapping("/mission")
    public R missionSubmit(@RequestBody @Valid MissionLogSubmitForm form) {
        String siteId = siteService.getIdByKey(form.getAppKey());
        if (siteId == null) {
            return R.badRequest("提交的AppKey不正确");
        }
        // 提取Mission
        Mission mission = missionService.getById(form.getMissionId());
        if (mission == null) {
            return R.badRequest("提交的任务ID不正确");
        }
        if (!mission.getSiteId().equals(siteId)) {
            return R.badRequest("提交的任务ID不正确");
        }
        // 检查可用性
        if (!mission.isEnabled()) {
            return R.error("任务已被禁用");
        }
        // 校验时间
        if (mission.getEndTime() != null) {
            if (new Date().getTime() > mission.getEndTime().getTime()) {
                return R.error("任务已过期");
            }
        }
        if (mission.getStartTime() != null) {
            if (new Date().getTime() < mission.getStartTime().getTime()) {
                return R.error("尚未到任务日志收集的时间");
            }
        }
        // 构造
        MissionLog log = new MissionLog();
        log.setMissionId(form.getMissionId());
        log.setPath(form.getPath());
        log.setContent(form.getContent());
        log.setType(form.getType());
        if (missionLogService.create(log)) {
            return R.success("提交成功");
        } else {
            return R.error("服务器错误，提交失败");
        }
    }
}
