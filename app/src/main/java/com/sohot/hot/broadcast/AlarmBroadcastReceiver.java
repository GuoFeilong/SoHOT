package com.sohot.hot.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.sohot.hot.MyConstant;

import tools.FileUtils;

public class AlarmBroadcastReceiver extends BroadcastReceiver {
    public AlarmBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        FileUtils.initData(MyConstant.SOHOT_SHAKE_COUNT + "", MyConstant.PHONE_SDCARD_PAHT, MyConstant.SOHOT_SHAKE_FILE_NAME);
        String shakeConent = FileUtils.ReadTxtFile(MyConstant.PHONE_SDCARD_PAHT + "/" + MyConstant.SOHOT_SHAKE_FILE_NAME);
        Log.e("广播>>>>>", "onReceive:收到了定时广播>>>次数是:" + shakeConent.trim() + "<<<<<<<<<");
    }
}
