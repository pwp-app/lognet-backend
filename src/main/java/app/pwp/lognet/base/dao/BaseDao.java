package app.pwp.lognet.base.dao;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projection;
import org.hibernate.internal.CriteriaImpl;
import org.hibernate.query.Query;
import org.springframework.orm.hibernate5.SessionFactoryUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

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
        Session session = this.getHibernateSession();
        try {
            Query<T> query =  session.createQuery(hql);
            query.setFirstResult((currentPage - 1) * pageSize).setMaxResults(pageSize);
            return query.list();
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

    public <T> boolean removeEntity(T entity) {
        try {
            this.getHibernateSession().delete(entity);
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public <T> boolean updateEntity(T entity) {
        try {
            this.getHibernateSession().update(entity);
            return true;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public List<T> findByHql(String hql){
        List<T> list;
        try{
            list = (List<T>) this.getHibernateSession().createQuery(hql).list();
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return list;
    }

    public <T> Integer addThenGetNumKey(T entity) {
        Integer id;
        try {
            id = (Integer) this.getHibernateSession().save(entity);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return id;
    }

    public <T> String addThenGetKey(T entity) {
        String id;
        try {
            id = (String) this.getHibernateSession().save(entity);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return id;
    }

    public <T> boolean add(T entity) {
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
