package app.pwp.lognet.base.dao;

import org.apache.shiro.crypto.hash.Hash;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projection;
import org.hibernate.internal.CriteriaImpl;
import org.hibernate.query.Query;
import org.springframework.orm.hibernate5.SessionFactoryUtils;
import org.springframework.stereotype.Repository;
import org.springframework.validation.ObjectError;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class BaseDao<T> {
    @PersistenceContext
    private EntityManager entityManager;

    public Session getHibernateSession() {
        return this.entityManager.unwrap(Session.class);
    }

    public SessionFactory getSessionFactory(){
        return this.getHibernateSession().getSessionFactory();
    }

    public Connection getConnection(){
        Connection connection = null;
        try{
            connection = SessionFactoryUtils.getDataSource(this.getSessionFactory()).getConnection();
        } catch (SQLException e){
            e.printStackTrace();
        }
        return connection;
    }

    public boolean updateByHql(String hql) {
        boolean flag;
        try {
            flag = this.executeHql(hql) > 0;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return flag;
    }

    public boolean removeByHql(String hql) {
        boolean flag;
        try {
            flag = this.executeHql(hql) > 0;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return flag;
    }

    public boolean removeBySession(String hql, HashMap<String, Object> params) {
        try {
            Query query = this.getHibernateSession().createQuery(hql);
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
            }
            return query.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public long countBySession(String hql, HashMap<String, Object> params) {
        try {
            Query<Long> query = this.getHibernateSession().createQuery(hql);
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
            }
            query.setMaxResults(1);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public long countByHql(String hql) {
        try{
            return (Long)this.getHibernateSession().createQuery(hql).setMaxResults(1).uniqueResult();
        } catch (Exception e){
            e.printStackTrace();
            return -1;
        }
    }

    public long countBySql(String sql) {
        try{
            return (Long)this.getHibernateSession().createSQLQuery(sql).setMaxResults(1).uniqueResult();
        } catch (Exception e){
            e.printStackTrace();
            return -1;
        }
    }

    public List<T> showPage(String hql, int currentPage, int pageSize) {
        try {
            Query<T> query =  this.getHibernateSession().createQuery(hql);
            query.setFirstResult((currentPage - 1) * pageSize).setMaxResults(pageSize);
            return query.list();
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public List<T> showPage(String hql, HashMap<String, Object> params, int currentPage, int pageSize) {
        try {
            Query<T> query =  this.getHibernateSession().createQuery(hql);
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
            }
            query.setFirstResult((currentPage - 1) * pageSize).setMaxResults(pageSize);
            return query.list();
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public HashMap<String, Object> showPageWithTotal(String hql, int currentPage, int pageSize) {
        Session session = this.getHibernateSession();
        try {
            Query<T> query =  session.createQuery(hql);
            query.setFirstResult((currentPage - 1) * pageSize).setMaxResults(pageSize);
            List<T> results = query.list();
            HashMap<String, Object> response = new HashMap<>();
            response.put("data", results);
            Query<Long> count_query =  session.createQuery("SELECT count(*) " + hql);
            count_query.setMaxResults(1);
            Long count = count_query.uniqueResult();
            response.put("total", count);
            return response;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public HashMap<String, Object> showPageWithTotal(String hql, HashMap<String, Object> params, int currentPage, int pageSize) {
        Session session = this.getHibernateSession();
        try {
            Query<T> query =  session.createQuery(hql);
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
            }
            query.setFirstResult((currentPage - 1) * pageSize).setMaxResults(pageSize);
            List<T> results = query.list();
            HashMap<String, Object> response = new HashMap<>();
            response.put("data", results);
            Query<Long> count_query =  session.createQuery("SELECT count(*) " + hql);
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                count_query.setParameter(entry.getKey(), entry.getValue());
            }
            count_query.setMaxResults(1);
            Long count = count_query.uniqueResult();
            response.put("total", count);
            return response;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public List<T> findBySql(String sql) {
        try {
            return this.getHibernateSession().createSQLQuery(sql).list();
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public long executeSql(String sql){
        try{
            return this.getHibernateSession().createSQLQuery(sql).executeUpdate();
        } catch (Exception e){
            e.printStackTrace();
            return -1;
        }
    }

    public long executeHql(String hql) {
        try {
            return this.getHibernateSession().createQuery(hql).executeUpdate();
        } catch (Exception e){
            e.printStackTrace();
            return -1;
        }
    }

    public Long getSingleLong(String hql, HashMap<String, Object> params) {
        try {
            Query<Long> query = this.getHibernateSession().createQuery(hql);
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
            }
            query.setMaxResults(1);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getSingleString(String hql, HashMap<String, Object> params) {
        try {
            Query<String> query = this.getHibernateSession().createQuery(hql);
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
            }
            query.setMaxResults(1);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public T getBySession(String hql, HashMap<String, Object> params) {
        try {
            Query<T> query = this.getHibernateSession().createQuery(hql);
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
            }
            query.setMaxResults(1);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public T getByHql(String hql) {
        T t;
        try {
            t = (T) this.getHibernateSession().createQuery(hql).setMaxResults(1).uniqueResult();
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return t;
    }

    public T getBySql(String sql) {
        T t;
        try {
            t = (T) this.getHibernateSession().createSQLQuery(sql).setMaxResults(1).uniqueResult();
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return t;
    }

    public T getById(Class clazz, Serializable id) {
        T t;
        try {
            t = (T) this.getHibernateSession().get(clazz, id);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return t;
    }

    public T getById(Class clazz, String id) {
        T t;
        try {
            t = (T) this.getHibernateSession().get(clazz, id);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return t;
    }

    public T getById(Class clazz, long id) {
        T t;
        try {
            t = (T) this.getHibernateSession().get(clazz, id);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return t;
    }

    public T getById(Class clazz, int id) {
        T t;
        try {
            t = (T) this.getHibernateSession().get(clazz, id);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return t;
    }

    public List<T> getEntityList(DetachedCriteria dc){
        Session session = this.getHibernateSession();
        Criteria criteria = dc.getExecutableCriteria(session);
        CriteriaImpl impl = (CriteriaImpl) criteria;
        Projection projection = impl.getProjection();
        criteria.setProjection(projection);
        if (projection == null) {
            criteria.setResultTransformer(CriteriaSpecification.ROOT_ENTITY);
        }
        return criteria.list();
    }

    public boolean removeEntity(T entity) {
        try {
            this.getHibernateSession().delete(entity);
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean updateEntity(T entity) {
        try {
            this.getHibernateSession().update(entity);
            return true;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public List<T> findByHql(String hql){
        try{
            List<T> list = this.getHibernateSession().createQuery(hql).list();
            return list;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public List<T> findBySession(String hql, HashMap<String, Object> params) {
        try{
            Query<T> query = this.getHibernateSession().createQuery(hql);
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
            }
            return query.list();
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public List<Long> findIDBySession(String hql, HashMap<String, Object> params) {
        try{
            Query<Long> query = this.getHibernateSession().createQuery(hql);
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
            }
            return query.list();
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public List<String> findUUIDBySession(String hql, HashMap<String, Object> params) {
        try{
            Query<String> query = this.getHibernateSession().createQuery(hql);
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
            }
            return query.list();
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public Integer addThenGetNumKey(T entity) {
        Integer id;
        try {
            id = (Integer) this.getHibernateSession().save(entity);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return id;
    }

    public String addThenGetKey(T entity) {
        String id;
        try {
            id = (String) this.getHibernateSession().save(entity);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return id;
    }

    public boolean add(T entity) {
        boolean flag = false;
        try {
            Serializable serializable = this.getHibernateSession().save(entity);
            if (serializable != null){
                flag = true;
            }
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return flag;
    }
}
