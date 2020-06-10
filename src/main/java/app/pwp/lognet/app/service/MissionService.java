package app.pwp.lognet.app.service;

import app.pwp.lognet.app.model.Mission;
import app.pwp.lognet.app.ro.RunningMission;
import app.pwp.lognet.base.service.BaseService;
import org.hibernate.query.NativeQuery;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class MissionService extends BaseService<Mission> {
    public HashMap<String, Object> listBySite(String siteId, int page, int pageSize) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("siteId", siteId);
        return this.baseDao.showPageWithTotal("FROM Mission WHERE siteId = :siteId ORDER BY createTime DESC", params, page, pageSize);
    }

    public HashMap<String, Object> listRunningByUser(Long uid, int page, int pageSize) {
        HashMap<String, Object> response = new HashMap<>();
        NativeQuery query = this.baseDao.getHibernateSession().createNativeQuery("SELECT m.id, s.domain, m.name, m.start_time, m.end_time FROM lognet_mission m INNER JOIN lognet_sites s ON s.id = m.site_id WHERE s.uid = :uid AND m.is_enabled = 1 AND ((m.start_time IS NULL AND m.end_time is NULL) OR (m.start_time IS NULL AND CURRENT_TIME < m.end_time) OR (m.end_time is NULL AND CURRENT_TIME > m.start_time) OR (m.start_time < CURRENT_TIME AND m.end_time > CURRENT_TIME))", "RunningMission");
        query.setParameter("uid", uid);
        query.setFirstResult((page - 1) * pageSize).setMaxResults(pageSize);
        List<RunningMission> result = (List<RunningMission>) query.list();
        response.put("data", result);
        response.put("total", this.countRunningByUser(uid));
        return response;
    }

    public Long countRunningByUser(Long uid) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("uid", uid);
        return this.baseDao.countBySession("SELECT count(*) FROM Mission WHERE " +
                "siteId IN (SELECT id FROM Site WHERE uid = :uid) AND " +
                "isEnabled = true AND " +
                "((startTime < CURRENT_TIME AND endTime > CURRENT_TIME) OR " +
                "(startTime is null AND endTime > CURRENT_TIME) OR " +
                "(startTime < CURRENT_TIME AND endTime is null) OR " +
                "(startTime is null AND endTime is null))", params);
    }

    public Long countBySite(String siteId) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("siteId", siteId);
        return this.baseDao.countBySession("SELECT count(*) FROM Mission WHERE siteId = :siteId", params);
    }

    public Long countRunningBySite(String siteId) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("siteId", siteId);
        return this.baseDao.countBySession("SELECT count(*) FROM Mission WHERE siteId = :siteId AND isEnabled = true AND " +
                "((startTime < CURRENT_TIME AND endTime > CURRENT_TIME) OR " +
                "(startTime is null AND endTime > CURRENT_TIME) OR " +
                "(startTime < CURRENT_TIME AND endTime is null) OR " +
                "(startTime is null AND endTime is null))", params);
    }

    @Cacheable(value = "queryLongCache", key = "'mission_byId_' + #id", unless = "#result == null")
    public Mission getById(String id) {
        return this.baseDao.getById(Mission.class, id);
    }

    @Cacheable(value = "queryLongCache", key = "'mission_exists_' + #id", unless = "#result == false")
    public boolean exists(String id) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("id", id);
        return this.baseDao.countBySession("SELECT count(*) FROM Mission WHERE id = :id", params) > 0;
    }

    // 获取任务对应的用户ID
    @Cacheable(value = "queryLongCache", key = "'mission_uid_' + #id", unless = "#result == null")
    public Long getUserId(String id) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("id", id);
        return this.baseDao.getSingleLong("SELECT s.uid FROM Site s, Mission m WHERE m.siteId = s.id AND m.id = :id", params);
    }

    @Cacheable(value = "queryLongCache", key="'mission_site_' + #id", unless = "#result == null")
    public String getSiteId(String id) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("id", id);
        return this.baseDao.getSingleString("SELECT siteId FROM Mission WHERE id = :id", params);
    }

    public boolean create(Mission mission) {
        mission.setEnabled(true);
        return this.baseDao.add(mission);
    }

    @CacheEvict(value = "queryLongCache", key="'mission_byId_' + #mission.id")
    public boolean update(Mission mission) {
        return this.baseDao.updateEntity(mission);
    }

    @Caching(evict = {
            @CacheEvict(value = "queryLongCache",  key="'mission_site_' + #id"),
            @CacheEvict(value = "queryLongCache", key = "'mission_uid_' + #id"),
            @CacheEvict(value = "queryLongCache", key = "'mission_exists_' + #id"),
            @CacheEvict(value = "queryLongCache", key = "'mission_byId_' + #id")
    })
    public boolean deleteById(String id) {
        // 先删除所有的任务日志
        HashMap<String, Object> params = new HashMap<>();
        params.put("id", id);
        this.baseDao.removeBySession("DELETE FROM MissionLog WHERE missionId = :id", params);
        // 删除任务
        return this.baseDao.removeBySession("DELETE FROM Mission WHERE id = :id", params);
    }
}
