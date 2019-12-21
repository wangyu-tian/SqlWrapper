package com.wangyu.sql.wrapper.support

import java.io.Serializable
import java.util.function.Function

/**
 * 支持序列化的 Function
 *
 */
@FunctionalInterface
public interface SFunction<T, R> : Function<T, R>, Serializable{

}
