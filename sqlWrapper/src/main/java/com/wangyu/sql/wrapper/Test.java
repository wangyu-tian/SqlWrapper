package com.wangyu.sql.wrapper;

import com.wangyu.sql.wrapper.constants.SqlWrapperConfig;
import com.wangyu.sql.wrapper.util.JpaUtil;
import com.wangyu.sql.wrapper.wrapper.SqlWrapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;


/**
 * Created by wangyu
 * Date: 2019/9/5
 * Time: 10:39 AM
 * Description:
 */
public class Test {
    private static JpaUtil jpaUtil;

    public static void main(String[] args){

        SqlWrapper<EntityModel> sqlWrapper = new SqlWrapper<>(EntityModel.class);
        sqlWrapper.eq(EntityModel::getId,12)
                .le(EntityModel::getName,"zhangsan")
                .and(wrapper->wrapper.ge(EntityModel::getName,"169219").eq(EntityModel::getId,12))
                .or(wrapper->wrapper.le(EntityModel::getName,"name").or().eq(EntityModel::getId,88))
                .orderBy(sqlWrapper.newOrderByModel(EntityModel::getId),
                        sqlWrapper.newOrderByModel(EntityModel::getName,SqlWrapperConfig.Order.DESC));
        ;
        //查询列表数据
        List<EntityModel> entityModels = jpaUtil.wrapper(sqlWrapper);
        //查询单个实体
        EntityModel model = jpaUtil.wrapperOne(sqlWrapper);
        //分页查询
        Page<EntityModel> page = jpaUtil.pageWrapper(sqlWrapper, PageRequest.of(1,10));

    }
}
