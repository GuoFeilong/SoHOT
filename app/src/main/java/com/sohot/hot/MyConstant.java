package com.sohot.hot;

import android.os.Environment;

import java.util.Random;

/**
 * Created by jsion on 15/11/18.
 */
public class MyConstant {
    /**
     * 网站首页地址
     */
    public static final String HOT_ADDRESS_HOME = "http://www.yazhouse.com";

    /**
     * 请求头的标示,用来伪装jsoup抓包请求
     */
    public static final String USER_AGENT = "Mozilla/5.0 (Linux; U; Android 4.4.4; zh-cn; MI 3 Build/KTU84P) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/42.0.0.0 Mobile Safari/537.36 XiaoMi/MiuiBrowser/2.1.1";

    /**
     * 超时时间
     */
    public static final int HOT_TIME_OUT = 1000 * 60 * 5 * 10;
    /**
     * 免费体验时间秒为单位
     */
    public static final long FREE_TRIAL_TIME = 3600 + new Random().nextInt(1500);
    /**
     * 最大体验时间
     */
    public static final long MAX_FREE_TRIAL_TIME = 3600 * 24;
    /**
     * 摇到的最大的免费体验时间
     */
    public static final int MAX_FREE_SHAKE_TIME = 2000;
    /**
     * 手机自身存储路径
     */
    public static final String PHONE_SELF_PATH = Environment.getDataDirectory().getPath();
    /**
     * 手机sd卡路径
     */
    public static final String PHONE_SDCARD_PAHT = Environment.getExternalStorageDirectory().getPath();

    public static final String HOT_FLAG_FILE_NAME = "system.txt";


    /**
     * 分享相关
     */
    public static final String DESCRIPTOR = "com.umeng.share";

    public static final String SOCIAL_TITLE = "SoHOT";

    public static final String SOCIAL_CONTENT = "  ____SoHOT社会化教育APP,来自内心深处的需求,一枝红杏出墙来";


    /**
     * 摇时间相关,一天限制次数三次
     */

    public static final int SOHOT_SHAKE_COUNT = 3;

    /**
     * 保存要时间限制次数的文本
     */
    public static final String SOHOT_SHAKE_FILE_NAME = "android.txt";

    /**
     * 一天的毫秒
     */
    public static final long ONE_DAY_TIEM_MILL = 24 * 60 * 60 * 1000;

}
