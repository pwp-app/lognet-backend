package app.pwp.lognet.app.service;

import app.pwp.lognet.app.model.Site;
import app.pwp.lognet.base.service.BaseService;
import app.pwp.lognet.system.ro.AdminSiteListItem;
import app.pwp.lognet.utils.common.HashUtils;
import app.pwp.lognet.utils.common.UUIDUtils;
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
public class SiteService extends BaseService<Site> {
    public HashMap<String, Object> list(int page, int pageSize) {
        HashMap<String, Object> response = new HashMap<>();
        NativeQuery query = this.baseDao.getHibernateSession().createNativeQuery("SELECT s.id, s.create_time as createTime, s.uid, u.username, s.domain, s.is_enabled ad enabled FROM lognet_site s INNER JOIN lognet_user u ON s.uid = u.id ORDER BY s.createTime DESC", "AdminSiteListItem");
        query.setFirstResult((page - 1) * pageSize).setMaxResults(pageSize);
        List<AdminSiteListItem> res = (List<AdminSiteListItem>) query.list();
        response.put("data", res);
        response.put("total", this.count());
        return response;
    }

    public HashMap<String, Object> listByUser(long uid, int page, int pageSize) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("uid", uid);
        return this.baseDao.showPageWithTotal("FROM Site WHERE uid = :uid ORDER BY createTime DESC", params, page, pageSize);
    }

    @Cacheable(value = "queryLongCache", key = "'site_enabled_' + #id")
    public boolean checkEnabled(String id) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("id", id);
        return this.baseDao.getSingleBoolean("SELECT isEnabled FROM Site WHERE id = :id", params);
    }

    @CacheEvict(value = "queryLongCache", key = "'site_enabled_' + #site.id")
    public boolean ban(Site site) {
        site.setEnabled(false);
        return this.baseDao.updateEntity(site);
    }

    @CacheEvict(value = "queryLongCache", key = "'site_enabled_' + #site.id")
    public boolean unban(Site site) {
        site.setEnabled(true);
        return this.baseDao.updateEntity(site);
    }

    @Cacheable(value = "queryLongCache", key = "'site_domain_exists_' + #uid + '_' + #domain")
    public boolean domainExists(Long uid, String domain) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("uid", uid);
        params.put("domain", domain);
        return this.baseDao.countBySession("SELECT count(*) FROM Site WHERE uid = :uid AND domain = :domain", params) > 0;
    }

    public Long count() {
        return this.baseDao.countByHql("SELECT count(*) FROM Site");
    }

    public Long countByUser(Long uid) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("uid", uid);
        return this.baseDao.countBySession("SELECT count(*) FROM Site WHERE uid = :uid", params);
    }

    @Cacheable(value = "queryLongCache", key = "'site_byId_' + #id", unless = "#result == null")
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

    @Cacheable(value = "queryLongCache", key = "'site_byKey_' + #key", unless = "#result == null")
    public Site getByKey(String key) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("key", key);
        return this.baseDao.getBySession("FROM Site WHERE appKey = :key", params);
    }

    @Cacheable(value = "queryLongCache", key = "'site_id_' + #key", unless = "#result == null")
    public String getIdByKey(String key) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("key", key);
        return this.baseDao.getSingleString("SELECT id FROM Site WHERE appKey = :key", params);
    }

    @CacheEvict(value = "queryLongCache", key="'site_domain_exists_' + #site.uid + '_' + #site.domain")
    public boolean add(Site site) {
        // 生成随机的appKey
        site.setAppKey(HashUtils.sha1(UUIDUtils.generate()));
        site.setEnabled(true);
        return this.baseDao.add(site);
    }

    @Caching(evict = {
            @CacheEvict(value = "queryLongCache", key = "'site_byKey_' + #site.appKey"),
            @CacheEvict(value = "queryLongCache", key="'site_byId_' + #site.id")
    })
    public boolean update(Site site) {
        return this.baseDao.updateEntity(site);
    }

    @Caching(evict = {
            @CacheEvict(value = "queryLongCache", key = "'site_byKey_' + #site.appKey"),
            @CacheEvict(value = "queryLongCache", key="'site_byId_' + #site.id"),
            @CacheEvict(value = "queryLongCache", key="'site_id_' + #site.appKey"),
            @CacheEvict(value = "queryLongCache", key="'site_uid_' + #site.id"),
            @CacheEvict(value = "queryLongCache", key="'site_domain_exists_' + #site.uid + '_' + #site.domain"),
            @CacheEvict(value = "queryLongCache", key = "'site_enabled_' + #stie.id")
    })
    public boolean delete(Site site) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("id", site.getId());
        // 删除相关的任务记录
        this.baseDao.removeBySession("DELETE FROM MissionLog WHERE missionId IN (SELECT id FROM Mission WHERE Mission.siteId = :id)", params);
        // 删除相关的任务
        this.baseDao.removeBySession("DELETE FROM Mission WHERE siteId = :id", params);
        // 删除相关的错误记录
        this.baseDao.removeBySession("DELETE FROM ErrorLog WHERE siteId = :id", params);
        // 删除实体本身
        return this.baseDao.removeBySession("DELETE FROM Site WHERE id = :id", params);
    }
}