package app.pwp.lognet.app.service;

import app.pwp.lognet.app.model.MissionLog;
import app.pwp.lognet.base.service.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

@Service
@Transactional(rollbackFor = Exception.class)
public class MissionLogService extends BaseService<MissionLog> {
    public boolean create(MissionLog log) {
        return this.baseDao.add(log);
    }

    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

    public HashMap<String, Object> list(String missionId, int page, int pageSize) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("missionId", missionId);
        return this.baseDao.showPageWithTotal("FROM MissionLog WHERE missionId = :missionId ORDER BY createTime DESC", params, page, pageSize);
    }

    public Long countTotal(String missionId) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("missionId", missionId);
        return this.baseDao.countBySession("SELECT count(*) FROM MissionLog WHERE missionId = :missionId", params);
    }

    public Long countError(String missionId) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("missionId", missionId);
        return this.baseDao.countBySession("SELECT count(*) FROM MissionLog WHERE missionId = :missionId AND type = 'error'", params);
    }

    public Long countToday(String missionId) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("missionId", missionId);
        return this.baseDao.countBySession("SELECT count(*) FROM MissionLog WHERE missionId = :missionId AND createTime > '"+ dateFormatter.format(new Date()) + " 00:00:00'", params);
    }

    public Long countTodayByUser(Long uid) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("uid", uid);
        return this.baseDao.countBySession("SELECT count(*) FROM MissionLog WHERE missionId in (SELECT id FROM Mission WHERE siteId IN (SELECT id FROM Site WHERE uid = :uid)) AND createTime > '"+ dateFormatter.format(new Date()) + " 00:00:00'", params);
    }

    public Long countTodayError(String missionId) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("missionId", missionId);
        return this.baseDao.countBySession("SELECT count(*) FROM MissionLog WHERE missionId = :missionId AND type = 'error' AND createTime > '"+ dateFormatter.format(new Date()) + " 00:00:00'", params);
    }

    public String getMissionId(String id) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("id", id);
        return this.baseDao.getSingleString("SELECT missionId FROM MissionLog WHERE id = :id", params);
    }

    public boolean deleteById(String id) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("id", id);
        return this.baseDao.removeBySession("DELETE FROM MissionLog WHERE id = :id", params);
    }
}
