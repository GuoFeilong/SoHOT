package com.sohot.hot.ui.categoryfragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sohot.R;
import com.sohot.hot.jsoup.GetHotData;
import com.sohot.hot.model.CategoryItem;
import com.sohot.hot.model.CategoryItemTableBookItem;
import com.sohot.hot.ui.BookDetailActivity;
import com.sohot.hot.ui.view.LoadingDialog;
import com.sohot.hot.ui.view.MusicLoading;

import java.util.ArrayList;

import tools.T;

/**
 * Created by jsion on 15/11/16.
 */
@SuppressLint("ValidFragment")
public class BookFragment extends Fragment {
    private static final int HAS_GET_NEXT_PAGE_DATA = 776;
    private static final int LOAD_MORE_DATA = 16;
    private static final int STOP_REFRESH = 998;
    private SwipeRefreshLayout swipeRefreshLayout;
    private static final int SET_UI_DATA = 100;
    private CategoryItem mCategoryItem;
    private GetHotData mGetHotData;
    private LoadingDialog loadingDialog;
    private MusicLoading musicLoading;
    private RecyclerView mPics;
    private PicItemAdapter picItemAdapter;
    private ArrayList<String> mAllPagerIndex;
    public static final String CURRENT_BOOK = "CURRENT_BOOK";
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SET_UI_DATA:
                    loadingDialog.dismiss();
                    picItemDate = (ArrayList<CategoryItemTableBookItem>) msg.obj;
                    mAllPagerIndex = picItemDate.get(0).getAllBookPageIndex();

