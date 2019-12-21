package com.wangyu.sql.wrapper.util;

/**
 * Created by wangyu
 * Date: 2019/9/6
 * Time: 10:28 AM
 * Description:
 */
public class WrapperUtil {

    /**
     * 分层解析
     * @return
     */
    public static boolean isZeroLevel(int level){
        return level == 0 ? true : false;
    }
}
