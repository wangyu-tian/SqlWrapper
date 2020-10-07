package com.wangyu.sql.wrapper.model.inner;

/**
 * @Author wangyu
 * @Create: 2019/9/26 2:37 下午
 * @Description: 扩展model父级root
 */
public class SqlModel<T> {
    protected T column;

    public SqlModel(){
    }

    public SqlModel(T t){
        column = t;
    }

    public T getColumn() {
        return column;
    }

}
