package com.sohot.hot.ui.menupanel;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.SwitchCompat;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.sohot.R;

import java.util.ArrayList;


/**
 * 菜单模块
 */
public class MenuModulePanel {

    public static final String TAG = "MenuModulePanel";
    public String mName;
    private LinearLayout mMainLayout;
    private static LayoutInflater mInflater;
    private Context mContext;
    private ArrayList<SettingOption> mPanelList = new ArrayList<SettingOption>();

    public MenuModulePanel(Context context) {
        mContext = context;
        createLayout();
    }

    private void createLayout() {
        mInflater = LayoutInflater.from(mContext);
        mMainLayout = (LinearLayout) mInflater.inflate(R.layout.commond_view_settingmodule, null);
    }

    public View getView() {
        return mMainLayout;
    }

    public void removeAllPanel() {
        mMainLayout.removeAllViews();
        mPanelList.clear();
    }

    public void removePanel(SettingOption panel) {
        mMainLayout.removeView(panel.getView());
        mPanelList.remove(panel);
    }

    private ImageView getLine() {
        Resources resources = mContext.getResources();
        ImageView imageView = new ImageView(mContext);
        imageView.setBackgroundColor(mContext.getResources().getColor(R.color.common_line));
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, (int) resources.getDimension(R.dimen.commond_line_height));
        imageView.setLayoutParams(lp);
        return imageView;
    }

    public void addOption(SettingOption s) {
        mMainLayout.addView(s.getView());
        if (mMainLayout.getChildCount() > 1) {
            // 控制item之间是否有线条
//            mMainLayout.addView(getLine());
        }
        mPanelList.add(s);
    }

    public SettingOption getOption(int index) {
        return mPanelList.get(index);
    }

    public int size() {
        return mPanelList.size();
    }


    public void setEnable(boolean enable) {
        for (int i = 0, size = mPanelList.size(); i < size; i++) {
            SettingOption option = mPanelList.get(i);
            option.setEnable(enable);
        }
    }

    public int getOptionSize() {
        return mPanelList.size();
    }


    public static class SumOption extends SettingOption {
        protected TextView name;
        private TextView value;
        private TextView subValue;
        private ImageView icon;
        private ImageView vIcon;

        public SumOption() {
            view = getLayoutView();
            name = (TextView) view.findViewById(R.id.textview_name);
            value = (TextView) view.findViewById(R.id.textview_value);
            subValue = (TextView) view.findViewById(R.id.textview_sub_value);
            icon = (ImageView) view.findViewById(R.id.iv_menu_icon);
            vIcon = (ImageView) view.findViewById(R.id.iv_v_icon);
        }

        View getLayoutView() {
            return mInflater.inflate(R.layout.commond_view_setting_sum2, null);
        }

        public void setName(String n) {
            name.setText(n);
        }

        public void setValue(String n) {
            value.setText(n);
        }
        public void setValueColor(int color){
            value.setTextColor(color);
        }

        public void setSubValue(String s){
            subValue.setText(s);
        }

        public void setSubValueColor(int color){
            subValue.setTextColor(color);
        }

        public void setValue(SpannableString spanString) {
            value.setText(spanString);
        }


        public void setVShow() {
            vIcon.setVisibility(View.VISIBLE);
        }

        public View getView() {
            return view;
        }

        public void setIconViewSRC(int src) {
            icon.setImageResource(src);
        }

    }


    public interface OnSwitchCompatLisnter {
        void hasSwitchCompatClick(SwitchCompat switchCompat);
    }


    /**
     * budgetcontrol文本模块
     *
     * @author guofl
     */
    public static class TextOptionForBudget extends SettingOption {
        protected TextView name;
        private TextView value;
        private ImageView icon;
        private SwitchCompat switchCompat;
        public OnSwitchCompatLisnter switchCompatLisnter;

        public void setSwitchCompatLisnter(OnSwitchCompatLisnter switchCompatLisnter) {
            this.switchCompatLisnter = switchCompatLisnter;
        }

        public TextOptionForBudget(boolean isArrowFlag) {
            view = getLayoutView();
            name = (TextView) view.findViewById(R.id.textview_name);
            value = (TextView) view.findViewById(R.id.textview_value);
            icon = (ImageView) view.findViewById(R.id.iv_menu_icon);
            switchCompat = (SwitchCompat) view.findViewById(R.id.switch_value);
            switchCompat.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (switchCompatLisnter != null) {
                        switchCompatLisnter.hasSwitchCompatClick(switchCompat);
                    }
                }
            });
        }

        View getLayoutView() {
            return mInflater.inflate(R.layout.commond_view_setting_sum_switch, null);
        }

        public void setName(String n) {
            name.setText(n);
        }

        public void setValue(String n) {
            value.setText(n);
        }


        public String getValue() {
            return value.getText().toString();
        }

        public View getView() {
            return view;
        }

        public void setValueHint(String hint) {
            value.setHint(hint);
        }
    }

    public static abstract class SettingOption {
        protected View view;

        abstract View getView();

        public void setOnClickListener(OnClickListener listener) {
            if (null != view) {
                view.setOnClickListener(listener);
            }
        }

        public void setEnable(boolean enable) {
            if (view != null) {
                view.setEnabled(enable);
            }
        }
    }

}
