package app.pwp.lognet.app.service;

import app.pwp.lognet.app.model.Site;
import app.pwp.lognet.base.service.BaseService;
import app.pwp.lognet.utils.common.HashUtils;
import app.pwp.lognet.utils.common.UUIDUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;

@Service
@Transactional
public class SiteService extends BaseService<Site> {

    public HashMap<String, Object> list(long uid, int page, int pageSize) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("uid", uid);
        return this.baseDao.showPageWithTotal("FROM Site WHERE uid = :uid", params, page, pageSize);
    }

    public Site getById(String id) {
        return this.baseDao.getById(Site.class, id);
    }

    public boolean add(Site site) {
        // 生成随机的appKey
        site.setAppKey(HashUtils.sha1(UUIDUtils.generate()));
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
        // 删除实体本身
        return this.baseDao.removeBySession("DELETE FROM Site WHERE id = :id", params);
    }
}