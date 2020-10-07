package com.wangyu.sql.wrapper.util.inner;

class FormattingTuple {
    public static org.slf4j.helpers.FormattingTuple NULL = new org.slf4j.helpers.FormattingTuple((String)null);
    private String message;
    private Throwable throwable;
    private Object[] argArray;

    public FormattingTuple(String message) {
        this(message, (Object[])null, (Throwable)null);
    }

    public FormattingTuple(String message, Object[] argArray, Throwable throwable) {
        this.message = message;
        this.throwable = throwable;
        this.argArray = argArray;
    }

    public String getMessage() {
        return this.message;
    }

    public Object[] getArgArray() {
        return this.argArray;
    }

    public Throwable getThrowable() {
        return this.throwable;
    }
}