package com.sohot.hot.model;

import java.util.ArrayList;

/**
 * Created by jsion on 15/11/19.
 */
public class CategoryForJson {
    private ArrayList<Category> categories;

    public ArrayList<Category> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<Category> categories) {
        this.categories = categories;
    }

    @Override
    public String toString() {
        return "CategoryForJson{" +
                "categories=" + categories +
                '}';
    }
}
