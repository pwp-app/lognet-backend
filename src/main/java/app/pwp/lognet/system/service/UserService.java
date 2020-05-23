package app.pwp.lognet.system.service;

import app.pwp.lognet.system.model.User;
import app.pwp.lognet.base.service.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;

@Service
@Transactional(rollbackFor=Exception.class)
public class UserService extends BaseService<User> {
    public User getByUsername(String username) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("username", username);
        return this.baseDao.getBySession("FROM USER WHERE username = :username", params);
    }

    public User getById(Long id) {
        return this.baseDao.getById(User.class, id);
    }
}