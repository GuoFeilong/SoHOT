package com.sohot.hot.model;

import java.io.Serializable;

/**
 * Created by jsion on 15/11/22.
 */
public class CategoryPicItemDesc implements Serializable {
    private String itemTitle;
    private String itemPicHref;

    public String getItemTitle() {
        return itemTitle;
    }

    public void setItemTitle(String itemTitle) {
        this.itemTitle = itemTitle;
    }

    public String getItemPicHrefs() {
        return itemPicHref;
    }

    public void setItemPicHrefs(String itemPicHref) {
        this.itemPicHref = itemPicHref;
    }

    @Override
    public String toString() {
        return "CategoryPicItemDesc{" +
                "itemTitle='" + itemTitle + '\'' +
                ", itemPicHref='" + itemPicHref + '\'' +
                '}';
    }
}
