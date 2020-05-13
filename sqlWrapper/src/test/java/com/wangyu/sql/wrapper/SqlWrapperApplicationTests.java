package com.wangyu.sql.wrapper;

import com.alibaba.fastjson.JSON;
import com.wangyu.sql.wrapper.util.JpaUtil;
import com.wangyu.sql.wrapper.wrapper.SqlWrapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SqlWrapperApplicationTests {

    @Autowired
    private JpaUtil jpaUtil;

    @Test
    public void contextLoads() {

        SqlWrapper<CalendarEntity> sqlWrapper = new SqlWrapper(CalendarEntity.class);
        sqlWrapper.ne(CalendarEntity::getId,14252,true);
//                .eq(EntityModel::getName,"zhangsan")
//                .and(wrapper->wrapper.ge(EntityModel::getName,"169219").eq(EntityModel::getId,12))
//                .or(wrapper->wrapper.le(EntityModel::getName,"name").or().eq(EntityModel::getId,88))
//                .orderBy(sqlWrapper.newOrderByModel(EntityModel::getId),
//                        sqlWrapper.newOrderByModel(EntityModel::getName,SqlWrapperConfig.Order.DESC));
        ;
        //查询列表数据
        List<CalendarEntity> entityModels = jpaUtil.wrapper(sqlWrapper);
        //查询单个实体
        CalendarEntity model = jpaUtil.wrapperOne(sqlWrapper);
        //分页查询
        Page<CalendarEntity> page = jpaUtil.pageWrapper(sqlWrapper, PageRequest.of(1,10));
        System.out.println(JSON.toJSONString(page));
    }

}
