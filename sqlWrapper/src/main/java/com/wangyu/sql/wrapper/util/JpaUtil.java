package com.wangyu.sql.wrapper.util;

import ch.qos.logback.core.joran.util.beans.BeanUtil;
import com.wangyu.sql.wrapper.model.OutEntityModel;
import com.wangyu.sql.wrapper.wrapper.SqlWrapper;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.hibernate.transform.Transformers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 *  * @Author wangyu
 *  * @Date 2019/3/15
 *  
 */
@Transactional(readOnly = true)
@Repository
public class JpaUtil {
    @PersistenceContext
    private EntityManager em;


    /**
     * 查询获取列表
     *
     * @param hql
     * @param params
     * @param <T>
     * @return
     */
    public <T> List<T> list(String hql, Map<String, Object> params) {
        Query query = queryParams(hql, params);
        return query.getResultList();
    }

    /**
     * 查询单数据
     *
     * @param hql
     * @param params
     * @param <T>
     * @return
     */
    private <T> T one(String hql, Map<String, Object> params) {
        Query query = queryParams(hql, params);
        query.setMaxResults(1);
        return (T) query.getSingleResult();
    }

    /**
     * 获取query
     *
     * @param hql
     * @param params
     * @return
     */
    private Query queryParams(String hql, Map<String, Object> params) {
        Query query = em.createQuery(hql);
        if (params != null) {
            for (String key : params.keySet()) {
                query.setParameter(key, params.get(key));
            }
        }
        return query;
    }

    /**
     * 设置参数
     *
     * @param query
     * @param params
     * @return
     */
    private Query setQueryParams(Query query, Map<String, Object> params) {
        if (params != null) {
            for (String key : params.keySet()) {
                query.setParameter(key, params.get(key));
            }
        }
        return query;
    }


    /**
     *  * 获取分页数据
     *  * @param sql
     *  * @param params
     *  * @param pageable: PageRequest.of(currentPage, pageSize);
     *  * @param requiredType
     *  * @return
     *  
     */
    @SuppressWarnings("unchecked")
    public <T> Page<T> page(String hql, Map<String, Object> params, Pageable pageable) {
        Query query = queryParams(hql, params);
        if (pageable.isPaged()) {
            query.setFirstResult((int) pageable.getOffset());
            query.setMaxResults(pageable.getPageSize());
        }
        /**
          * 生成获取总数的hql
          */
        TypedQuery<Long> cQuery = (TypedQuery<Long>) em.createQuery(QueryUtils.createCountQueryFor(hql));
        setQueryParams(cQuery, params);
        return PageableExecutionUtils.getPage(query.getResultList(), pageable, () -> executeCountQuery(cQuery));
    }

    /**
     *  * 计算数量
     *  * @param sql
     *  * @param params
     *  * @return
     *  
     */
    public int count(String hql, Map<String, Object> params) {
        TypedQuery<Long> cQuery = (TypedQuery<Long>) em.createQuery(QueryUtils.createCountQueryFor(hql));
        setQueryParams(cQuery, params);
        long countL = executeCountQuery(cQuery);
        return Integer.parseInt(String.valueOf(countL));
    }

    /**
     *  * Executes a count query and transparently sums up all values returned.
     *  *
     *  * @param query must not be {@literal null}.
     *  * @return
     *  
     */
    private Long executeCountQuery(TypedQuery<Long> query) {
        Assert.notNull(query, "TypedQuery must not be null!");
        return query.getSingleResult();
    }


    //wrapper实现

    /**
     * 查询单数据
     *
     * @param sqlWrapper
     * @param <T>
     * @return
     */
    public <T> T wrapperOne(SqlWrapper<T> sqlWrapper) {
        return one(sqlWrapper.getHql(), sqlWrapper.getParamsMap());
    }

    public <R> R wrapperOne(SqlWrapper sqlWrapper, Class<R> modelClass) {
        return (R) BeanUtils.objsToEntity((Object[]) one(sqlWrapper.getHql(), sqlWrapper.getParamsMap()),
                sqlWrapper.getOutSqlModel().getColumnNameList(), modelClass);
    }

    /**
     * 列表查询
     *
     * @param sqlWrapper
     * @param <T>
     * @return
     */
    public <T> List wrapper(SqlWrapper<T> sqlWrapper) {
        if (!ObjectUtils.isEmpty(sqlWrapper.getOutSqlModel())) {
            return BeanUtils.objsToEntityBatch(list(sqlWrapper.getHql(), sqlWrapper.getParamsMap()),
                    sqlWrapper.getOutSqlModel().getColumnNameList(), sqlWrapper.getOutSqlModel().getEntityClass());
        }
        return list(sqlWrapper.getHql(), sqlWrapper.getParamsMap());
    }

    /**
     * 分页查询
     *
     * @param sqlWrapper
     * @param pageable
     * @param <T>
     * @return
     */
    public <T> Page<T> pageWrapper(SqlWrapper<T> sqlWrapper, Pageable pageable) {
        return page(sqlWrapper.getHql(), sqlWrapper.getParamsMap(), pageable);
    }

    /**
     * 分页查询
     *
     * @param sqlWrapper
     * @param currentPage 当前页。第一页是0
     * @param pageSize    每页大小
     * @param <T>
     * @return
     */
    public <T> Page<T> pageWrapper(SqlWrapper<T> sqlWrapper, int currentPage, int pageSize) {
        return page(sqlWrapper.getHql(), sqlWrapper.getParamsMap(), PageRequest.of(currentPage, pageSize));
    }
}
