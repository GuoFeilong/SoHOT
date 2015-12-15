package com.sohot.hot.model;

import java.io.Serializable;

/**
 * Created by jsion on 15/11/22.
 */
public class CategoryBookItemDesc implements Serializable {
    private String bookCateGory;
    private String bookName;
    private String bookContent;

    public String getBookCateGory() {
        return bookCateGory;
    }

    public void setBookCateGory(String bookCateGory) {
        this.bookCateGory = bookCateGory;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getBookContent() {
        return bookContent;
    }

    public void setBookContent(String bookContent) {
        this.bookContent = bookContent;
    }

    @Override
    public String toString() {
        return "CategoryBookItemDesc{" +
                "bookCateGory='" + bookCateGory + '\'' +
                ", bookName='" + bookName + '\'' +
                ", bookContent='" + bookContent + '\'' +
                '}';
    }
}
