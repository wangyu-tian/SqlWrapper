package com.wangyu.sql.wrapper.model;

/**
 * @Author wangyu
 * @Create: 2019/9/26 2:37 下午
 * @Description:
 */
public class SqlModel<T> {
    protected T column;

    public SqlModel(T t){
        column = t;
    }

    public T getColumn() {
        return column;
    }

}
