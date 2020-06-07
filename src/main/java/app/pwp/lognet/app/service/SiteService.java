package app.pwp.lognet.app.service;

import app.pwp.lognet.app.model.Site;
import app.pwp.lognet.base.service.BaseService;
import app.pwp.lognet.utils.common.HashUtils;
import app.pwp.lognet.utils.common.UUIDUtils;
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
public class SiteService extends BaseService<Site> {
    @Resource
    private EhCacheCacheManager cacheManager;

    public HashMap<String, Object> listByUser(long uid, int page, int pageSize) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("uid", uid);
        return this.baseDao.showPageWithTotal("FROM Site WHERE uid = :uid", params, page, pageSize);
    }

    public Site getById(String id) {
        return this.baseDao.getById(Site.class, id);
    }

    // 获取站点对应的用户ID
    @Cacheable(value = "queryLongCache", key = "'site_uid_' + #id", unless = "#result == null")
    public Long getUserId(String id) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("id", id);
        return this.baseDao.getSingleLong("SELECT uid FROM Site WHERE id = :id", params);
    }

    @Cacheable(value = "queryLongCache", key = "'site_id_' + #id", unless = "#result == null")
    public String getIdByKey(String key) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("key", key);
        return this.baseDao.getSingleString("SELECT id FROM Site WHERE appKey = :key", params);
    }

    public boolean add(Site site) {
        // 生成随机的appKey
        site.setAppKey(HashUtils.sha1(UUIDUtils.generate()));
        site.setEnabled(true);
        return this.baseDao.add(site);
    }

    public boolean update(Site site) {
        return this.baseDao.updateEntity(site);
    }

    public boolean deleteById(String id) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("id", id);
        // 删除相关的任务记录
        this.baseDao.removeBySession("DELETE FROM MissionLog INNER JOIN Mission WITH Mission.id = MissionLog.missionId WHERE Mission.siteId = :id", params);
        // 删除相关的任务
        this.baseDao.removeBySession("DELETE FROM Mission WHERE siteId = :id", params);
        // 删除相关的错误记录
        this.baseDao.removeBySession("DELETE FROM ErrorLog WHERE siteId = :id", params);
        // 处理缓存
        CacheManager manager = cacheManager.getCacheManager();
        Cache queryLongCache = manager.getCache("queryLongCache");
        try {
            queryLongCache.remove("site_id_" + id);
        } catch (IllegalStateException ise) { /* do nothing */ }
        try {
            queryLongCache.remove("site_uid_" + id);
        } catch (IllegalStateException ise) { /* do nothing */ }
        // 删除实体本身
        return this.baseDao.removeBySession("DELETE FROM Site WHERE id = :id", params);
    }
}