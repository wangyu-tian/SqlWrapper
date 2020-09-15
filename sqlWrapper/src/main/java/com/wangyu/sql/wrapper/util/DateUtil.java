package com.wangyu.sql.wrapper.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @Author wangyu
 * @Create: 2020/7/28 10:23 下午
 * @Description:
 */
public class DateUtil {

    public final static String format19 = "yyyy-MM-dd HH:mm:ss";

    public final static String format14 = "yyyyMMddHHmmss";

    public final static String format10 = "yyyy-MM-dd";

    public final static String format8 = "yyyyMMdd";

    public static Date now(){
        return new Date();
    }

    public static Date addDate(Date date , int field , int offset){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(field,offset);
        return calendar.getTime();
    }

    public static String date2string(Date date,String format){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date);
    }

    public static Date string2date(String dateStr,String format){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        try {
            return simpleDateFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
