package app.pwp.lognet.app.service;

import app.pwp.lognet.app.model.Mission;
import app.pwp.lognet.base.service.BaseService;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;

@Service
@Transactional(rollbackFor = Exception.class)
public class MissionService extends BaseService<Mission> {
    @Resource
    private EhCacheCacheManager cacheManager;

    public HashMap<String, Object> listBySite(String siteId, int page, int pageSize) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("siteId", siteId);
        return this.baseDao.showPageWithTotal("FROM Mission WHERE siteId = :siteId", params, page, pageSize);
    }

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
        return this.baseDao.getSingleLong("SELECT Site.uid FROM Site INNER JOIN Mission WITH Mission.siteId = Site.id WHERE Mission.id = :id", params);
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
    public boolean update(Mission mission) {
        return this.baseDao.updateEntity(mission);
    }

    public boolean deleteById(String id) {
        // 先删除所有的任务日志
        HashMap<String, Object> params = new HashMap<>();
        params.put("id", id);
        this.baseDao.removeBySession("DELETE FROM MissionLog WHERE missionId = :id", params);
        // 处理缓存
        CacheManager manager = cacheManager.getCacheManager();
        Cache queryLongCache = manager.getCache("queryLongCache");
        try {
            queryLongCache.remove("mission_uid_" + id);
        } catch (IllegalStateException ise) { /* do nothing */ }
        try {
            queryLongCache.remove("mission_site_" + id);
        } catch (IllegalStateException ise) { /* do nothing */ }
        try {
            queryLongCache.remove("mission_exists_" + id);
        } catch (IllegalStateException ise) { /* do nothing */ }
        // 删除任务
        return this.baseDao.removeBySession("DELETE FROM Mission WHERE id = :id", params);
    }
}
