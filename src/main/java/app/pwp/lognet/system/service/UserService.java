package app.pwp.lognet.system.service;

import app.pwp.lognet.system.model.User;
import app.pwp.lognet.base.service.BaseService;
import app.pwp.lognet.system.ro.AdminSiteListItem;
import app.pwp.lognet.system.ro.AdminUserListItem;
import org.hibernate.query.NativeQuery;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;

@Service
@Transactional(rollbackFor=Exception.class)
public class UserService extends BaseService<User> {
    public HashMap<String, Object> list(int page, int pageSize) {
        HashMap<String, Object> response = new HashMap<>();
        NativeQuery query = this.baseDao.getHibernateSession().createNativeQuery("SELECT u.id as uid, u.create_time as createTime, u.username as username, r.name as role, u.enabled as enabled FROM lognet_user u INNER JOIN lognet_role r ON u.role_id = r.id", "AdminUserListItem");
        query.setFirstResult((page - 1) * pageSize).setMaxResults(pageSize);
        List<AdminUserListItem> res = (List<AdminUserListItem>) query.list();
        response.put("data", res);
        response.put("total", this.count());
        return response;
    }

    public Long count() {
        return this.baseDao.countByHql("SELECT count(*) FROM User");
    }

    public boolean create(User user) {
        return this.baseDao.add(user);
    }

    public boolean update(User user) { return this.baseDao.updateEntity(user); }

    @Cacheable(value = "queryPersistCache", key = "'user_exists_' + #username", unless = "#result == false")
    public boolean existsByUsername(String username) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("username", username);
        return this.baseDao.countBySession("SELECT count(*) FROM User WHERE username = :username", params) > 0;
    }

    @Cacheable(value = "queryPersistCache", key = "'user_exists_' + #mail", unless = "#result == false")
    public boolean existsByMail(String mail) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("mail", mail);
        return this.baseDao.countBySession("SELECT count(*) FROM User WHERE email = :mail", params) > 0;
    }

    public User getByUsername(String username) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("username", username);
        return this.baseDao.getBySession("FROM User WHERE username = :username", params);
    }

    public User getByMail(String mail) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("mail", mail);
        return this.baseDao.getBySession("FROM User WHERE email = :mail", params);
    }

    public User getById(Long id) {
        return this.baseDao.getById(User.class, id);
    }
}