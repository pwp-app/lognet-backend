package app.pwp.lognet.system.controller;

import app.pwp.lognet.utils.common.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ping")
public class PingController {
    @GetMapping(value = "/")
    public R ping() {
        return R.success();
    }
}
