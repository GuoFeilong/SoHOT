package com.sohot.hot.model;

import java.util.ArrayList;

/**
 * Created by jsion on 15/11/20.
 */
public class CategoryItemTableFilmItem {
    /**
     * 电影名字
     */
    private String itemFilmName;
    /**
     * 电影壁纸超链接
     */
    private String itemFilmIconNetAddress;
    /**
     * 电影超链接
     */
    private String itemHref;
    /**
     * 电影分类
     */
    private String itemCategoryName;
    /**
     * 更新时间
     */
    private String itemUpdateDate;
    /**
     * 浏览次数
     */
    private String itemViewCount;

    private ArrayList<String> allFilmPageIndex;
    public String getItemFilmIconNetAddress() {
        return itemFilmIconNetAddress;
    }

    public void setItemFilmIconNetAddress(String itemFilmIconNetAddress) {
        this.itemFilmIconNetAddress = itemFilmIconNetAddress;
    }

    public String getItemFilmName() {
        return itemFilmName;
    }

    public void setItemFilmName(String itemFilmName) {
        this.itemFilmName = itemFilmName;
    }

    public String getItemHref() {
        return itemHref;
    }

    public void setItemHref(String itemHref) {
        this.itemHref = itemHref;
    }

    public String getItemCategoryName() {
        return itemCategoryName;
    }

    public void setItemCategoryName(String itemCategoryName) {
        this.itemCategoryName = itemCategoryName;
    }

    public String getItemUpdateDate() {
        return itemUpdateDate;
    }

    public void setItemUpdateDate(String itemUpdateDate) {
        this.itemUpdateDate = itemUpdateDate;
    }

    public String getItemViewCount() {
        return itemViewCount;
    }

    public void setItemViewCount(String itemViewCount) {
        this.itemViewCount = itemViewCount;
    }

    public ArrayList<String> getAllFilmPageIndex() {
        return allFilmPageIndex;
    }

    public void setAllFilmPageIndex(ArrayList<String> allFilmPageIndex) {
        this.allFilmPageIndex = allFilmPageIndex;
    }

    @Override
    public String toString() {
        return "CategoryItemTableFilmItem{" +
                "itemFilmName='" + itemFilmName + '\'' +
                ", itemFilmIconNetAddress='" + itemFilmIconNetAddress + '\'' +
                ", itemHref='" + itemHref + '\'' +
                ", itemCategoryName='" + itemCategoryName + '\'' +
                ", itemUpdateDate='" + itemUpdateDate + '\'' +
                ", itemViewCount='" + itemViewCount + '\'' +
                '}';
    }
}
