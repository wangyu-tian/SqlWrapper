package com.wangyu.sql.wrapper.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author wangyu
 * @Create: 2020/10/7 10:53 上午
 * @Description:
 */
public class BeanUtils {

    /**
     * 实体对象列表映射为指定对象
     *
     * @param objsList
     * @param columnNameList
     * @param entityClass
     * @return
     */
    public static <T> List<T> objsToEntityBatch(List<Object[]> objsList, List<String> columnNameList, Class<T> entityClass) {
        try {
            List<T> resList = new ArrayList<>();
            for (int k = 0; k < objsList.size(); k++) {
                resList.add(invoke(entityClass, columnNameList, objsList.get(k)));
            }
            return resList;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T objsToEntity(Object[] valueArray, List<String> columnNameList, Class<T> entityClass) {
        try {
            return invoke(entityClass, columnNameList, valueArray);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 反射初始化数据
     * @param entityClass
     * @param columnNameList
     * @param valueArray
     * @param <T>
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    private static <T> T invoke(Class<T> entityClass, List<String> columnNameList, Object[] valueArray) throws IllegalAccessException, InstantiationException {
        T target = entityClass.newInstance();
        for (int i = 0; i < columnNameList.size(); i++) {
            Object dataValue = valueArray[i];
            String propertyName = columnNameList.get(i);
            PropertyDescriptor targetPd = org.springframework.beans.BeanUtils.getPropertyDescriptor(entityClass, propertyName);
            if (targetPd != null) {
                Method writeMethod = targetPd.getWriteMethod();
                if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
                    writeMethod.setAccessible(true);
                }
                try {
                    writeMethod.invoke(target, dataValue);
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        return target;
    }

}
