package app.pwp.lognet.app.service;

import app.pwp.lognet.app.model.Mission;
import app.pwp.lognet.base.service.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;

@Service
@Transactional
public class MissionService extends BaseService<Mission> {
    public boolean create(Mission mission) {
        return this.baseDao.add(mission);
    }
    public boolean update(Mission mission) {
        return this.baseDao.updateEntity(mission);
    }

    public boolean deleteById(String id) {
        // 先删除所有的任务日志
        HashMap<String, Object> params = new HashMap<>();
        params.put("id", id);
        this.baseDao.removeBySession("DELETE FROM MissionLog WHERE missionId = :id", params);;
        return this.baseDao.removeBySession("DELETE FROM Mission WHERE id = :id", params);
    }
}
