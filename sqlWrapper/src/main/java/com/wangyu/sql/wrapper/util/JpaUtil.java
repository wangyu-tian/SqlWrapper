package com.wangyu.sql.wrapper.util;

import com.wangyu.sql.wrapper.CalendarEntity;
import com.wangyu.sql.wrapper.wrapper.SqlWrapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Map;

/**
 * @Author wangyu
 * @Description: Copyright  2018 yiYuan Networks 上海义援网络科技有限公司. All rights reserved.
 * @Date 2019/3/15
 */
@Transactional(readOnly=true)
@Repository
public class JpaUtil{
  @PersistenceContext
  private EntityManager em;
  /**
   * 获取列表
   * @param sql
   * @param params
   * @param requiredType
   * @return
   */
  public <T> List<T> list(String sql, Map<String, Object> params, Class<T> requiredType) {
    //String hql = "select o.uuid,o.name from UserModel o where 1=1 and o.uuid=:uuid";
    Query query = em.createQuery(sql);
    if (params != null) {
      for (String key : params.keySet()) {
        query.setParameter(key, params.get(key));
      }
    }
    return query.getResultList();
  }

  public <T> List<T> list(String sql, Map<String, Object> params) {
    return list(sql,params,null);
  }
  /**
   * 获取分页数据
   * @param sql
   * @param params
   * @param pageable
   * @param requiredType
   * @return
   */
  @SuppressWarnings("unchecked")
  public <T> Page<T> page(String sql, Map<String, Object> params, Pageable pageable, Class<T> requiredType) {
    Query query = em.createQuery(sql);
    if (params != null) {
      for (String key : params.keySet()) {
        query.setParameter(key, params.get(key));
      }
    }
    if (pageable.isPaged()) {
      query.setFirstResult((int) pageable.getOffset());
      query.setMaxResults(pageable.getPageSize());
    }
    /**
     * 生成获取总数的sql
     */
    TypedQuery<Long> cQuery = (TypedQuery<Long>) em.createQuery(QueryUtils.createCountQueryFor(sql));
    if (params != null) {
      for (String key : params.keySet()) {
        cQuery.setParameter(key, params.get(key));
      }
    }
    return PageableExecutionUtils.getPage(query.getResultList(), pageable, ()->executeCountQuery(cQuery));
    //return new PageImpl<T>(query.getResultList(), pageable, executeCountQuery(cQuery));
  }

  /**
   * 计算数量
   * @param sql
   * @param params
   * @return
   */
  public int count(String sql, Map<String, Object> params) {
    TypedQuery<Long> cQuery = (TypedQuery<Long>) em.createQuery(QueryUtils.createCountQueryFor(sql));
    if (params != null) {
      for (String key : params.keySet()) {
        cQuery.setParameter(key, params.get(key));
      }
    }
    long countL = executeCountQuery(cQuery);
    return Integer.parseInt(countL+"");
  }

  /**
   * Executes a count query and transparently sums up all values returned.
   *
   * @param query must not be {@literal null}.
   * @return
   */
  private static Long executeCountQuery(TypedQuery<Long> query) {
    Assert.notNull(query, "TypedQuery must not be null!");
    List<Long> totals = query.getResultList();
    Long total = 0L;
    for (Long element : totals) {
      total += element == null ? 0 : element;
    }
    return total;
  }

  public List<CalendarEntity> wrapper(SqlWrapper<CalendarEntity> sqlWrapper) {
    return list(sqlWrapper.getHql(),sqlWrapper.getParamsMap());
  }

  public <T> Page<T> pageWrapper(SqlWrapper<T> sqlWrapper,Pageable pageable){
    return page(sqlWrapper.getHql(),sqlWrapper.getParamsMap(),pageable
            ,null);
  }
}
