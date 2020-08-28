package com.fit2cloud.mc.strategy.util;



import java.util.List;

import java.util.function.Function;

import java.util.stream.Collectors;

public class ModelManagerUtil {

    /**
     * 比较两个List是否相等
     * @param list_old
     * @param list_new
     * @param action 获取list中元素的方法
     * @param <T>
     * @return
     */
    public static<T> boolean sameList(List<T> list_old, List<T> list_new, Function<T,String> action){
        if(list_old.size() != list_new.size()) return false;
        List<Object> format_news = list_new.stream().map(action).collect(Collectors.toList());
        return !list_old.stream().anyMatch(element -> !format_news.contains(action.apply(element)));
    }


}
