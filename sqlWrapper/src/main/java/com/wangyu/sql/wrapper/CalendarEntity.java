package com.wangyu.sql.wrapper;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @Author wangyu
 * @Create: 2019/9/26 3:24 下午
 * @Description:
 */
@Table(name = "calendar")
@Entity
public class CalendarEntity {

    @Id
    private int id;
    private String date;
    private String status;
    private Date createTime;
    private String memo;
    private String data;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "CalendarEntity{" +
                "id=" + id +
                ", date='" + date + '\'' +
                ", status='" + status + '\'' +
                ", createTime=" + createTime +
                ", memo='" + memo + '\'' +
                ", data='" + data + '\'' +
                '}';
    }
}