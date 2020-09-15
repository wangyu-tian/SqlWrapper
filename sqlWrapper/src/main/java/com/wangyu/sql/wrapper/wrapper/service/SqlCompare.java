package com.wangyu.sql.wrapper.wrapper.service;

import com.wangyu.sql.wrapper.model.GroupByModel;
import com.wangyu.sql.wrapper.model.OrderByModel;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

/**
 * Created by wangyu
 * Date: 2019/9/5
 * Time: 10:41 AM
 * Description:
 */
public interface SqlCompare<Children, R> {

    Children groupBy(GroupByModel... groupByModels);

    Children orderBy(OrderByModel... orderByModels);

    Children and();
    Children and(Function<Children, Children> function);

    Children or();
    Children or(Function<Children, Children> function);

    default Children in(R column, Collection value){
        return in(column,value,false);
    }

    default Children notIn(R column, List value){
        return notIn(column,value,false);
    }

    Children notIn(R column, List value, boolean b);

    default Children like(R column, Object value){
        return like(column,value,false);
    }

    Children like(R column, Object value, boolean b);

    default Children eq(R column, Object value){
        return eq(column,value,false);
    }

    Children eq(R column, Object value, boolean b);

    default Children ne(R column, Object value){
        return ne(column,value,false);
    }

    Children ne(R column, Object value, boolean b);

    default Children gt(R column, Object value){
        return gt(column,value,false);
    }

    Children gt(R column, Object value, boolean b);

    default Children ge(R column, Object value){
        return ge(column,value,false);
    }

    Children ge(R column, Object value, boolean b);

    default Children lt(R column, Object value){
        return lt(column,value,false);
    }

    Children lt(R column, Object value, boolean b);

    default Children le(R column, Object value){
        return le(column,value,false);
    }

    Children le(R column, Object value, boolean b);

    Children isNull(R column);

    Children isNotNull(R column);

    Children in(R column, Collection value, boolean b);

    Children between(R column, Object startValue, Object endValue, boolean b);

    default Children between(R column, Object startValue, Object endValue){
        return between(column,startValue, endValue,false);
    }

    default String getHql(){return "";}

    default String getSql(){return "";}
}
