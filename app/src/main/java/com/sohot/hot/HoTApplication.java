package com.sohot.hot;

import android.app.Application;

import com.mylibrary.SkinManager;


/**
 * Created by jsion on 15/11/18.
 */
public class HoTApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SkinManager.getInstance().init(getApplicationContext());
    }
}
