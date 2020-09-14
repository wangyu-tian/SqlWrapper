package com.wangyu.sql.wrapper;

import com.alibaba.fastjson.JSON;
import com.wangyu.sql.wrapper.util.JpaUtil;
import com.wangyu.sql.wrapper.wrapper.SqlWrapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class SqlWrapperApplicationTests {

    @Autowired
    private JpaUtil jpaUtil;

    @Test
    public void contextLoads() {

        SqlWrapper<CalendarEntity> sqlWrapper = new SqlWrapper(CalendarEntity.class);
        sqlWrapper.ne(CalendarEntity::getId,14252,true)
                    .in(CalendarEntity::getId, Stream.of(14253,14254).collect(Collectors.toList()));
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


    /**
     * 功能使用说明
     */
    @Test
    public void readMe(){
        SqlWrapper<CalendarEntity> sqlWrapper = new SqlWrapper(CalendarEntity.class);
        // sqlWrapper支持jpa自动装配sql处理机制。
        //    EQ("="),NE("<>"),GT(">"),GE(">="),LT("<"),LE("<="),
        //    LIKE("LIKE"),
        //    IN("IN"),
        //    NOT_IN("NOT IN"),
        //    IS_NULL("IS NULL"),
        //    IS_NOT_NULL("IS NOT NULL"),
        //    NOT_EXISTS("NOT EXISTS"),
        //    EXISTS("EXISTS"),
        //    BETWEEN("BETWEEN"),
        sqlWrapper.eq(CalendarEntity::getId,1)
                .ne(CalendarEntity::getId,null,true)
                .gt(CalendarEntity::getId,3)
                .ge(CalendarEntity::getId,4)
                .lt(CalendarEntity::getId,5)
                .le(CalendarEntity::getId,6)
                .like(CalendarEntity::getId,7,false,1)
                .in(CalendarEntity::getId,Stream.of(8,9,10).collect(Collectors.toList()))
                .notIn(CalendarEntity::getId,Stream.of().collect(Collectors.toList()),true)
                .isNull(CalendarEntity::getId)
                .isNotNull(CalendarEntity::getId);

//        log.info("hql1:{}",sqlWrapper.getHql());
//        log.info("sqlWrapper1:{}",JSON.toJSONString(sqlWrapper.getParamsMap()));

        sqlWrapper.clear();

        //判断关联字
        //AND，OR
        sqlWrapper.and(wrapper->wrapper.eq(CalendarEntity::getId,1).or().eq(CalendarEntity::getId,2))
                .or().le(CalendarEntity::getId,3);
        log.info("hql2:{}",sqlWrapper.getHql());
        log.info("sqlWrapper2:{}",JSON.toJSONString(sqlWrapper.getParamsMap()));
    }

}
