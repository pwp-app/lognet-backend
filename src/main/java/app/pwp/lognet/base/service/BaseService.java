package app.pwp.lognet.base.service;

import app.pwp.lognet.base.dao.BaseDao;

import javax.annotation.Resource;

public abstract class BaseService<T> {
    @Resource
    public BaseDao<T> baseDao;
}
