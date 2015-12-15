package com.sohot.hot.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;

import com.sohot.R;
import com.sohot.hot.model.CategoryPicItemDesc;
import com.sohot.hot.ui.view.LoadingDialog;

import tools.AsyncImageLoader;

/**
 * Created by jsion on 15/11/22.
 */
public class FullImageActivity extends Activity {
    private LoadingDialog loadingDialog;
    private static final int GET_IMAGE_DATA = 99;
    private ImageView fullImageView;
    //    private LargeImageView fullImageView;
    private AsyncImageLoader asyncImageLoader;
    private CategoryPicItemDesc categoryPicItemDesc;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GET_IMAGE_DATA:
//                    try {
//                        if (msg.obj != null) {
//                            fullImageView.setInputStream(FormatTools.getInstance().Drawable2InputStream((BitmapDrawable) msg.obj));
//                        } else {
//                            fullImageView.setBackgroundResource(R.mipmap.bg_no_pic);
//                        }
//                    } catch (Exception e) {
//
//                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);
//        StatusBarCompat.compat(this, getResources().getColor(R.color.colorPrimary));

        initData();
        initView();
        initEvent();

    }

    private void initEvent() {
        asyncImageLoader.loadDrawable(categoryPicItemDesc.getItemPicHrefs(), new AsyncImageLoader.ImageCallback() {
            @Override
            public void imageLoaded(Drawable imageDrawable, String imageUrl) {
                loadingDialog.dismiss();
                if (imageDrawable != null) {
                    fullImageView.setBackground(imageDrawable);
                    fullImageView.setImageResource(R.color.trans);
                } else {
                    fullImageView.setBackgroundResource(R.mipmap.bg_no_pic);
                }


                Message msg = handler.obtainMessage();
                msg.what = GET_IMAGE_DATA;
                msg.obj = imageDrawable;
                handler.sendMessage(msg);
            }



        });


        fullImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void initView() {
        fullImageView = (ImageView) findViewById(R.id.iv_full_image);
//        fullImageView = (LargeImageView) findViewById(R.id.iv_full_image);
    }

    private void initData() {
        loadingDialog = new LoadingDialog(this,true,getString(R.string.loading));
        loadingDialog.show();
        asyncImageLoader = new AsyncImageLoader();
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        categoryPicItemDesc = (CategoryPicItemDesc) bundle.get(PicItemDetailActivity.CURRENT_PIC_DETIAL);
    }
}
