package com.sohot.hot.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by jsion on 15/10/22.
 */
public class MyFragmentPagerAdapter extends FragmentStatePagerAdapter {

    /**
     * 适配器
     * 根据自己的项目和需求来选择对应的适配器是否要销毁视图并且移除 实例
     * <p/>
     * <p/>
     * 使用FragmentStatePagerAdapter会销毁掉不需要的fragment。事务提交后，
     * 可将fragment从activity的FragmentManager中彻底移除。类名中的“state”表明：
     * 在销毁fragment时，它会将其onSaveInstanceState(Bundle) 方法中的Bundle信息保存下来。
     * 用户切换回原来的页面后，保存的实例状态可用于恢复生成新的fragment.
     * FragmentPagerAdapter的做法大不相同
     * 。对于不再需要的fragment，FragmentPagerAdapter则选择调用事务的detach(Fragment) 方法
     * ，而非remove(Fragment)方法来处理它。也就是说，FragmentPagerAdapter只是销毁了fragment的视图，
     * 但仍将fragment实例保留在FragmentManager中。因此， FragmentPagerAdapter创建的fragment永远不会被销毁。
     *
     * @author guofl
     */
    private ArrayList<Fragment> fragments;
    private FragmentManager fm;

    public MyFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
        this.fm = fm;
    }

    public MyFragmentPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
        super(fm);
        this.fm = fm;
        this.fragments = fragments;
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        Object object = super.instantiateItem(container, position);
        return object;

    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }


    public void setFragments(ArrayList<Fragment> fragments) {
        if (this.fragments != null) {
            FragmentTransaction ft = fm.beginTransaction();
            for (Fragment f : this.fragments) {
                ft.remove(f);
            }
            ft.commit();
            ft = null;
            fm.executePendingTransactions();
        }
        this.fragments = fragments;
        notifyDataSetChanged();
    }

}
