package app.pwp.lognet.system.service;

import app.pwp.lognet.base.service.BaseService;
import app.pwp.lognet.system.model.UserLoginLog;
import app.pwp.lognet.utils.geo.Alimap;
import app.pwp.lognet.utils.geo.AlimapLocation;
import app.pwp.lognet.utils.geo.CZIP;
import app.pwp.lognet.utils.geo.CZIPStringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.concurrent.ListenableFuture;

import javax.annotation.Resource;

@Service
@Transactional(rollbackFor = Exception.class)
public class UserLoginLogService extends BaseService<UserLoginLog> {
    @Resource
    private Alimap alimap;
    @Resource
    private CZIP czip;
    // 异步执行任务，提升速度
    public boolean create(long uid, String ip) throws Exception {
        UserLoginLog log = new UserLoginLog(uid, ip);
        // 根据IP获取地理位置信息
        if (CZIPStringUtils.isIPv4(ip)) {
            // 只支持IPv4
            AlimapLocation location = alimap.getGeo(ip).get();
            if (location.getProvince() != null) {
                log.setGeo(location.toString());
            } else {
                // 高德LBS无法获取到，可能是国外IP，使用纯真IP
                log.setGeo(czip.getIPLocation(ip).toString());
            }
        }
        return this.baseDao.add(log);
    }
}
