package com.sohot.hot.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.widget.TextView;

import com.sohot.R;


public class LoadingDialog extends Dialog {

    private TextView mTextView;
    private String mLoadingText;

    public LoadingDialog(Context context) {
        super(context, R.style.Translucent_NoTitle);
    }

    public LoadingDialog(Context context, boolean cancelable) {
        super(context, R.style.Translucent_NoTitle);
        this.setCancelable(cancelable);
    }

    public LoadingDialog(Context context, boolean cancelable, String loadingText) {
        super(context, R.style.Translucent_NoTitle);
        this.setCancelable(cancelable);
        this.mLoadingText = loadingText;

    }

    protected LoadingDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog_loading);
        mTextView = (TextView) findViewById(R.id.dialog_txt);
        if (TextUtils.isEmpty(mLoadingText)) {
            mLoadingText = "loading...";
        }
        mTextView.setText(mLoadingText);
    }

    @Override
    public void show() {
        mCountDownTimer.start();
        super.show();
    }

    @Override
    public void cancel() {
        mCountDownTimer.cancel();
        super.cancel();
    }

    @Override
    public void dismiss() {
        mCountDownTimer.cancel();
        super.dismiss();
    }

    private LoadingCountDownTime mCountDownTimer = new LoadingCountDownTime(1000 * 60, 1000);
    ;

    /*
     * 倒计时显示
     */
    class LoadingCountDownTime extends CountDownTimer {

        public LoadingCountDownTime(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            LoadingDialog.this.cancel();
        }

        @Override
        public void onTick(long millisUntilFinished) {
        }
    }

}
