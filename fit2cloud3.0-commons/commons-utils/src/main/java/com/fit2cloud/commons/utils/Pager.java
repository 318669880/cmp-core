package com.fit2cloud.commons.utils;

import io.swagger.annotations.ApiModelProperty;

public class Pager<T> {
    @ApiModelProperty("返回结果集")
    private T listObject;
    @ApiModelProperty("总条数")
    private long itemCount;
    @ApiModelProperty("总页数")
    private long pageCount;

    public Pager() {
    }

    public Pager(T listObject, long itemCount, long pageCount) {
        this.listObject = listObject;
        this.itemCount = itemCount;
        this.pageCount = pageCount;
    }

    public long getPageCount() {
        return pageCount;
    }

    public void setPageCount(long pageCount) {
        this.pageCount = pageCount;
    }


    public long getItemCount() {
        return itemCount;
    }

    public void setItemCount(long itemCount) {
        this.itemCount = itemCount;
    }

    public T getListObject() {
        return listObject;
    }

    public void setListObject(T listObject) {
        this.listObject = listObject;
    }
}
