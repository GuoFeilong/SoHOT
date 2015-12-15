package com.sohot.hot.service;

import android.content.Context;
import android.content.Intent;

/**
 * Created by jsion on 15/11/24.
 */
public class ServiceTool {
    public static void startServices(Context context) {
        context.startService(new Intent(context, CountDownService.class));
    }
}
