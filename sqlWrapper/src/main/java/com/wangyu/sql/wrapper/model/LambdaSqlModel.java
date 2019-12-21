package com.wangyu.sql.wrapper.model;

import com.wangyu.sql.wrapper.enums.SqlKeyword;

/**
 * Created by wangyu
 * Date: 2019/9/5
 * Time: 1:49 PM
 * Description:
 */
public class LambdaSqlModel {

    private String columnName;

    private Object value;

    private SqlKeyword sqlKeyword;

    public LambdaSqlModel(String columnName,Object value,SqlKeyword sqlKeyword){
        this.columnName = columnName;
        this.value = value;
        this.sqlKeyword = sqlKeyword;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public SqlKeyword getSqlKeyword() {
        return sqlKeyword;
    }

    public void setSqlKeyword(SqlKeyword sqlKeyword) {
        this.sqlKeyword = sqlKeyword;
    }
}
