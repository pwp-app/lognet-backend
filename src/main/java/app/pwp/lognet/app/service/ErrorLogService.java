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

    public boolean deleteById(String id) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("id", id);
        return this.baseDao.removeBySession("DELETE FROM ErrorLog WHERE id = :id", params);
    }
}
