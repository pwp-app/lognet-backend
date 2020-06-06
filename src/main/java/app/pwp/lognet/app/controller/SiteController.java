package app.pwp.lognet.app.controller;

import app.pwp.lognet.app.service.SiteService;
import app.pwp.lognet.utils.auth.UserAuthUtils;
import app.pwp.lognet.utils.common.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;

@RestController
@RequestMapping(value = "/site")
public class SiteController {
    @Resource
    private SiteService siteService;
    @Resource
    private UserAuthUtils userAuthUtils;

    @GetMapping("/list")
    public R list(int page, int pageSize) {
        if (page < 0 || pageSize < 0 || pageSize > 20) {
            return R.badRequest("请提交正确的参数");
        }
        return R.success(siteService.list(userAuthUtils.getUid(), page, pageSize));
    }

    @PostMapping("/add")
    public R add() {

    }

    @PostMapping("/update")
    public R update() {

    }

    @PostMapping("/delete")
    public R delete() {

    }
}