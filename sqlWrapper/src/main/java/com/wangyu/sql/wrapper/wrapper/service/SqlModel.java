package com.wangyu.sql.wrapper.wrapper.service;

import com.wangyu.sql.wrapper.constants.SqlWrapperConfig;
import com.wangyu.sql.wrapper.model.GroupByModel;
import com.wangyu.sql.wrapper.model.OrderByModel;
import com.wangyu.sql.wrapper.model.OutSqlModel;

/**
 * @Author wangyu
 * @Create: 2019/9/26 2:44 下午
 * @Description:
 * 组合关联字处理
 */
public interface SqlModel<R> {

    default OrderByModel<R> newOrderByModel(R column,String value){
        return new OrderByModel<R>(column,value);
    }

    default OrderByModel<R> newOrderByModel(R column){
        return new OrderByModel<R>(column, SqlWrapperConfig.Order.ASC);
    }

    default GroupByModel<R> newGroupByModel(R column){
        return new GroupByModel<>(column);
    }

    default <T> OutSqlModel<T,R> newOutModel(Class<T> entityClass, R... args){
        return new OutSqlModel<T,R>(entityClass, args);
    }
}
