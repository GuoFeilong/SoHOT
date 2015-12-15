package com.sohot.hot.model;

import java.io.Serializable;

/**
 * Created by jsion on 15/11/19.
 */
public class CategoryItem implements Serializable {
    /**
     * 子分类标题
     */
    private String categoryItemTitle;
    /**
     * 子分类超链接,当前页面的超链接
     */
    private String categoryItemHref;



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



    @Override
    public String toString() {
        return "CategoryItem{" +
                "categoryItemTitle='" + categoryItemTitle + '\'' +
                ", categoryItemHref='" + categoryItemHref + '\'' +
                '}';
    }
}
