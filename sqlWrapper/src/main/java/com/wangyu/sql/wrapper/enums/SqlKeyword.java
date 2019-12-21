package com.wangyu.sql.wrapper.enums;

public enum SqlKeyword {
    AND("AND"),
    AND_LEFT("("),
    AND_RIGHT(")"),
    OR("OR"),
    OR_LEFT("("),
    OR_RIGHT(")"),
    IN("IN"),
    NOT_IN("NOT IN"),
    LIKE("LIKE"),
    EQ("="),
    NE("<>"),
    GT(">"),
    GE(">="),
    LT("<"),
    LE("<="),
    IS_NULL("IS NULL"),
    IS_NOT_NULL("IS NOT NULL"),
    GROUP_BY("GROUP BY"),
    HAVING("HAVING"),
    ORDER_BY("ORDER BY"),
    NOT_EXISTS("NOT EXISTS"),
    EXISTS("EXISTS"),
    BETWEEN("BETWEEN"),
    ;
    private final String keyword;

    SqlKeyword(final String keyword) {
        this.keyword = keyword;
    }

    public String getKeyword() {
        return this.keyword;
    }
}
