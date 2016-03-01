package com.sohot.hot.ui.categoryfragments;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.LruCache;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.sohot.R;
import com.sohot.hot.jsoup.GetHotData;
import com.sohot.hot.model.CategoryItem;
import com.sohot.hot.model.CategoryItemTableFilmItem;
import com.sohot.hot.ui.FilmDetailActivity;
import com.sohot.hot.ui.view.LoadingDialog;
import com.sohot.hot.ui.view.MusicLoading;

import java.util.ArrayList;

import tools.AsyncImageLoader;
import tools.T;
import tools.Tools;
import tools.ViewUtils;

/**
 * Created by jsion on 15/11/16.
 */
@SuppressLint("ValidFragment")
public class FilmFragment extends Fragment {
    private static final int HAS_GET_NEXT_PAGE_DATA = 776;
    private static final int LOAD_MORE_DATA = 16;
    private static final int STOP_REFRESH = 998;
    private SwipeRefreshLayout swipeRefreshLayout;
    private static final int SET_UI_DATA = 100;
    private CategoryItem mCategoryItem;
    private GetHotData mGetHotData;
    private LoadingDialog loadingDialog;
    private MusicLoading musicLoading;
    private ArrayList<String> mAllPagerIndex;
    private RecyclerView mFilms;
    private AsyncImageLoader imageDownloader;
    private FilmItemAdapter filmItemAdapter;
    private ArrayList<CategoryItemTableFilmItem> filmItemsData;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SET_UI_DATA:
                    loadingDialog.dismiss();
                    filmItemsData = (ArrayList<CategoryItemTableFilmItem>) msg.obj;
                    mAllPagerIndex = filmItemsData.get(0).getAllFilmPageIndex();

//                    Log.e("=======fragment===", "handleMessage: " + picItemDate.toString());
                    if (filmItemsData != null) {
                        filmItemAdapter = new FilmItemAdapter(filmItemsData);
                        filmItemAdapter.setOnItemClickLitener(new OnItemClickLitener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                CategoryItemTableFilmItem filmItem = filmItemsData.get(position);
                                final String test= "xfplay://dna=BHeXmwqdDwtXDHIcAeffDHe0DGEgDZycDHH5mGH1AwydmGEfBdMdBa|dx=265039775|mz=042813-323-carib.rmvb";
                                mGetHotData.setHasGetNextHref(new GetHotData.HasGetNextHref() {
                                    @Override
                                    public void hasGetNextHref(String nextHref) {
                                        if (Tools.openApp(getContext(), "xfplay", test)) {

                                        } else {
                                            T.show(getContext(), "请下载先锋影音", 0);
                                        }
                                    }
                                });
                                mGetHotData.getCurrentFilmNextHref(filmItem.getItemHref());

                            }

                            @Override
                            public void onItemLongClick(View view, int position) {
                                T.show(getContext(), "我是recy的film_longitemclick事件>>>position>>>>" + position, 0);
                                ViewUtils.changeActivity(getContext(), FilmDetailActivity.class);
                            }
                        });
                        mFilms.setAdapter(filmItemAdapter);
                    }
                    break;
                case HAS_GET_NEXT_PAGE_DATA:
                    ArrayList<CategoryItemTableFilmItem> tempData = (ArrayList<CategoryItemTableFilmItem>) msg.obj;
                    if (msg.arg1 == LOAD_MORE_DATA) {
                        musicLoading.stopLoading();
                        musicLoading.setVisibility(View.GONE);
                        filmItemsData.addAll(tempData);
                    } else {
                        swipeRefreshLayout.setRefreshing(false);
                        swipeRefreshLayout.clearAnimation();
                        // 取出刚才获取到的数据
                        filmItemsData.addAll(0, tempData);
                    }
                    filmItemAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    public FilmFragment(CategoryItem mCategoryItem) {
        this.mCategoryItem = mCategoryItem;
    }

    public FilmFragment() {
    }

    private SwipeRefreshLayout.OnRefreshListener listener = new SwipeRefreshLayout.OnRefreshListener() {
        public void onRefresh() {
            // 封装下一页的数据模型,调用获取下一页数据的
            if (mAllPagerIndex != null && mAllPagerIndex.size() > 0) {
                mCategoryItem.setCategoryItemHref(mAllPagerIndex.get(0));
                mGetHotData.setHasGetHotTableFilmDataListener(new GetHotData.HasGetHotTableFilmDataListener() {
                    @Override
                    public void hasGetHotTableFilmData(ArrayList<CategoryItemTableFilmItem> filmItems) {
                        // 如果已经获取到了下一页是数据
                        mAllPagerIndex.remove(0);
                        Message msg = handler.obtainMessage();
                        msg.what = HAS_GET_NEXT_PAGE_DATA;
                        msg.obj = filmItems;
                        handler.sendMessage(msg);
                    }
                });
                mGetHotData.getCategoryTableFilmItem(mCategoryItem);
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_film, container, false);

        initData();
        initView(rootView);
        initEvent();

        return rootView;
    }

    private void initEvent() {
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.skin_colorPrimary),
                getResources().getColor(android.R.color.holo_green_light),
                getResources().getColor(android.R.color.holo_orange_light),
                getResources().getColor(R.color.skin_colorPrimaryDark));

        swipeRefreshLayout.setOnRefreshListener(listener);

        mFilms.setLayoutManager(new LinearLayoutManager(getContext()));

        loadingDialog.show();
        mGetHotData.setHasGetHotTableFilmDataListener(new GetHotData.HasGetHotTableFilmDataListener() {
            @Override
            public void hasGetHotTableFilmData(ArrayList<CategoryItemTableFilmItem> filmItems) {
                Message msg = handler.obtainMessage();
                msg.what = SET_UI_DATA;
                msg.obj = filmItems;
                handler.sendMessage(msg);
            }
        });
        mGetHotData.getCategoryTableFilmItem(mCategoryItem);


        mFilms.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                LinearLayoutManager m = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    // 获取最后一个可见的item
                    int lastVisibleItem = m.findLastCompletelyVisibleItemPosition();
                    // 获取全部的item数量
                    int itemCount = m.getItemCount();

                    if (lastVisibleItem == itemCount - 1) {

                        if (mAllPagerIndex.size() <= 0) {
                            T.show(getContext(), getResources().getString(R.string.no_more_data), 0);
                        } else {
                            // 到最后加载更多
                            musicLoading.setVisibility(View.VISIBLE);
                            musicLoading.startLoading();


                            // 封装下一页的数据模型,调用获取下一页数据的
                            if (mAllPagerIndex != null && mAllPagerIndex.size() > 0) {
                                mCategoryItem.setCategoryItemHref(mAllPagerIndex.get(0));
                                mGetHotData.setHasGetHotTableFilmDataListener(new GetHotData.HasGetHotTableFilmDataListener() {
                                    @Override
                                    public void hasGetHotTableFilmData(ArrayList<CategoryItemTableFilmItem> filmItems) {
                                        // 如果已经获取到了下一页是数据
                                        mAllPagerIndex.remove(0);
                                        Message msg = handler.obtainMessage();
                                        msg.what = HAS_GET_NEXT_PAGE_DATA;
                                        msg.arg1 = LOAD_MORE_DATA;
                                        msg.obj = filmItems;
                                        handler.sendMessage(msg);
                                    }
                                });
                                mGetHotData.getCategoryTableFilmItem(mCategoryItem);
                            }
                        }


                    } else {
                        musicLoading.stopLoading();
                        musicLoading.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

    }

    private void initView(View rootView) {
        loadingDialog = new LoadingDialog(getContext(), true, getResources().getString(R.string.loading));
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_fresh_layout_film);
        mFilms = (RecyclerView) rootView.findViewById(R.id.rlv_fragment_film);
        musicLoading = (MusicLoading) rootView.findViewById(R.id.film_musicloading);
    }

    private void initData() {
        mGetHotData = new GetHotData();
        imageDownloader = new AsyncImageLoader();
    }


    /**
     * recylerview的点击时间回调
     */
    public interface OnItemClickLitener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    /**
     * 电影是适配器
     */
    class FilmItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        ImageLoader mImageLoader;


        private OnItemClickLitener mOnItemClickLitener;

        public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
            this.mOnItemClickLitener = mOnItemClickLitener;
        }

        private ArrayList<CategoryItemTableFilmItem> itemTableFilmItems;

        public FilmItemAdapter(ArrayList<CategoryItemTableFilmItem> itemTableFilmItems) {
            this.itemTableFilmItems = itemTableFilmItems;
            RequestQueue queue = Volley.newRequestQueue(getContext());
            mImageLoader = new ImageLoader(queue, new BitmapCache());
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder viewHolder = new ItemFilmVH(LayoutInflater.from(getContext()).inflate(R.layout.item_fragment_film, parent, false));
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
            final ItemFilmVH itemFilmVH = (ItemFilmVH) holder;
            CategoryItemTableFilmItem currentFilmModel = itemTableFilmItems.get(position);
//            imageDownloader.loadDrawable(currentFilmModel.getItemFilmIconNetAddress(), new AsyncImageLoader.ImageCallback() {
//                @Override
//                public void imageLoaded(Drawable imageDrawable, String imageUrl) {
//                    if (imageDrawable != null) {
//                        itemFilmVH.filmCover.setBackground(imageDrawable);
//                        itemFilmVH.filmCover.setImageResource(R.color.trans);
//                    } else {
//                        itemFilmVH.filmCover.setBackgroundResource(R.mipmap.bg_no_pic);
//                    }
//                }
//            });


            itemFilmVH.filmCover.setDefaultImageResId(R.mipmap.bg_sohot_loading_red);
            itemFilmVH.filmCover.setErrorImageResId(R.mipmap.bg_no_pic);
            itemFilmVH.filmCover.setImageUrl(currentFilmModel.getItemFilmIconNetAddress(), mImageLoader);

            itemFilmVH.filmViewCount.setText("浏览次数: 0");
            itemFilmVH.filmUpdateTime.setText("更新: 未知");
            itemFilmVH.filmName.setText(currentFilmModel.getItemFilmName());
            itemFilmVH.filmCategory.setText("类别: " + currentFilmModel.getItemCategoryName());


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
            return itemTableFilmItems.size();
        }


        class ItemFilmVH extends RecyclerView.ViewHolder {
            private NetworkImageView filmCover;
            //            private ImageView filmCover;
            private TextView filmName;
            private TextView filmCategory;
            private TextView filmUpdateTime;
            private TextView filmViewCount;

            public ItemFilmVH(View itemView) {
                super(itemView);
                filmCover = (NetworkImageView) itemView.findViewById(R.id.iv_item_film_cover);
                filmName = (TextView) itemView.findViewById(R.id.tv_item_film_name);
                filmCategory = (TextView) itemView.findViewById(R.id.tv_item_film_category);
                filmUpdateTime = (TextView) itemView.findViewById(R.id.tv_item_film_update);
                filmViewCount = (TextView) itemView.findViewById(R.id.tv_item_film_itemViewCount);
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
