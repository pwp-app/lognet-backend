package app.pwp.lognet.system.service;

import app.pwp.lognet.base.service.BaseService;
import app.pwp.lognet.system.model.UserLoginLog;
import app.pwp.lognet.utils.geo.CZIPStringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class UserLoginLogService extends BaseService<UserLoginLog> {
    // 异步执行任务，提升速度
    @Async
    public boolean create(long uid, String ip) {
        UserLoginLog log = new UserLoginLog(uid, ip);
        // 根据IP获取地理位置信息
        if (CZIPStringUtils.isIPv4(ip)) {
            // 只支持IPv4

        }
        return this.baseDao.add(log);
    }
}
