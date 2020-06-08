package app.pwp.lognet.app.controller;

import app.pwp.lognet.app.service.MissionLogService;
import app.pwp.lognet.app.service.MissionService;
import app.pwp.lognet.utils.auth.UserAuthUtils;
import app.pwp.lognet.utils.common.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/mission_log")
public class MissionLogController {
    @Resource
    private MissionLogService missionLogService;
    @Resource
    private MissionService missionService;
    @Resource
    private UserAuthUtils userAuthUtils;

    @GetMapping("/list")
    public R list(String missionId, int page, int pageSize) {
        // 参数校验
        if (missionId == null || missionId.length() < 1 || page < 0 || pageSize < 0 || pageSize > 20) {
            return R.badRequest("请提交正确的参数");
        }
        // 权限检查
        Long uid = userAuthUtils.getUid();
        if (uid == null) {
            return R.error("无法获取用户信息");
        }
        Long missionUserId = missionService.getUserId(missionId);
        if (missionUserId == null) {
            return R.error("无法获取任务信息");
        }
        if (!missionUserId.equals(uid)) {
            return R.unauth("无法访问");
        }
        // 返回结果
        return R.success(missionLogService.list(missionId, page, pageSize));
    }

    @PostMapping("/delete")
    public R delete(String id, String missionId) {
        // 参数校验
        if (id == null || id.length() < 1 || missionId == null || missionId.length() < 1) {
            return R.badRequest("请提交正确的参数");
        }
        // 获取日志的missionId
        String _missionId = missionLogService.getMissionId(id);
        if (_missionId == null) {
            return R.error("无法获取任务信息");
        }
        if (!missionId.equals(_missionId)) {
            return R.error("提交的任务ID不正确");
        }
        // 权限校验
        Long uid = userAuthUtils.getUid();
        if (uid == null) {
            return R.error("无法获取用户信息");
        }
        Long missionUserId = missionService.getUserId(missionId);
        if (missionUserId == null) {
            return R.error("无法获取任务信息");
        }
        if (!missionUserId.equals(uid)) {
            return R.unauth("无法访问");
        }
        if (missionLogService.deleteById(id)) {
            return R.success("删除成功");
        } else {
            return R.error("服务器错误，删除失败");
        }
    }
}
