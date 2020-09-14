package com.wangyu.sql.wrapper.enums;

/**
 *  sql关键字索引枚举
 *  author: wangyu
 */
public enum SqlKeyword {
    //比较关联字
    EQ("=","1"),
    NE("<>","1"),
    GT(">","1"),
    GE(">=","1"),
    LT("<","1"),
    LE("<=","1"),
    LIKE("LIKE","1"),
    IN("IN","1"),
    NOT_IN("NOT IN","1"),
    NOT_EXISTS("NOT EXISTS","1"),
    EXISTS("EXISTS","1"),
    BETWEEN("BETWEEN","1"),

    //判断关联字
    IS_NULL("IS NULL","2"),
    IS_NOT_NULL("IS NOT NULL","2"),

    //连接关联字
    AND("AND","3"),
    OR("OR","3"),

    //分组关联字
    HAVING("HAVING","4"),
    GROUP_BY("GROUP BY","4"),

    //排序关联字
    ORDER_BY("ORDER BY","5"),

    //内部使用
    AND_LEFT("(","9"),
    AND_RIGHT(")","9"),
    OR_LEFT("(","9"),
    OR_RIGHT(")","9"),
    ;
    private final String keyword;

    private final String keyFlag;

    SqlKeyword(final String keyword, String keyFlag) {
        this.keyword = keyword;
        this.keyFlag = keyFlag;
    }


    public String getKeyword() {
        return this.keyword;
    }

    public String getKeyFlag() {
        return keyFlag;
    }
}
