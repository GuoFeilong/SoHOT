package com.sohot.hot.ui;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import com.mylibrary.base.BaseSkinActivity;
import com.sohot.R;

import tools.StatusBarCompat;

public class FilmDetailActivity extends BaseSkinActivity {
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_film_detail);
        StatusBarCompat.compat(this, getResources().getColor(R.color.skin_colorPrimary));
        textView = (TextView) findViewById(R.id.tv_test);
        textView.setText(Html.fromHtml(descString(), getImageGetterInstance(), null));
    }


    /**
     * 字符串
     *
     * @return
     */
    private String descString() {
        return "您消耗的总热量约等于4杯\n" + "<img src='" + R.drawable.bg_d_yumao
                + "'/>" + "\n+5只" + "<img src='" + R.drawable.bg_a_h
                + "'/>" + "\n+10个" + "<img src='"
                + R.drawable.bg_a_h + "'/>" + "<img src='" + R.drawable.bg_a_h
                + "'/>" + "\n+5只" + "<img src='" + R.drawable.bg_a_h
                + "'/>" + "\n+10个" + "<img src='"
                + R.drawable.bg_d_yumao + "'/>"+"<img src='" + R.drawable.bg_a_h
                + "'/>" + "\n+5只" + "<img src='" + R.drawable.bg_a_h
                + "'/>" + "\n+10个" + "<img src='"
                + R.drawable.bg_d_yumao + "'/>"+"<img src='" + R.drawable.bg_a_h
                + "'/>" + "\n+5只" + "<img src='" + R.drawable.bg_a_h
                + "'/>" + "\n+10个" + "<img src='"
                + R.drawable.bg_a_h + "'/>"+"<img src='" + R.drawable.bg_a_h
                + "'/>" + "\n+5只" + "<img src='" + R.drawable.bg_a_h
                + "'/>" + "\n+10个" + "<img src='"
                + R.drawable.bg_d_yumao + "'/>"+"<img src='" + R.drawable.bg_a_h
                + "'/>" + "\n+5只" + "<img src='" + R.drawable.bg_a_h
                + "'/>" + "\n+10个" + "<img src='"
                + R.drawable.bg_d_yumao+ "'/>";

    }

    /**
     * ImageGetter用于text图文混排
     *
     * @return
     */
    public Html.ImageGetter getImageGetterInstance() {
        Html.ImageGetter imgGetter = new Html.ImageGetter() {
            @Override
            public Drawable getDrawable(String source) {
                int id = Integer.parseInt(source);
                Drawable d = getResources().getDrawable(id);
                int height = d.getIntrinsicHeight();
//                int width = (int) ((float) d.getIntrinsicWidth() / (float) d
//                        .getIntrinsicHeight()) * height;
                int width = d.getIntrinsicWidth();
                if (width == 0) {
                    width = d.getIntrinsicWidth();
                }
                d.setBounds(0, 0, width, height);
                return d;
            }
        };
        return imgGetter;
    }
}
