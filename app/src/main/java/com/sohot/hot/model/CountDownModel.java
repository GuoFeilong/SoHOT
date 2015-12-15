package com.sohot.hot.model;

import java.io.Serializable;

/**
 * Created by jsion on 15/11/24.
 */
public class CountDownModel implements Serializable {
    private long countDown;

    public CountDownModel(long countDown) {
        this.countDown = countDown;
    }

    public long getCountDown() {
        return countDown;
    }

    public void setCountDown(long countDown) {
        this.countDown = countDown;
    }
}
