package com.sohot.hot.ui.categoryfragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sohot.R;

/**
 * Created by jsion on 15/11/16.
 */
public class AAAModelFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pic_zipai, container, false);

        initData();
        initView(rootView);
        initEvent();

        return rootView;
    }

    private void initEvent() {

    }

    private void initView(View rootView) {

    }

    private void initData() {

    }
}
