package com.wangyu.sql.wrapper;

import com.wangyu.sql.wrapper.constants.SqlWrapperConfig;
import com.wangyu.sql.wrapper.support.WrapperConstant;
import com.wangyu.sql.wrapper.wrapper.SqlWrapper;

import static java.util.Arrays.asList;

/**
 * Created by wangyu
 * Date: 2019/9/5
 * Time: 10:39 AM
 * Description:
 */
public class Test {
    public static void main(String[] args){

        SqlWrapper<EntityModel> sqlWrapper = new SqlWrapper<>(EntityModel.class);
        sqlWrapper.eq(EntityModel::getId,12)
                .le(EntityModel::getName,"zhangsan")
                .and(wrapper->wrapper.ge(EntityModel::getName,"169219").eq(EntityModel::getId,12))
                .or(wrapper->wrapper.le(EntityModel::getName,"name").or().eq(EntityModel::getId,88))
                .orderBy(sqlWrapper.newOrderByModel(EntityModel::getId),
                        sqlWrapper.newOrderByModel(EntityModel::getName,SqlWrapperConfig.Order.DESC));
        ;
        System.out.println(sqlWrapper.getHql());
    }
}
