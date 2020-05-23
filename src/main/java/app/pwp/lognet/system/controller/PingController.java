package app.pwp.lognet.system.controller;

import app.pwp.lognet.utils.common.R;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping("/ping")
public class PingController {
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public R ping() {
        return R.ok();
    }
}
