package com.sohot.hot.ui.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.sohot.R;
import com.sohot.hot.MyConstant;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.bean.StatusCode;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;

/**
 *
 */
public class CustomShareBoard extends PopupWindow implements OnClickListener {

    private UMSocialService mController = UMServiceFactory.getUMSocialService(MyConstant.DESCRIPTOR);
    private Activity mActivity;

    public CustomShareBoard(Activity activity) {
        super(activity);
        this.mActivity = activity;
        initView(activity);
    }

    @SuppressWarnings("deprecation")
    private void initView(Context context) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.custom_board, null);
        rootView.findViewById(R.id.wechat).setOnClickListener(this);
        rootView.findViewById(R.id.wechat_circle).setOnClickListener(this);
        rootView.findViewById(R.id.qq).setOnClickListener(this);
        rootView.findViewById(R.id.qzone).setOnClickListener(this);
        setContentView(rootView);
        setWidth(LayoutParams.MATCH_PARENT);
        setHeight(LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        setBackgroundDrawable(new BitmapDrawable());
        setTouchable(true);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.wechat:
                performShare(SHARE_MEDIA.WEIXIN);
                break;
            case R.id.wechat_circle:
                performShare(SHARE_MEDIA.WEIXIN_CIRCLE);
                break;
            case R.id.qq:
                performShare(SHARE_MEDIA.QQ);
                break;
            case R.id.qzone:
                performShare(SHARE_MEDIA.QZONE);
                break;
            default:
                break;
        }
    }

    private void performShare(SHARE_MEDIA platform) {
        mController.postShare(mActivity, platform, new SnsPostListener() {

            @Override
            public void onStart() {

            }

            @Override
            public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity entity) {
                String showText = platform.toString();
                if (eCode == StatusCode.ST_CODE_SUCCESSED) {
                    showText += "平台分享成功";
                } else {
                    showText += "平台分享失败";
                }
                Toast.makeText(mActivity, showText, Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
    }

}
