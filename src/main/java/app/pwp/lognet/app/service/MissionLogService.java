package app.pwp.lognet.app.service;

import app.pwp.lognet.app.model.MissionLog;
import app.pwp.lognet.base.service.BaseService;

import java.util.HashMap;

public class MissionLogService extends BaseService<MissionLog> {
    public boolean create(MissionLog log) {
        return this.baseDao.add(log);
    }

    public boolean deleteById(String id) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("id", id);
        return this.baseDao.removeBySession("DELETE FROM MissionLog WHERE id = :id", params);
    }
}
