package com.sohot.hot.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.mylibrary.base.BaseSkinActivity;
import com.sohot.R;
import com.sohot.hot.jsoup.GetHotData;
import com.sohot.hot.model.CategoryItemTableItem;
import com.sohot.hot.model.CategoryPicItemDesc;
import com.sohot.hot.model.ChangeSkinModel;
import com.sohot.hot.ui.categoryfragments.ZongHePicFragment;
import com.sohot.hot.ui.view.LoadingDialog;

import java.util.ArrayList;

import tools.AsyncImageLoader;
import tools.StatusBarCompat;
import tools.Tools;

public class PicItemDetailActivity extends BaseSkinActivity implements View.OnClickListener {
    public static final String CURRENT_PIC_DETIAL = "CURRENT_PIC_DETIAL";
    private static final int SET_UI_DATA = 100;
    private CategoryItemTableItem mCurrentPicData;
    private LoadingDialog mLoadingDialog;
    private RecyclerView mPics;
    private GetHotData mGetHotData;
    private AsyncImageLoader imageDownloader;
    private PicDetailAdapter mAdapter;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SET_UI_DATA:
                    mLoadingDialog.dismiss();
                    final ArrayList<CategoryPicItemDesc> allPics = (ArrayList<CategoryPicItemDesc>) msg.obj;
                    mAdapter = new PicDetailAdapter(allPics);
                    mAdapter.setOnItemClickLitener(new OnItemClickLitener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            Intent intent = new Intent(PicItemDetailActivity.this, FullImageActivity.class);
                            intent.putExtra(CURRENT_PIC_DETIAL, allPics.get(position));
                            startActivity(intent);
                        }

                        @Override
                        public void onItemLongClick(View view, int position) {

                        }
                    });
                    mPics.setLayoutManager(new LinearLayoutManager(PicItemDetailActivity.this));
                    mPics.setAdapter(mAdapter);
                    break;
            }
        }
    };
    private ChangeSkinModel mCurrentChangeSkinModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic_item_detail);

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

    private void initEvent() {
        mLoadingDialog.show();
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setSubtitle(mCurrentPicData.getCategoryItemTitle());
        actionBar.setTitle(mCurrentPicData.getCategoryItemMainTitle());
        actionBar.setDisplayHomeAsUpEnabled(true);

        if (mCurrentChangeSkinModel != null) {
            ColorDrawable colorDrawable = new ColorDrawable(mCurrentChangeSkinModel.getCardColor());
            actionBar.setBackgroundDrawable(colorDrawable);
        } else {
            ColorDrawable colorDrawable = new ColorDrawable(getResources().getColor(R.color.skin_colorPrimary));
            actionBar.setBackgroundDrawable(colorDrawable);
        }

    }

    private void initView() {
        mLoadingDialog = new LoadingDialog(this, true, getResources().getString(R.string.loading));
        mPics = (RecyclerView) findViewById(R.id.rlv_pic_details);
    }

    private void initData() {
        getIntentData();
        imageDownloader = new AsyncImageLoader();
        mGetHotData = new GetHotData();
        mGetHotData.setHasGetHotPicItemHrefsListener(new GetHotData.HasGetHotPicItemHrefsListener() {
            @Override
            public void hasGetHotTableData(ArrayList<CategoryPicItemDesc> tableItems) {
                Message msg = mHandler.obtainMessage();
                msg.what = SET_UI_DATA;
                msg.obj = tableItems;
                mHandler.sendMessage(msg);
            }
        });
        mGetHotData.getPicItemHrefs(mCurrentPicData);
    }

    private void getIntentData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            mCurrentPicData = (CategoryItemTableItem) bundle.getSerializable(ZongHePicFragment.CURRENT_PIC_ITEM);
            Log.i("获取fragment传递的值", "getIntentData: " + mCurrentPicData.toString());
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

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

    /**
     * recylerview的点击时间回调
     */
    public interface OnItemClickLitener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    /**
     * 图片浏览适配器
     */
    class PicDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        ImageLoader mImageLoader;
        private OnItemClickLitener mOnItemClickLitener;

        public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
            this.mOnItemClickLitener = mOnItemClickLitener;
        }


        private ArrayList<CategoryPicItemDesc> allPicItems;

        public PicDetailAdapter(ArrayList<CategoryPicItemDesc> allPicItems) {
            this.allPicItems = allPicItems;
            RequestQueue queue = Volley.newRequestQueue(PicItemDetailActivity.this);
            mImageLoader = new ImageLoader(queue, new BitmapCache());
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder viewHolder = new PicDetailVH(LayoutInflater.from(PicItemDetailActivity.this).inflate(R.layout.item_picdetails, parent, false));
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
//            imageDownloader.loadDrawable(allPicItems.get(position).getItemPicHrefs(), new AsyncImageLoader.ImageCallback() {
//                @Override
//                public void imageLoaded(Drawable imageDrawable, String imageUrl) {
//                    if (imageDrawable != null) {
//                        ((PicDetailVH) holder).pic.setBackground(imageDrawable);
//                    } else {
//                        ((PicDetailVH) holder).pic.setBackgroundResource(R.mipmap.bg_no_pic);
//                    }
//                }
//            });

            ((PicDetailVH) holder).pic.setDefaultImageResId(R.mipmap.bg_sohot_loading_red);
            ((PicDetailVH) holder).pic.setErrorImageResId(R.mipmap.bg_no_pic);
            ((PicDetailVH) holder).pic.setImageUrl(allPicItems.get(position).getItemPicHrefs(), mImageLoader);


            // 如果设置了回调，则设置点击事件
            if (mOnItemClickLitener != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = holder.getLayoutPosition();
                        mOnItemClickLitener.onItemClick(holder.itemView, pos);
                    }
                });

                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        int pos = holder.getLayoutPosition();
                        mOnItemClickLitener.onItemLongClick(holder.itemView, pos);
                        return false;
                    }
                });
            }

        }

        @Override
        public int getItemCount() {
            return allPicItems.size();
        }

        class PicDetailVH extends RecyclerView.ViewHolder {
            private NetworkImageView pic;

            public PicDetailVH(View itemView) {
                super(itemView);
                pic = (NetworkImageView) itemView.findViewById(R.id.xciv_pic);
            }
        }
    }

    /**
     * 使用LruCache来缓存图片
     */
    public class BitmapCache implements ImageLoader.ImageCache {

        private LruCache<String, Bitmap> mCache;

        public BitmapCache() {
            // 获取应用程序最大可用内存
            int maxMemory = (int) Runtime.getRuntime().maxMemory();
            int cacheSize = maxMemory / 8;
            mCache = new LruCache<String, Bitmap>(cacheSize) {
                @Override
                protected int sizeOf(String key, Bitmap bitmap) {
                    return bitmap.getRowBytes() * bitmap.getHeight();
                }
            };
        }

        @Override
        public Bitmap getBitmap(String url) {
            return mCache.get(url);
        }

        @Override
        public void putBitmap(String url, Bitmap bitmap) {
            mCache.put(url, bitmap);
        }

    }

}
