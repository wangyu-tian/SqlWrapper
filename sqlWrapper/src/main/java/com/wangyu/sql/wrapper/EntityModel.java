package com.wangyu.sql.wrapper;

import com.wangyu.sql.wrapper.model.OutEntityModel;

/**
 * Created by wangyu
 * Date: 2019/9/5
 * Time: 4:20 PM
 * Description:
 */
public class EntityModel extends OutEntityModel {

    private int id;

    private String name;

    private String sex;

    private String memo;

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }
}
