package com.sohot.hot.ui;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sohot.R;
import com.sohot.hot.MyConstant;
import com.sohot.hot.broadcast.AlarmBroadcastReceiver;
import com.sohot.hot.jsoup.GetHotData;
import com.sohot.hot.model.Category;
import com.sohot.hot.model.CategoryForJson;
import com.sohot.hot.ui.view.LoadingDialog;

import java.util.ArrayList;

import tools.CheckNetWorkUtils;
import tools.FileUtils;
import tools.JsonHelper;
import tools.SpTools;
import tools.StatusBarCompat;
import tools.T;
import tools.ViewUtils;


public class StartActivity extends Activity implements View.OnClickListener {
    public static final int OUT_OF_DATA = 222;
    private LoadingDialog loadingDialog;
    private GetHotData.HasGetHotMenuDataListener hasGetL;
    private GetHotData getHotData;
    private CategoryForJson cForJson;
    private Dialog dialog;

    private EditText mSoHOTDesc;
    private TextView mAccept;
    private TextView mEext;

    public static final String START_ACTIVITY_TAG = "StartActivity";
    public static final int INTO_MAIN_ACTIVITY = 10;
    //    private ImageView mStartLogo;
    private LinearLayout mLogoLetters;
    private TextView mStartDesc;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case INTO_MAIN_ACTIVITY:
                    loadingDialog.dismiss();

                    //判断文件是否存在,不存在则创建,创建的时候写入初始的体验时间
                    if (FileUtils.fileIsExists(MyConstant.PHONE_SDCARD_PAHT, MyConstant.HOT_FLAG_FILE_NAME)) {
                        String textConent = FileUtils.ReadTxtFile(MyConstant.PHONE_SDCARD_PAHT + "/" + MyConstant.HOT_FLAG_FILE_NAME);
                        // TODO: 15/11/24 文件存在读取存储是剩余时间,进入系统
                        Log.e("start界面读取的?>>>>>>>", "存储的时间是{" + textConent + "}");
                        if (Long.parseLong(textConent.trim()) > MyConstant.MAX_FREE_TRIAL_TIME) {
                            FileUtils.initData(MyConstant.FREE_TRIAL_TIME + "", MyConstant.PHONE_SDCARD_PAHT, MyConstant.HOT_FLAG_FILE_NAME);

                            // 创建记录摇一摇次数的文件
                            FileUtils.initData(MyConstant.SOHOT_SHAKE_COUNT + "", MyConstant.PHONE_SDCARD_PAHT, MyConstant.SOHOT_SHAKE_FILE_NAME);
                            ViewUtils.changeActivity(StartActivity.this, MainActivity.class);
                            overridePendingTransition(R.anim.anim_goaway, R.anim.anim_start);
                            finish();
                        } else {
                            if (Long.parseLong(textConent.trim()) > 0) {


                                // 判断摇一摇次数有没有被人发现修改
                                String shakeConent = FileUtils.ReadTxtFile(MyConstant.PHONE_SDCARD_PAHT + "/" + MyConstant.SOHOT_SHAKE_FILE_NAME);
                                if (Integer.parseInt(shakeConent.trim()) > MyConstant.SOHOT_SHAKE_COUNT) {
                                    // 如果被修改就改回原来的次数
                                    FileUtils.initData(MyConstant.SOHOT_SHAKE_COUNT + "", MyConstant.PHONE_SDCARD_PAHT, MyConstant.SOHOT_SHAKE_FILE_NAME);
                                }

                                ViewUtils.changeActivity(StartActivity.this, MainActivity.class);
                                overridePendingTransition(R.anim.anim_goaway, R.anim.anim_start);
                                finish();
                            } else {
                                T.show(StartActivity.this, getResources().getString(R.string.out_of_data), 0);
                                Message msg1 = mHandler.obtainMessage(OUT_OF_DATA);
                                mHandler.sendMessageDelayed(msg1, 2000);
                            }
                        }

//                        registAlarmBroadcastReceiver();

                    } else {
                        showSoHOTDescDialog();

                    }

