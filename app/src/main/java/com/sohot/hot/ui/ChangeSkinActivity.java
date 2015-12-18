package com.sohot.hot.ui;

import android.annotation.TargetApi;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mylibrary.SkinManager;
import com.mylibrary.base.BaseSkinActivity;
import com.sohot.R;
import com.sohot.hot.model.ChangeSkinModel;

import java.util.ArrayList;

import tools.StatusBarCompat;
import tools.Tools;

public class ChangeSkinActivity extends BaseSkinActivity implements View.OnClickListener {
    public static final String SKIN_DATA = "SKIN_DATA";
    public static final String CURRENT_SKIN_DATA = "CURRENT_SKIN_DATA";
    private ActionBar actionBar;
    private RecyclerView mChangeSkinRecy;
    private ArrayList<ChangeSkinModel> mChangeSkinData;
    private ChangeSkinModel mCurrentChangeSkinData;
    private ChangeSkinAdapter mChangeSkinAdapter;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
    private ChangeSkinModel mCurrentChangeSkinModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_skin);

        actionBar = getSupportActionBar();
        actionBar.setTitle(getResources().getString(R.string.changeskin));
        actionBar.setDisplayHomeAsUpEnabled(true);

        mCurrentChangeSkinModel = (ChangeSkinModel) Tools.readObject(this, ChangeSkinActivity.CURRENT_SKIN_DATA);
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

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        mChangeSkinRecy.setLayoutManager(gridLayoutManager);
        mChangeSkinRecy.setAdapter(mChangeSkinAdapter);

    }

    private void initView() {
        mChangeSkinRecy = (RecyclerView) findViewById(R.id.rlv_change_skin);
    }

    private void initData() {

        mChangeSkinData = (ArrayList<ChangeSkinModel>) Tools.readObject(this, SKIN_DATA);
        if (mChangeSkinData != null && mChangeSkinData.size() != 0) {

        } else {
            mChangeSkinData = new ArrayList<>();
            ChangeSkinModel changeSkinModel = new ChangeSkinModel("SoHOT冰柠檬", -1, R.mipmap.desklrc_color_3_ok, true, getResources().getColor(R.color.skin_colorPrimary_lime), "lime");
            mChangeSkinData.add(changeSkinModel);
            changeSkinModel = new ChangeSkinModel("SoHOT火热红", -1, R.mipmap.desklrc_color_1_ok, false, getResources().getColor(R.color.skin_colorPrimary_mred), "mred");
            mChangeSkinData.add(changeSkinModel);
            changeSkinModel = new ChangeSkinModel("SoHOT魅力蓝", -1, R.mipmap.desklrc_color_2_ok, false, getResources().getColor(R.color.skin_colorPrimary_blue), "blue");
            mChangeSkinData.add(changeSkinModel);
            changeSkinModel = new ChangeSkinModel("SoHOT环保绿", -1, R.mipmap.desklrc_color_3_ok, false, getResources().getColor(R.color.skin_colorPrimary_green), "green");
            mChangeSkinData.add(changeSkinModel);
            changeSkinModel = new ChangeSkinModel("SoHOT激情橙", -1, R.mipmap.desklrc_color_4_ok, false, getResources().getColor(R.color.skin_colorPrimary_dorange), "dorange");
            mChangeSkinData.add(changeSkinModel);
            changeSkinModel = new ChangeSkinModel("SoHOT风骚紫", -1, R.mipmap.desklrc_color_5_ok, false, getResources().getColor(R.color.skin_colorPrimary_purple), "purple");
            mChangeSkinData.add(changeSkinModel);

        }


        mChangeSkinAdapter = new ChangeSkinAdapter(mChangeSkinData);
        mChangeSkinAdapter.setOnItemClickLitener(new OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {

                for (int i = 0; i < mChangeSkinData.size(); i++) {
                    mChangeSkinData.get(i).setIsShowCheckIcon(false);
                }

                mChangeSkinData.get(position).setIsShowCheckIcon(true);
                mCurrentChangeSkinData = mChangeSkinData.get(position);
                SkinManager.getInstance().changeSkin(mChangeSkinData.get(position).getSkinFlag());
                StatusBarCompat.compat(ChangeSkinActivity.this, mChangeSkinData.get(position).getCardColor());

                ColorDrawable colorDrawable = new ColorDrawable(mChangeSkinData.get(position).getCardColor());
                actionBar.setBackgroundDrawable(colorDrawable);

                mChangeSkinAdapter.notifyDataSetChanged();
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:// 点击返回图标事件
                Tools.writeObject(this, SKIN_DATA, mChangeSkinData);
                Tools.writeObject(this, CURRENT_SKIN_DATA, mCurrentChangeSkinData);
                this.finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
    }


    /**
     * recylerview的点击时间回调
     */
    public interface OnItemClickLitener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    /**
     * 换肤的适配器
     */
    class ChangeSkinAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private ArrayList<ChangeSkinModel> changeSkinModels;

        private OnItemClickLitener mOnItemClickLitener;

        public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
            this.mOnItemClickLitener = mOnItemClickLitener;
        }

        public ChangeSkinAdapter(ArrayList<ChangeSkinModel> changeSkinModels) {
            this.changeSkinModels = changeSkinModels;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder viewHolder = new ChangeSkinVH(LayoutInflater.from(ChangeSkinActivity.this).inflate(R.layout.item_change_skin, parent, false));
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
            ChangeSkinVH changeSkinVH = (ChangeSkinVH) holder;
            ChangeSkinModel changeSkinModel = changeSkinModels.get(position);
            if (changeSkinModel.isShowCheckIcon()) {
                changeSkinVH.changeSkinCheckedIcon.setVisibility(View.VISIBLE);
                changeSkinVH.changeSkinCheckedIcon.setImageResource(changeSkinModel.getSkinCheckedIcon());
            } else {
                changeSkinVH.changeSkinCheckedIcon.setVisibility(View.INVISIBLE);
            }
            changeSkinVH.changeSkinTitle.setText(changeSkinModel.getSkinTitle());
            changeSkinVH.changeSkinCard.setCardBackgroundColor(changeSkinModel.getCardColor());


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
            return changeSkinModels.size();
        }

        class ChangeSkinVH extends RecyclerView.ViewHolder {
            private CardView changeSkinCard;
            private TextView changeSkinTitle;
            private ImageView changeSkinCheckedIcon;

            public ChangeSkinVH(View itemView) {
                super(itemView);
                changeSkinCard = (CardView) itemView.findViewById(R.id.cardview_change_skin);
                changeSkinTitle = (TextView) itemView.findViewById(R.id.tv_item_change_skin_title);
                changeSkinCheckedIcon = (ImageView) itemView.findViewById(R.id.iv_item_change_skin_checked_icon);
            }
        }
    }
}
