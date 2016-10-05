package com.zhuimeng.lsy.doit.bmob;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobUser;

/**
 * Created by lsy on 16-5-11.
 */
public class TeamTableUsers extends BmobObject {
    public BmobUser user;
    public Boolean isAdmin;
    public Boolean isSuperAdmin;

    public TeamTableUsers(String tableName){
        setTableName("User_"+tableName);
    }

    public BmobUser getUser() {
        return user;
    }

    public void setUser(BmobUser user) {
        this.user = user;
    }

    public Boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    public Boolean getSuperAdmin() {
        return isSuperAdmin;
    }

    public void setSuperAdmin(Boolean superAdmin) {
        isSuperAdmin = superAdmin;
    }
}
