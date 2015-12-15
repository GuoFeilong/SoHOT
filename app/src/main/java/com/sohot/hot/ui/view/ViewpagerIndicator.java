package com.sohot.hot.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sohot.R;
import com.sohot.hot.model.ChangeSkinModel;

import java.util.List;

@SuppressLint("NewApi")
public class ViewpagerIndicator extends LinearLayout {
	/**
	 * 画笔，底部矩形
	 */
	private Paint paint;
	/**
	 * 子View个数
	 */
	private int mCount;
	/**
	 * 整体的高度
	 */
	private int mTop;
	/**
	 * 指示器的宽度
	 */
	private int mDicatorWidth;
	private int mWindowWidth;
	private HorizontalScrollView mHorizontalScrollView;
	private int mLeft;
	private int mHeight = 5;
	public OnTextClick onTextClick;
	private List<String> mTabTitles;
	/**
	 * 与之绑定的ViewPager
	 */
	public ViewPager mViewPager;

	public ViewpagerIndicator(Context context) {
		this(context, null);
	}

	public ViewpagerIndicator(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ViewpagerIndicator(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setBackgroundColor(Color.TRANSPARENT);
		paint = new Paint();
		paint.setColor(getResources().getColor(R.color.skin_colorPrimary));
		paint.setAntiAlias(true);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		// 整体高度
		mTop = getMeasuredHeight();
		// 整体宽度
		int width = getMeasuredWidth();
		// 加上指示器高度
		int height = mTop + mHeight;
		// 指示器的宽度
		mDicatorWidth = width / mCount;

		setMeasuredDimension(width, height);
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		Rect rect = new Rect(mLeft, mTop, mLeft + mDicatorWidth, mTop + mHeight);
		Log.e(VIEW_LOG_TAG, mDicatorWidth + "==============");
		canvas.drawRect(rect, paint);
	}

	@Override
	public void scrollTo(int position, int positionOffset) {
		mLeft = (int) ((position + positionOffset) * mDicatorWidth);
		postInvalidate();
	}

	public interface OnTextClick {
		public void textViewClick(int position);
	}

	/**
	 * 设置tab的标题内容 可选，可以自己在布局文件中写死
	 * 
	 * @param datas
	 */
	public void setTabItemTitles(List<String> datas, int windowWidth, HorizontalScrollView horizontalScrollView) {
		mWindowWidth = windowWidth;
		mHorizontalScrollView = horizontalScrollView;
		// 如果传入的list有值，则移除布局文件中设置的view
		if (datas != null && datas.size() > 0) {
			this.removeAllViews();
			this.mTabTitles = datas;
			mCount = datas.size();
			for (String title : mTabTitles) {
				// 添加view
				addView(generateTextView(title));
			}
			// 设置item的click事件
			setItemClickEvent();
		}

	}


	private ChangeSkinModel changeSkinModel;
	/**
	 * 设置tab的标题内容 可选，可以自己在布局文件中写死
	 *
	 * @param datas
	 */
	public void setTabItemTitles(List<String> datas, int windowWidth, HorizontalScrollView horizontalScrollView,ChangeSkinModel changeSkinModel) {
		this.changeSkinModel = changeSkinModel;
		mWindowWidth = windowWidth;
		mHorizontalScrollView = horizontalScrollView;
		//改变画笔颜色
		paint.setColor(changeSkinModel.getCardColor());
		// 如果传入的list有值，则移除布局文件中设置的view
		if (datas != null && datas.size() > 0) {
			this.removeAllViews();
			this.mTabTitles = datas;
			mCount = datas.size();
			for (String title : mTabTitles) {
				// 添加view
				addView(generateTextView(title));
			}
			// 设置item的click事件
			setItemClickEvent();
		}

	}

	/**
	 * 设置点击事件
	 */
	public void setItemClickEvent() {
		int cCount = getChildCount();
		for (int i = 0; i < cCount; i++) {
			final int j = i;
			View view = getChildAt(i);
			view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mViewPager.setCurrentItem(j);
				}
			});
		}
	}

	/**
	 * 根据标题生成我们的TextView
	 * 
	 * @param text
	 * @return
	 */
	private TextView generateTextView(String text) {
		TextView tv = new TextView(getContext());
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
		// lp.weight = 1;
		lp.width = mWindowWidth / 5;
		lp.setMargins(10, 0, 10, 0);
		tv.setGravity(Gravity.CENTER);
		tv.setTextColor(getResources().getColor(R.color.common_text_color));
		tv.setText(text);
		tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
		tv.setLayoutParams(lp);
		return tv;
	}

	/**
	 * 对外的ViewPager的回调接口
	 * 
	 * @author zhy
	 * 
	 */
	public interface PageChangeListener {
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

		public void onPageSelected(int position);

		public void onPageScrollStateChanged(int state);
	}

	// 对外的ViewPager的回调接口
	private PageChangeListener onPageChangeListener;

	// 对外的ViewPager的回调接口的设置
	public void setOnPageChangeListener(PageChangeListener pageChangeListener) {
		this.onPageChangeListener = pageChangeListener;
	}

	// 设置关联的ViewPager
	public void setViewPager(ViewPager mViewPager, int pos) {
		this.mViewPager = mViewPager;

		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			private int lastX;

			@Override
			public void onPageSelected(int position) {
				// 设置字体颜色高亮
				resetTextViewColor();
				highLightTextView(position);

				// 回调
				if (onPageChangeListener != null) {
					onPageChangeListener.onPageSelected(position);
				}
			}

			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
				if (!(position == 0 && positionOffset - lastX >= 0) || !(position == 1 && positionOffset - lastX <= 0)) {
					mHorizontalScrollView.scrollTo((int) ((positionOffset + position - 1) * mDicatorWidth), 0);
				}
				// 滚动
				scroll(position, positionOffset);
				// 回调
				if (onPageChangeListener != null) {
					onPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
				}
				lastX = (int) positionOffset;
			}

			@Override
			public void onPageScrollStateChanged(int state) {
				// 回调
				if (onPageChangeListener != null) {
					onPageChangeListener.onPageScrollStateChanged(state);
				}

			}
		});
		// 设置当前页
		mViewPager.setCurrentItem(pos);
		// 高亮
		highLightTextView(pos);
	}

	/**
	 * 指示器跟随手指滚动，以及容器滚动
	 * 
	 * @param position
	 * @param offset
	 */
	public void scroll(int position, float offset) {
		/**
		 * <pre>
		 *  0-1:position=0 ;1-0:postion=0;
		 * </pre>
		 */
		// 不断改变偏移量，invalidate
		mLeft = (int) ((position + offset) * mDicatorWidth);
		// 容器滚动，当移动到倒数最后一个的时候，开始滚动
		if (offset > 0 && position >= (mTabTitles.size() - 2) && getChildCount() > mTabTitles.size()) {
			if (mTabTitles.size() != 1) {
				this.scrollTo((position - (mTabTitles.size() - 2)) * mDicatorWidth + (int) (mDicatorWidth * offset), 0);
			} else
			// 为count为1时 的特殊处理
			{
				this.scrollTo(position * mDicatorWidth + (int) (mDicatorWidth * offset), 0);
			}
		}

		invalidate();
	}

	/**
	 * 高亮文本
	 * 
	 * @param position
	 */
	protected void highLightTextView(int position) {
		View view = getChildAt(position);
		if (view instanceof TextView) {
			if (changeSkinModel!=null){

				((TextView) view).setTextColor(changeSkinModel.getCardColor());
			}else {

				((TextView) view).setTextColor(getResources().getColor(R.color.skin_colorPrimary));
			}
		}

	}

	/**
	 * 重置文本颜色
	 */
	private void resetTextViewColor() {
		for (int i = 0; i < getChildCount(); i++) {
			View view = getChildAt(i);
			if (view instanceof TextView) {
				((TextView) view).setTextColor(getResources().getColor(R.color.common_text_color));
			}
		}
	}



	/**
	 * 设置高亮颜色
	 */
	public void setCurrentColor(int index) {
		View view = getChildAt(index);
		if (view instanceof TextView) {
			if (changeSkinModel!=null){

				((TextView) view).setTextColor(changeSkinModel.getCardColor());
			}else {

				((TextView) view).setTextColor(getResources().getColor(R.color.skin_colorPrimary));
			}
		}
	}
}
