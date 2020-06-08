package app.pwp.lognet.app.controller;

import app.pwp.lognet.app.service.ErrorLogService;
import app.pwp.lognet.app.service.SiteService;
import app.pwp.lognet.utils.auth.UserAuthUtils;
import app.pwp.lognet.utils.common.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/general_log")
public class ErrorLogController {
    @Resource
    private SiteService siteService;
    @Resource
    private ErrorLogService errorLogService;
    @Resource
    private UserAuthUtils userAuthUtils;

    @GetMapping("/list")
    public R list(String siteId, int page, int pageSize) {
        // 参数校验
        if (siteId == null || siteId.length() < 1 || page < 0 || pageSize < 0 || pageSize > 20) {
            return R.badRequest("请提交正确的参数");
        }
        // 权限检查
        Long uid = userAuthUtils.getUid();
        if (uid == null) {
            return R.error("无法获取用户信息");
        }
        Long siteUserId = siteService.getUserId(siteId);
        if (siteUserId == null) {
            return R.error("无法获取站点信息");
        }
        if (!siteUserId.equals(uid)) {
            return R.unauth("无权访问");
        }
        return R.success(errorLogService.list(siteId, page, pageSize));
    }

    @PostMapping("/delete")
    public R delete(String id) {
        // 参数校验
        if (id == null || id.length() < 1) {
            return R.badRequest("请提交正确的参数");
        }
        // 检查权限
        Long uid = userAuthUtils.getUid();
        if (uid == null) {
            return R.error("无法获取用户信息");
        }
        Long logUserId = errorLogService.getUserId(id);
        if (logUserId == null) {
            return R.error("无法获取日志信息");
        }
        if (!logUserId.equals(uid)) {
            return R.unauth("无权访问");
        }
        if (errorLogService.deleteById(id)) {
            return R.success("删除成功");
        } else {
            return R.error("服务器错误，删除失败");
        }
    }
}
