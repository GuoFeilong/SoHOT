package com.sohot.hot.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sohot.R;


/**
 * Created by jsion on 15/10/30.
 */
public class MusicLoading extends RelativeLayout {
    private String musicLoadingDesc;
    private ImageView musicIcon;
    private TextView musicDesc;
    private AnimationDrawable musicA;

    private LinearLayout loadingContainer;

    public MusicLoading(Context context) {
        this(context, null);
    }

    public MusicLoading(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MusicLoading(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.music_loading_style, defStyleAttr, 0);
        int indexCount = a.getIndexCount();
        for (int i = 0; i < indexCount; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.music_loading_style_music_loading_desc:
                    musicLoadingDesc = a.getString(attr);
                    break;
                case R.styleable.music_loading_style_music_loading_bg:
                    break;
            }
        }

        a.recycle();
        creatAnim();
        creatLoadingContainer();
        initView(musicLoadingDesc);
        addView(loadingContainer);
    }

    private void creatAnim() {
        musicA = new AnimationDrawable();
        musicA.addFrame(getContext().getResources().getDrawable(R.mipmap.loading1), 150);
        musicA.addFrame(getContext().getResources().getDrawable(R.mipmap.loading2), 150);
        musicA.addFrame(getContext().getResources().getDrawable(R.mipmap.loading3), 150);
        musicA.addFrame(getContext().getResources().getDrawable(R.mipmap.loading4), 150);
        musicA.setOneShot(false);

    }

    /**
     * 生成容器
     */
    private void creatLoadingContainer() {
        loadingContainer = new LinearLayout(getContext());
        loadingContainer.setOrientation(LinearLayout.HORIZONTAL);
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        loadingContainer.setGravity(Gravity.CENTER);
        loadingContainer.setLayoutParams(params);
    }

    /**
     * 创建view
     *
     * @param musicLoadingDesc
     */
    private void initView(String musicLoadingDesc) {
        musicIcon = new ImageView(getContext());
        musicIcon.setBackground(musicA);

        musicDesc = new TextView(getContext());
        musicDesc.setText(musicLoadingDesc);
        musicDesc.setTextColor(getContext().getResources().getColor(R.color.common_text_color));
        musicDesc.setTextSize(13);

        loadingContainer.addView(musicIcon);
        loadingContainer.addView(musicDesc);


    }


    public void startLoading() {
        loadingContainer.setVisibility(VISIBLE);


        musicA.start();

    }

    public void stopLoading() {


        musicA.stop();

        loadingContainer.setVisibility(INVISIBLE);
    }

}
