package com.wangyu.function;

import java.util.HashMap;
import java.util.TreeMap;

/**
 * @Author wangyu
 * @Create: 2019/9/26 2:27 下午
 * @Description:
 * TreeMap通过Comparable或者Comparator来比较数据
 * HashMap通过hashCode和equal来比较数据
 */
public class TreeMapTest {

    public static void main(String[] args) {
        TreeMap treeMap = new TreeMap();
        treeMap.put(new Key(),"1");
        treeMap.put(new Key(),"2");
        System.out.println(treeMap.size());

        HashMap hashMap = new HashMap();
        hashMap.put(new Key(),"1");
        hashMap.put(new Key(),"2");
        System.out.println(hashMap.size());
    }
}

class Key implements Comparable<Key>{

    @Override
    public int compareTo(Key o) {
        return 1;
    }

    @Override
    public int hashCode() {
        return 1;
    }

    @Override
    public boolean equals(Object obj) {
        return true;
    }
}
