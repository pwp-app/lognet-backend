package app.pwp.lognet.system.service;

import app.pwp.lognet.system.model.User;
import app.pwp.lognet.base.service.BaseService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;

@Service
@Transactional(rollbackFor=Exception.class)
public class UserService extends BaseService<User> {
    public boolean create(User user) {
        return this.baseDao.add(user);
    }

    @Cacheable(value = "queryCache", key = "'user_exists_' + #username")
    public boolean existsByUsername(String username) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("username", "username");
        return this.baseDao.countBySession("SELECT count(*) FROM User WHERE username = :username", params) > 0;
    }

    @Cacheable(value = "queryCache", key = "'user_exists_' + #mail")
    public boolean existsByMail(String mail) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("mail", "mail");
        return this.baseDao.countBySession("SELECT count(*) FROM User WHERE email = :mail", params) > 0;
    }

    public User getByUsername(String username) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("username", username);
        return this.baseDao.getBySession("FROM User WHERE username = :username", params);
    }

    public User getById(Long id) {
        return this.baseDao.getById(User.class, id);
    }
}