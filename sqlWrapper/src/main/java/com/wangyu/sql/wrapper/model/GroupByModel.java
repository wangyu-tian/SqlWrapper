package com.wangyu.sql.wrapper.model;

import com.wangyu.sql.wrapper.model.inner.SqlModel;

/**
 * @Author wangyu
 * @Create: 2019/9/26 2:38 下午
 * @Description:
 */
public class GroupByModel<T> extends SqlModel<T> {

    public GroupByModel(T t){
        super(t);
    }
}
