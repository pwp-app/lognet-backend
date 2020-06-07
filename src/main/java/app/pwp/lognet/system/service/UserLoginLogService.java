package app.pwp.lognet.system.service;

import app.pwp.lognet.base.service.BaseService;
import app.pwp.lognet.system.model.UserLoginLog;
import app.pwp.lognet.utils.geo.Alimap;
import app.pwp.lognet.utils.geo.AlimapLocation;
import app.pwp.lognet.utils.geo.CZIP;
import app.pwp.lognet.utils.geo.CZIPStringUtils;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class UserLoginLogService extends BaseService<UserLoginLog> {
    @Resource
    private Alimap alimap;
    @Resource
    private CZIP czip;
    // 异步执行任务，提升速度
    @CacheEvict(value = "queryCache", key = "'user_loginlog_' + #uid")
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
    // 获取近期的登录日志
    @Cacheable(value = "queryCache", key = "'user_loginlog_' + #uid", unless = "#result == null")
    public List<UserLoginLog> getRecent(long uid) {
        Session session = this.baseDao.getHibernateSession();
        Query<UserLoginLog> query = session.createQuery("FROM UserLoginLog WHERE uid = :uid ORDER BY createTime desc");
        query.setParameter("uid", uid);
        query.setMaxResults(10);
        return query.list();
    }
}