                    break;
                case OUT_OF_DATA:
                    finish();
                    android.os.Process.killProcess(android.os.Process.myPid());
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_start);


        if (Build.VERSION.SDK_INT >= 23) {
            //请求权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.VIBRATE}, 10);
            //判断是否需要 向用户解释，为什么要申请该权限
            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.VIBRATE);
        }

        loadingDialog = new LoadingDialog(this, false, getResources().getString(R.string.loading));
        loadingDialog.show();
//        mStartLogo = (ImageView) findViewById(R.id.iv_start_logo);
        mLogoLetters = (LinearLayout) findViewById(R.id.ll_iv_start_logo);

        mStartDesc = (TextView) findViewById(R.id.tv_start_title);

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.anim_start_logo);
//        mStartLogo.setAnimation(animation);
        mLogoLetters.setAnimation(animation);
        mStartDesc.setAnimation(animation);

        StatusBarCompat.compat(this, getResources().getColor(R.color.common_line));


        if (CheckNetWorkUtils.isNetworkAvailable(StartActivity.this)) {
            cForJson = new CategoryForJson();
            hasGetL = new GetHotData.HasGetHotMenuDataListener() {
                @Override
                public void hasGetMenuData(ArrayList<Category> data) {
                    cForJson.setCategories(data);
                    SpTools.getInstance(getApplicationContext()).saveCurrentLanguage(JsonHelper.getHelper().genJsonByBean(cForJson));
                    Message msg = mHandler.obtainMessage();
                    msg.what = INTO_MAIN_ACTIVITY;
                    mHandler.sendMessageDelayed(msg, 1000);
                }
            };
            getHotData = new GetHotData();
            getHotData.setHasGetHotMenuDataListener(hasGetL);
            getHotData.getCategoryMenuData();
        } else {
            T.show(StartActivity.this, getResources().getString(R.string.no_internet), 0);
        }

    }

    /**
     * 显示sohot介绍的dialog
     */

    private void showSoHOTDescDialog() {
        if (dialog == null) {
            dialog = new Dialog(this, R.style.myDialogTheme);
        }
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_create_newsonglist, null, false);
        dialog.setContentView(view);
        mSoHOTDesc = (EditText) view.findViewById(R.id.et_dialog_new_songname);
        mEext = (TextView) view.findViewById(R.id.tv_dialog_cancle);
        mAccept = (TextView) view.findViewById(R.id.tv_dialog_submit);

        mAccept.setOnClickListener(this);
        mEext.setOnClickListener(this);

        dialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_dialog_cancle:
                dialog.dismiss();
                finish();
                break;
            case R.id.tv_dialog_submit:
                dialog.dismiss();
                //同意sohot声明,进行倒计时试用
                boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);   //判断sd卡是否存在
                if (sdCardExist) {
                    FileUtils.initData(MyConstant.FREE_TRIAL_TIME + "", MyConstant.PHONE_SDCARD_PAHT, MyConstant.HOT_FLAG_FILE_NAME);
                    FileUtils.initData(MyConstant.SOHOT_SHAKE_COUNT + "", MyConstant.PHONE_SDCARD_PAHT, MyConstant.SOHOT_SHAKE_FILE_NAME);
                    registAlarmBroadcastReceiver();
                    // 不存在创建后直接按照初始值进行倒计时
                    ViewUtils.changeActivity(StartActivity.this, MainActivity.class);
                    overridePendingTransition(R.anim.anim_goaway, R.anim.anim_start);
                    finish();
                }
                break;
        }
    }

    /**
     * 注册定时任务的广播
     */
    private void registAlarmBroadcastReceiver() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmBroadcastReceiver.class);
        intent.setAction("AlarmReceiver");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, 0, MyConstant.ONE_DAY_TIEM_MILL, pendingIntent);
    }
}
