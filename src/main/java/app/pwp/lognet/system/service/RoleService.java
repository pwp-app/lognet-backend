package app.pwp.lognet.system.service;

import app.pwp.lognet.base.service.BaseService;
import app.pwp.lognet.system.model.Role;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;

@Service
@Transactional(rollbackFor=Exception.class)
public class RoleService extends BaseService<Role> {
    public boolean exists(String name) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("name", name);
        return this.baseDao.countBySession("SELECT count(*) FROM Role WHERE name = :name", params) > 0;
    }

    public boolean add(String name, int level) {
        Role role = new Role(name, level);
        return this.baseDao.add(role);
    }

    @Cacheable(value="queryCache", key = "'role_' + #id")
    public Role getById(long id) {
        return this.baseDao.getById(Role.class, id);
    }
    @Cacheable(value="queryCache", key = "'role_' + #name")
    public Role getByName(String name) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("name", name);
        return this.baseDao.getBySession("FROM Role WHERE name = :name", params);
    }
}
