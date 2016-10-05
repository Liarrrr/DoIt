package com.zhuimeng.lsy.doit.bmob;

import android.provider.ContactsContract;

import cn.bmob.v3.BmobObject;

/**
 * Created by lsy on 16-4-19.
 */
public class PlanItem extends BmobObject {
    private Integer planImage;
    private String planText;
    private ContactsContract.Data createTime;
    private ContactsContract.Data targetTime;
    private Integer colorType = 0;
    private Integer prioiv = 1;
    private String planFrom=null;
    private Boolean select = false;

    public String getPlanFrom() {
        return planFrom;
    }

    public void setPlanFrom(String planFrom) {
        this.planFrom = planFrom;
    }

    public Integer getColorType() {
        return colorType;
    }

    public void setColorType(Integer colorType) {
        this.colorType = colorType;
    }

    public Boolean getSelect() {
        return select;
    }

    public void setSelect(Boolean select) {
        this.select = select;
    }

    public void setPlanImage(Integer planImage) {
        this.planImage = planImage;
    }

    public ContactsContract.Data getCreateTime() {
        return createTime;
    }

    public void setCreateTime(ContactsContract.Data createTime) {
        this.createTime = createTime;
    }

    public ContactsContract.Data getTargetTime() {
        return targetTime;
    }

    public void setTargetTime(ContactsContract.Data targetTime) {
        this.targetTime = targetTime;
    }

    public Integer getPrioiv() {
        return prioiv;
    }

    public void setPrioiv(Integer prioiv) {
        this.prioiv = prioiv;
    }


    public Integer getPlanImage() {
        return planImage;
    }


    public String getPlanText() {
        return planText;
    }

    public void setPlanText(String name) {
        this.planText = name;
    }

    public PlanItem(int planImage, String name) {
        this.planImage = planImage;
        this.planText = name;
    }
}