                    Log.e("=======fragment===", "handleMessage: " + picItemDate.toString());
                    if (picItemDate != null) {
                        picItemAdapter = new PicItemAdapter(picItemDate);
                        picItemAdapter.setOnItemClickLitener(new OnItemClickLitener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                CategoryItemTableBookItem bookItem = picItemDate.get(position);
                                bookItem.setItemBookCategoryName(mCategoryItem.getCategoryItemTitle());
                                Intent intent = new Intent(getContext(), BookDetailActivity.class);
                                intent.putExtra(CURRENT_BOOK, bookItem);
                                startActivity(intent);
                            }

                            @Override
                            public void onItemLongClick(View view, int position) {
                                T.show(getContext(), "我是recy的book_longitemclick事件>>>position>>>>" + position, 0);

                            }
                        });
                        mPics.setAdapter(picItemAdapter);
                    }
                    break;
                case HAS_GET_NEXT_PAGE_DATA:
                    ArrayList<CategoryItemTableBookItem> tempData = (ArrayList<CategoryItemTableBookItem>) msg.obj;
                    if (msg.arg1 == LOAD_MORE_DATA) {
                        musicLoading.stopLoading();
                        musicLoading.setVisibility(View.GONE);
                        picItemDate.addAll(tempData);
                    } else {
                        swipeRefreshLayout.setRefreshing(false);
                        swipeRefreshLayout.clearAnimation();
                        // 取出刚才获取到的数据
                        picItemDate.addAll(0, tempData);
                    }
                    picItemAdapter.notifyDataSetChanged();

                    break;
            }
        }
    };
    private ArrayList<CategoryItemTableBookItem> picItemDate;

    public BookFragment(CategoryItem mCategoryItem) {
        this.mCategoryItem = mCategoryItem;
    }

    public BookFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_book, container, false);

        initData();
        initView(rootView);
        initEvent();

        return rootView;
    }

    private SwipeRefreshLayout.OnRefreshListener listener = new SwipeRefreshLayout.OnRefreshListener() {
        public void onRefresh() {
            // 封装下一页的数据模型,调用获取下一页数据的
            if (mAllPagerIndex != null && mAllPagerIndex.size() > 0) {
                mCategoryItem.setCategoryItemHref(mAllPagerIndex.get(0));
                mGetHotData.setHasGetHotTableBookDataListener(new GetHotData.HasGetHotTableBookDataListener() {
                    @Override
                    public void hasGetHotTableBookData(ArrayList<CategoryItemTableBookItem> filmItems) {
                        // 如果已经获取到了下一页是数据
                        mAllPagerIndex.remove(0);
                        Message msg = handler.obtainMessage();
                        msg.what = HAS_GET_NEXT_PAGE_DATA;
                        msg.obj = filmItems;
                        handler.sendMessage(msg);
                    }
                });
                mGetHotData.getCategoryTableBookItem(mCategoryItem);
            }
        }
    };

    private void initEvent() {
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.skin_colorPrimary),
                getResources().getColor(android.R.color.holo_green_light),
                getResources().getColor(android.R.color.holo_orange_light),
                getResources().getColor(R.color.skin_colorPrimaryDark));

        swipeRefreshLayout.setOnRefreshListener(listener);
        mPics.setLayoutManager(new LinearLayoutManager(getContext()));


        loadingDialog.show();
        mGetHotData.setHasGetHotTableBookDataListener(new GetHotData.HasGetHotTableBookDataListener() {
            @Override
            public void hasGetHotTableBookData(ArrayList<CategoryItemTableBookItem> filmItems) {
                Message msg = handler.obtainMessage();
                msg.what = SET_UI_DATA;
                msg.obj = filmItems;
                handler.sendMessage(msg);
            }
        });
        mGetHotData.getCategoryTableBookItem(mCategoryItem);


        mPics.setOnScrollListener(new RecyclerView.OnScrollListener() {
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
                            mGetHotData.setHasGetHotTableBookDataListener(new GetHotData.HasGetHotTableBookDataListener() {
                                @Override
                                public void hasGetHotTableBookData(ArrayList<CategoryItemTableBookItem> filmItems) {
                                    // 如果已经获取到了下一页是数据
                                    mAllPagerIndex.remove(0);
                                    Message msg = handler.obtainMessage();
                                    msg.what = HAS_GET_NEXT_PAGE_DATA;
                                    msg.arg1 = LOAD_MORE_DATA;
                                    msg.obj = filmItems;
                                    handler.sendMessage(msg);
                                }
                            });
                            mGetHotData.getCategoryTableBookItem(mCategoryItem);
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
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_fresh_layout_book);
        mPics = (RecyclerView) rootView.findViewById(R.id.rlv_fragment_book);
        musicLoading = (MusicLoading) rootView.findViewById(R.id.books_musicloading);
    }

    private void initData() {
        mGetHotData = new GetHotData();
    }


    /**
     * recylerview的点击时间回调
     */
    public interface OnItemClickLitener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }


    /**
     * 图区title适配器
     */
    class PicItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private OnItemClickLitener mOnItemClickLitener;

        public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
            this.mOnItemClickLitener = mOnItemClickLitener;
        }

        private ArrayList<CategoryItemTableBookItem> categoryItemTableItems;

        public PicItemAdapter(ArrayList<CategoryItemTableBookItem> categoryItemTableItems) {
            this.categoryItemTableItems = categoryItemTableItems;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder viewHolder = new PicItemVH(LayoutInflater.from(getContext()).inflate(R.layout.item_fragment_pic, parent, false));
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
            PicItemVH picItemVH = (PicItemVH) holder;
            CategoryItemTableBookItem categoryItemTableBookItem = categoryItemTableItems.get(position);
            picItemVH.itemUpdateTime.setText(categoryItemTableBookItem.getItemBookUpdateTime());
            picItemVH.itemTitle.setText(categoryItemTableBookItem.getItemBookName());
            picItemVH.itemIcon.setImageResource(R.mipmap.topmenu_icn_book);

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
            return categoryItemTableItems.size();
        }

        /**
         * 图片的vh
         */
        class PicItemVH extends RecyclerView.ViewHolder {
            private ImageView itemIcon;
            private ImageView itemHotIcon;
            private TextView itemTitle;
            private TextView itemUpdateTime;

            public PicItemVH(View itemView) {
                super(itemView);
                itemIcon = (ImageView) itemView.findViewById(R.id.iv_item_icon);
                itemHotIcon = (ImageView) itemView.findViewById(R.id.iv_is_pic_item_hot);
                itemTitle = (TextView) itemView.findViewById(R.id.tv_pic_item_title);
                itemUpdateTime = (TextView) itemView.findViewById(R.id.tv_pic_item_data);
            }
        }
    }
}
