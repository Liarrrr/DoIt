package com.zhuimeng.lsy.doit.bmob;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobUser;

/**
 * Created by lsy on 16-5-11.
 */
public class TeamTable extends BmobObject {
    public String createTableName;
    public BmobUser createUser;
    public String password;

    public String getCreateTableName() {
        return createTableName;
    }

    public void setCreateTableName(String createTableName) {
        this.createTableName = createTableName;
    }

    public BmobUser getCreateUser() {
        return createUser;
    }

    public void setCreateUser(BmobUser createUser) {
        this.createUser = createUser;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
