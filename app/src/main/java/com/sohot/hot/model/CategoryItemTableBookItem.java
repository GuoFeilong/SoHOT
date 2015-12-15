package com.sohot.hot.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by jsion on 15/11/22.
 */
public class CategoryItemTableBookItem implements Serializable {
    private String itemBookName;
    private String itemBookUpdateTime;
    private String itemBookHref;
    private String itemBookCategoryName;

    private ArrayList<String> allBookPageIndex;

    public String getItemBookName() {
        return itemBookName;
    }

    public void setItemBookName(String itemBookName) {
        this.itemBookName = itemBookName;
    }

    public String getItemBookUpdateTime() {
        return itemBookUpdateTime;
    }

    public void setItemBookUpdateTime(String itemBookUpdateTime) {
        this.itemBookUpdateTime = itemBookUpdateTime;
    }

    public String getItemBookHref() {
        return itemBookHref;
    }

    public void setItemBookHref(String itemBookHref) {
        this.itemBookHref = itemBookHref;
    }

    public String getItemBookCategoryName() {
        return itemBookCategoryName;
    }

    public void setItemBookCategoryName(String itemBookCategoryName) {
        this.itemBookCategoryName = itemBookCategoryName;
    }

    @Override
    public String toString() {
        return "CategoryItemTableBookItem{" +
                "itemBookName='" + itemBookName + '\'' +
                ", itemBookUpdateTime='" + itemBookUpdateTime + '\'' +
                ", itemBookHref='" + itemBookHref + '\'' +
                '}';
    }

    public ArrayList<String> getAllBookPageIndex() {
        return allBookPageIndex;
    }

    public void setAllBookPageIndex(ArrayList<String> allBookPageIndex) {
        this.allBookPageIndex = allBookPageIndex;
    }
}
