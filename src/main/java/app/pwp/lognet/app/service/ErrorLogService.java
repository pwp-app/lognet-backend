package app.pwp.lognet.app.service;

import app.pwp.lognet.app.model.ErrorLog;
import app.pwp.lognet.base.service.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;

@Service
@Transactional(rollbackFor = Exception.class)
public class ErrorLogService extends BaseService<ErrorLog> {
    public boolean create(ErrorLog log) {
        return this.baseDao.add(log);
    }

    public HashMap<String, Object> list(String siteId, int page, int pageSize) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("siteId", siteId);
        return this.baseDao.showPageWithTotal("FROM ErrorLog WHERE siteId = :siteId ORDER BY createTime DESC", params, page, pageSize);
    }

    public Long getUserId(String id) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("id", id);
        return this.baseDao.getSingleLong("SELECT Site.userId FROM Site INNER JOIN ErrorLog WITH ErrorLog.siteId = Site.id WHERE ErrorLog.id = :id", params);
    }

    public boolean deleteById(String id) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("id", id);
        return this.baseDao.removeBySession("DELETE FROM ErrorLog WHERE id = :id", params);
    }
}
