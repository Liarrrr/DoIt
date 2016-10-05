package com.zhuimeng.lsy.doit.bmob;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;

public class User extends BmobUser {

    /**
     *
     */

    private static final long serialVersionUID = 1L;

    private String name;//年龄
    private List<String> myCloudPlan=new ArrayList<String>();

    public List<String> getMyCloudPlan() {
        return myCloudPlan;
    }

    public void setMyCloudPlan(List<String> myCloudPlan) {
        this.myCloudPlan = myCloudPlan;
    }

    public String getAge() {
        return name;
    }

    public void setAge(String name) {
        this.name = name;
    }

}
