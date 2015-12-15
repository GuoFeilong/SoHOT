package com.sohot.hot.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by jsion on 15/11/19.
 */
public class Category implements Serializable{
    /**
     * 大分类标题
     */
    private String categoryTitle;
    /**
     * 大分类孩子
     */
    private ArrayList<CategoryItem> categoryItems;

    public String getCategoryTitle() {
        return categoryTitle;
    }

    public void setCategoryTitle(String categoryTitle) {
        this.categoryTitle = categoryTitle;
    }

    public ArrayList<CategoryItem> getCategoryItems() {
        return categoryItems;
    }

    public void setCategoryItems(ArrayList<CategoryItem> categoryItems) {
        this.categoryItems = categoryItems;
    }

    @Override
    public String toString() {
        return "Category{" +
                "categoryTitle='" + categoryTitle + '\'' +
                ", categoryItems=" + categoryItems +
                '}';
    }
}
