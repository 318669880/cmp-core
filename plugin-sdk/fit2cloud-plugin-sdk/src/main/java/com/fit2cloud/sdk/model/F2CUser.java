package com.fit2cloud.sdk.model;

public class F2CUser extends F2CResource{
    private String id;
    //表示分配用户到工作空间是否成功
    private boolean assign;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isAssign() {
        return assign;
    }

    public void setAssign(boolean assign) {
        this.assign = assign;
    }
}
