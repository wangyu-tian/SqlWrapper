package com.wangyu.sql.wrapper;

import com.wangyu.sql.wrapper.constants.SqlWrapperConfig;
import com.wangyu.sql.wrapper.util.JpaUtil;
import com.wangyu.sql.wrapper.wrapper.SqlWrapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Arrays.asList;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SqlWrapperApplicationTests {

    @Autowired
    private JpaUtil jpaUtil;

    @Test
    public void contextLoads() {
        SqlWrapper<CalendarEntity> sqlWrapper = new SqlWrapper<>(CalendarEntity.class);
        sqlWrapper.gt(CalendarEntity::getId,1000)
                .lt(CalendarEntity::getDate,"20100124")
                .in(CalendarEntity::getId, asList(14242,14243,14244,14245))
                .and(wrapper->wrapper.ge(CalendarEntity::getMemo,"169219").or().eq(CalendarEntity::getId,12))
                .orderBy(sqlWrapper.newOrderByModel(CalendarEntity::getId),
                        sqlWrapper.newOrderByModel(CalendarEntity::getDate, SqlWrapperConfig.Order.DESC))
        ;
        List<CalendarEntity> calendarEntityList = jpaUtil.wrapper(sqlWrapper);
        calendarEntityList.forEach(c->{
            System.out.println(c);
        });
    }

}
