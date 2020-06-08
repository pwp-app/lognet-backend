package app.pwp.lognet.app.service;

import app.pwp.lognet.app.model.ErrorLog;
import app.pwp.lognet.base.service.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

@Service
@Transactional(rollbackFor = Exception.class)
public class ErrorLogService extends BaseService<ErrorLog> {
    public boolean create(ErrorLog log) {
        return this.baseDao.add(log);
    }

    public Long countBySite(String siteId) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("siteId", siteId);
        return this.baseDao.countBySession("SELECT count(*) FROM ErrorLog WHERE siteId = :siteId", params);
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
