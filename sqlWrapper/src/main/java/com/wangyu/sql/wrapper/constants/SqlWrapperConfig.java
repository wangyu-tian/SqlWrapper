package com.wangyu.sql.wrapper.constants;

/**
 * @Author wangyu
 * @Create: 2019/9/26 2:42 下午
 * @Description:
 */
public class SqlWrapperConfig {

    public static class Order{

        public static final String ASC = "ASC";

        public static final String DESC = "DESC";
    }

    public static class Key{
        //排序关联字
        public static final String COMPARE = "1";

        //判断关联字
        public static final String JUDGE = "2";

        //连接关联字
        public static final String LINK = "3";

        //分组关联字
        public static final String GROUP = "4";

        //排序关联字
        public static final String ORDER = "5";

        //内部关联字
        public static final String INTERIOR = "9";
    }
}
