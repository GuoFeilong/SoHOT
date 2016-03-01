package com.sohot.hot.ui;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.widget.TextView;

import com.mylibrary.base.BaseSkinActivity;
import com.sohot.R;
import com.sohot.hot.jsoup.GetHotData;
import com.sohot.hot.model.CategoryBookItemDesc;
import com.sohot.hot.model.CategoryItemTableBookItem;
import com.sohot.hot.model.ChangeSkinModel;
import com.sohot.hot.ui.categoryfragments.BookFragment;
import com.sohot.hot.ui.view.LoadingDialog;
import com.sohot.hot.ui.view.MyScrollViewF;

import java.util.ArrayList;

import tools.StatusBarCompat;
import tools.Tools;

public class BookDetailActivity extends BaseSkinActivity {
    private LoadingDialog loadingDialog;
    private GetHotData mGetHotData;

    private TextView mBookDetail;
    private CategoryItemTableBookItem mBookItem;

    private MyScrollViewF myScrollViewF;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ArrayList<CategoryBookItemDesc> bookContent = (ArrayList<CategoryBookItemDesc>) msg.obj;
            loadingDialog.dismiss();
            mBookDetail.setText(bookContent.get(0).getBookContent());
        }
    };
    private ChangeSkinModel mCurrentChangeSkinModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);


        mCurrentChangeSkinModel = (ChangeSkinModel) Tools.readObject(this, ChangeSkinActivity.CURRENT_SKIN_DATA);
        if (mCurrentChangeSkinModel != null) {
            StatusBarCompat.compat(this, mCurrentChangeSkinModel.getCardColor());
        } else {
            StatusBarCompat.compat(this, getResources().getColor(R.color.skin_colorPrimary));
        }

        initData();
        initView();
        initEvent();

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void initEvent() {
        loadingDialog.show();

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setSubtitle(mBookItem.getItemBookName());

        actionBar.setTitle(mBookItem.getItemBookCategoryName());
        actionBar.setDisplayHomeAsUpEnabled(true);

        if (mCurrentChangeSkinModel != null) {
            ColorDrawable colorDrawable = new ColorDrawable(mCurrentChangeSkinModel.getCardColor());
            actionBar.setBackgroundDrawable(colorDrawable);
        } else {
            ColorDrawable colorDrawable = new ColorDrawable(getResources().getColor(R.color.skin_colorPrimary));
            actionBar.setBackgroundDrawable(colorDrawable);
        }

        mGetHotData.setHasGetHotTableBookDetailListener(new GetHotData.HasGetHotTableBookDetailListener() {
            @Override
            public void hasGetHotTableBookDetails(ArrayList<CategoryBookItemDesc> bookDetail) {
                Message msg = handler.obtainMessage();
                msg.obj = bookDetail;
                handler.sendMessage(msg);
            }
        });

        mGetHotData.getCategoryTableBookItemDesc(mBookItem);

        myScrollViewF.setNotifyActiviyFinishListener(new MyScrollViewF.NotifyActiviyFinishListener() {
            @Override
            public void activityFinish() {
                finish();
            }
        });
    }

    private void initView() {
        loadingDialog = new LoadingDialog(this, true, getString(R.string.loading));
        mBookDetail = (TextView) findViewById(R.id.tv_book_details);
        myScrollViewF = (MyScrollViewF) findViewById(R.id.msv_book);

    }

    private void initData() {
        mGetHotData = new GetHotData();
        getIntentData();
    }

    private void getIntentData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            mBookItem = (CategoryItemTableBookItem) bundle.getSerializable(BookFragment.CURRENT_BOOK);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:// 点击返回图标事件
                this.finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
