package app.pwp.lognet.app.service;

import app.pwp.lognet.app.model.ErrorLog;
import app.pwp.lognet.app.ro.HotPath;
import app.pwp.lognet.base.service.BaseService;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class ErrorLogService extends BaseService<ErrorLog> {
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

    public boolean create(ErrorLog log) {
        return this.baseDao.add(log);
    }

    public List<HotPath> listHotPath(Long uid) {
        NativeQuery query = this.baseDao.getHibernateSession().createNativeQuery("SELECT site.domain as domain, log.path as path, count(log.id) as count FROM lognet_errorlog log " +
                "INNER JOIN lognet_sites site ON site.id = log.site_id " +
                "WHERE site.uid = :uid AND DATEDIFF(CURRENT_DATE, site.create_time) > -3 " +
                "GROUP BY site.domain, log.path ORDER BY count DESC", "HotPath");
        query.setParameter("uid", uid);
        query.setMaxResults(10);
        return (List<HotPath>)query.list();
    }

    public Long countBySite(String siteId) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("siteId", siteId);
        return this.baseDao.countBySession("SELECT count(*) FROM ErrorLog WHERE siteId = :siteId", params);
    }

    public Long countTodayByUser(Long uid) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("uid", uid);
        return this.baseDao.countBySession("SELECT count(*) FROM ErrorLog WHERE siteId IN (SELECT id FROM Site WHERE uid = :uid) AND createTime > '"+ dateFormatter.format(new Date()) + " 00:00:00'", params);
    }

    public Long countRecentBySite(String siteId) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("siteId", siteId);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, -7);
        params.put("dateLimit", calendar.getTime());
        return this.baseDao.countBySession("SELECT count(*) FROM ErrorLog WHERE siteId = :siteId AND createTime > :dateLimit", params);
    }

    public HashMap<String, Object> list(String siteId, int page, int pageSize) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("siteId", siteId);
        return this.baseDao.showPageWithTotal("FROM ErrorLog WHERE siteId = :siteId ORDER BY createTime DESC", params, page, pageSize);
    }

    public Long getUserId(String id) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("id", id);
        return this.baseDao.getSingleLong("SELECT s.userId FROM Site s, ErrorLog e WHERE e.siteId = s.id AND e.id = :id", params);
    }

    public boolean deleteById(String id) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("id", id);
        return this.baseDao.removeBySession("DELETE FROM ErrorLog WHERE id = :id", params);
    }
}
