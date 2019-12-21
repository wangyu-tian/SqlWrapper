package com.wangyu.sql.wrapper.model;

/**
 * @Author wangyu
 * @Create: 2019/9/26 2:38 下午
 * @Description:
 */
public class OrderByModel<T> extends SqlModel<T> {

    private String order;

    public OrderByModel(T t,String order){
        super(t);
        this.order = order;
    }

    public String getOrder() {
        return order;
    }
}
