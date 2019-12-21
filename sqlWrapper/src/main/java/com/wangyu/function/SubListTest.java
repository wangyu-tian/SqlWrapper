package com.wangyu.function;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author wangyu
 * @Create: 2019/9/26 2:00 下午
 * @Description:
 * fail-fast之SubList对象，在list通过subList进行对象拆分的时候：
 * 由于衍生出来的SubList在进行增删改查的时候都会进行checkForComodification()校验：
 * private void checkForComodification() {
 *    if (ArrayList.this.modCount != this.modCount)
 *       throw new ConcurrentModificationException();
 *  }
 *  如果原list大小发生改变时，抛出ConcurrentModificationException错误。
 */
public class SubListTest {

    public static void main(String[] args){
        List masterList = new ArrayList();
        masterList.add("1");
        masterList.add("2");
        masterList.add("3");
        List subList = masterList.subList(0,2);
        System.out.println(subList.get(0));
        masterList.add("4");
        System.out.println(subList.get(0));
    }
}
