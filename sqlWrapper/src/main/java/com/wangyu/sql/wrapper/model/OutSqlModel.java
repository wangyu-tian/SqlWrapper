package com.wangyu.sql.wrapper.model;

import com.wangyu.sql.wrapper.model.inner.SqlModel;
import com.wangyu.sql.wrapper.support.SFunction;
import com.wangyu.sql.wrapper.util.LambdaUtils;
import com.wangyu.sql.wrapper.util.StringUtils;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author wangyu
 * @Create: 2020/9/30 1:57 下午
 * @Description: 自定义实体输出对象
 * 1.使用构造器解析输出对象
 */
public class OutSqlModel<T, R> extends SqlModel<R> {

    private Class<T> entityClass;

    private R[] args;

    private List<String> columnNameList;

    public OutSqlModel(Class<T> entityClass, R[] args) {
        this.entityClass = entityClass;
        this.args = args;
        init();
    }

    public void init() {
        columnNameList = new ArrayList<>();
        for (int i = 0; i < args.length; i++) {
            String fieldName = StringUtils.resolveFieldName(LambdaUtils.resolve((SFunction) args[i]).getImplMethodName());
            columnNameList.add(fieldName);
        }
    }

    public Class<T> getEntityClass() {
        return entityClass;
    }

    public List<String> getColumnNameList(){
        return columnNameList;
    }

    public String getColumnNames() {
        StringBuffer sbData = new StringBuffer();
        for (String columnName : columnNameList) {
            sbData.append(columnName).append(",");
        }
        return sbData.substring(0,sbData.length()-1);
    }
}
