package com.sohot.hot.ui;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.app.ActionBar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.mylibrary.base.BaseSkinActivity;
import com.sohot.R;
import com.sohot.hot.MyConstant;
import com.sohot.hot.model.ChangeSkinModel;
import com.sohot.hot.ui.view.LoadingDialog;
import com.sohot.hot.ui.view.ShakeListener;

import java.util.Random;

import tools.FileUtils;
import tools.MusicUtils;
import tools.StatusBarCompat;
import tools.T;
import tools.Tools;

public class ShakeTimeActivity extends BaseSkinActivity implements View.OnClickListener {
    private static final int HAS_GET_SHAKE_RESULT = 65;
    public static final String SKIN_DATA = "SKIN_DATA";
    public static final String CURRENT_SKIN_DATA = "CURRENT_SKIN_DATA";
    public static final String SHAKE_TIME_KEY = "SHAKE_TIME_KEY";
    private ActionBar actionBar;
    private ChangeSkinModel mCurrentChangeSkinModel;

    private TextView mShakeTime;
    private ImageView mShakeIcon;
    private ImageView mShakeIconGirl;
    private int mShakeCount;
    private int mShakeedTime;
    private ShakeListener mShakeListener;
    private Vibrator mVibrator;
    private LoadingDialog mLoadingDialog;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HAS_GET_SHAKE_RESULT:
                    mLoadingDialog.dismiss();
                    showShakeTimeResult();
                    break;
            }
        }
    };
    public static final int SHAKE_RESULT_CODE = 45;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shake_time);

        actionBar = getSupportActionBar();
        actionBar.setTitle(getResources().getString(R.string.shake_time));
        actionBar.setDisplayHomeAsUpEnabled(true);

        mCurrentChangeSkinModel = (ChangeSkinModel) Tools.readObject(this, ShakeTimeActivity.CURRENT_SKIN_DATA);
        if (mCurrentChangeSkinModel != null) {
            StatusBarCompat.compat(this, mCurrentChangeSkinModel.getCardColor());
            ColorDrawable colorDrawable = new ColorDrawable(mCurrentChangeSkinModel.getCardColor());
            actionBar.setBackgroundDrawable(colorDrawable);
        } else {
            StatusBarCompat.compat(this, getResources().getColor(R.color.skin_colorPrimary));
            ColorDrawable colorDrawable = new ColorDrawable(getResources().getColor(R.color.skin_colorPrimary));
            actionBar.setBackgroundDrawable(colorDrawable);
        }

        initData();
        initView();
        initEvent();

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void initEvent() {
        mShakeTime.setText(getColorText(mShakeCount));
        setShakeListener();
    }


    private void initView() {
        mShakeIconGirl = (ImageView) findViewById(R.id.iv_shake_icon_gril);
        mShakeTime = (TextView) findViewById(R.id.tv_shake_left_time);
        mLoadingDialog = new LoadingDialog(this, false);
    }

    private void initData() {
        String tempCount = FileUtils.ReadTxtFile(MyConstant.PHONE_SDCARD_PAHT + "/" + MyConstant.SOHOT_SHAKE_FILE_NAME);
        mShakeCount = Integer.parseInt(tempCount.trim());
        mShakeListener = new ShakeListener(this);
        mVibrator = (Vibrator) getApplication().getSystemService(VIBRATOR_SERVICE);

    }

    /**
     * 设置摇一摇监听
     */
    private void setShakeListener() {
        mShakeListener.setOnShakeListener(new ShakeListener.OnShakeListener() {
            public void onShake() {
                if (mShakeCount <= 0) {
                    T.show(ShakeTimeActivity.this, R.string.no_shake_count, 0);
                } else {
                    mShakeListener.start();
                    mVibrator.vibrate(300);
                    startAnim(); // 开始 摇一摇手掌动画
                }

            }
        });
    }


    /**
     * 开始摇一摇动画
     */
    private void startAnim() {

        TranslateAnimation ta = new TranslateAnimation(0,10,Animation.RELATIVE_TO_SELF,Animation.RELATIVE_TO_SELF);
        ta.setInterpolator(new CycleInterpolator(10));
        ta.setDuration(1000);
        mShakeIconGirl.startAnimation(ta);

        MusicUtils musicUtils = new MusicUtils(ShakeTimeActivity.this, R.raw.shake_sound);
        musicUtils.playMusic(false);
        ta.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mLoadingDialog.show();
                Message msg = mHandler.obtainMessage();
                msg.what = HAS_GET_SHAKE_RESULT;
                mHandler.sendMessageDelayed(msg, 2000);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    /**
     * 返回概率
     *
     * @param value
     * @return
     */
    private boolean getRay(int value) {
        if (new Random().nextInt(100) <= value) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 显示摇一摇的结果
     */
    private void showShakeTimeResult() {
        if (getRay(70)) {
            // 抽到奖励时间
            mShakeedTime += new Random().nextInt(MyConstant.MAX_FREE_SHAKE_TIME);
            MusicUtils musicUtils = new MusicUtils(this, R.raw.shake_match);
            musicUtils.playMusic(false);
            String temp = String.format("%.2f", mShakeedTime / 60.0f);
            T.show(ShakeTimeActivity.this, getResources().getString(R.string.has_shake_time) + temp + "分钟!", 0);
        } else {
            // 没抽到
            MusicUtils musicUtils = new MusicUtils(this, R.raw.shake_nomatch);
            musicUtils.playMusic(false);
            T.show(ShakeTimeActivity.this, R.string.shake_no_time, 0);
        }

        mShakeCount--;
        mShakeTime.setText(getColorText(mShakeCount));
        /**
         * 写文件记录今天摇一摇剩余次数
         */
        FileUtils.initData(mShakeCount + "", MyConstant.PHONE_SDCARD_PAHT, MyConstant.SOHOT_SHAKE_FILE_NAME);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:// 点击返回图标事件
                mShakeListener.stop();
                Intent data = new Intent();
                data.putExtra(SHAKE_TIME_KEY, mShakeedTime);
                setResult(SHAKE_RESULT_CODE, data);
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
    }

    /**
     * 获取彩色的字体
     *
     * @param timeCount
     */
    private SpannableString getColorText(int timeCount) {
        //改变字体颜色
        //先构造SpannableString
        SpannableString spanString = new SpannableString("今日还能摇人家" + timeCount + "次哦");
        //再构造一个改变字体颜色的Span
        ForegroundColorSpan span = new ForegroundColorSpan(getResources().getColor(R.color.skin_colorPrimary_mred));
        //将这个Span应用于指定范围的字体
        spanString.setSpan(span, 7, 8, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

        return spanString;
    }

}
