package com.sohot.hot.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by jsion on 15/11/19.
 */
public class CategoryItemTableItem  implements Serializable{
    private String categoryItemMainTitle;
    /**
     * 子分类列表标题
     */
    private String categoryItemTitle;
    /**
     * 子分类列表超链接
     */
    private String categoryItemHref;
    /**
     * 更新时间
     */
    private String updateDate;
    /**
     * 是否热门
     */
    private boolean isHot;

    /**
     * 所有当前分类的超链接,一共多少页,
     */
    private ArrayList<String> categoryAllPageIndex;

    public String getCategoryItemTitle() {
        return categoryItemTitle;
    }

    public void setCategoryItemTitle(String categoryItemTitle) {
        this.categoryItemTitle = categoryItemTitle;
    }

    public String getCategoryItemHref() {
        return categoryItemHref;
    }

    public void setCategoryItemHref(String categoryItemHref) {
        this.categoryItemHref = categoryItemHref;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    @Override
    public String toString() {
        return "CategoryItemTableItem{" +
                "categoryItemTitle='" + categoryItemTitle + '\'' +
                ", categoryItemHref='" + categoryItemHref + '\'' +
                ", updateDate='" + updateDate + '\'' +
                '}';
    }

    public boolean isHot() {
        return isHot;
    }

    public void setIsHot(boolean isHot) {
        this.isHot = isHot;
    }

    public String getCategoryItemMainTitle() {
        return categoryItemMainTitle;
    }

    public void setCategoryItemMainTitle(String categoryItemMainTitle) {
        this.categoryItemMainTitle = categoryItemMainTitle;
    }

    public void setCategoryAllPageIndex(ArrayList<String> categoryAllPageIndex) {
        this.categoryAllPageIndex = categoryAllPageIndex;
    }

    public ArrayList<String> getCategoryAllPageIndex() {
        return categoryAllPageIndex;
    }
}
