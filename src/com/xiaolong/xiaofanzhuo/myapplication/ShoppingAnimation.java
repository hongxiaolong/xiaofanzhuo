package com.xiaolong.xiaofanzhuo.myapplication;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ShoppingAnimation {
	
	private Context mContext;
	private ImageView imgIcon;
	private TextView tvNumber;
	private ViewGroup anim_mask_layout;
	
	public ShoppingAnimation(Context context, ImageView imageView, TextView textView) {
		mContext = context;
		imgIcon = imageView;
		tvNumber = textView;
	}
	
	/**
	 * @Description: 创建动画层
	 * @param
	 * @return void
	 * @throws
	 */
	@SuppressWarnings("deprecation")
	public void createAnimLayout() {
		ViewGroup rootView = (ViewGroup) ((Activity) mContext).getWindow().getDecorView();
		LinearLayout animLayout = new LinearLayout(mContext);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.FILL_PARENT);
		animLayout.setLayoutParams(lp);
		animLayout.setBackgroundResource(android.R.color.transparent);
		rootView.addView(animLayout);
		this.anim_mask_layout = animLayout;
	}

	/**
	 * @Description: 添加视图到动画层
	 * @param @param vg
	 * @param @param view
	 * @param @param location
	 * @param @return
	 * @return View
	 * @throws
	 */
	private View addViewToAnimLayout(final ViewGroup vg, final View view,
			int[] location) {
		int x = location[0];
		int y = location[1];
		vg.addView(view);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		lp.leftMargin = x;
		lp.topMargin = y;
		view.setLayoutParams(lp);
		return view;
	}

	public void setAnim(View v) {

		Animation mScaleAnimation = new ScaleAnimation(1.5f, 0.1f, 1.5f, 0.1f,
				Animation.RELATIVE_TO_SELF, 0.1f, Animation.RELATIVE_TO_SELF,
				0.1f);
		mScaleAnimation.setDuration(AnimationDuration);
		mScaleAnimation.setFillAfter(true);

		int[] start_location = new int[2];
		imgIcon.getLocationInWindow(start_location);
		ViewGroup vg = (ViewGroup) imgIcon.getParent();
		vg.removeView(imgIcon);
		// 将组件添加到我们的动画层上
		final View view = addViewToAnimLayout(anim_mask_layout, imgIcon,
				start_location);
		int[] end_location = new int[2];
		tvNumber.getLocationInWindow(end_location);
		// 计算位移
		int endX = end_location[0];
		int endY = end_location[1] - start_location[1];

		Animation mTranslateAnimation = new TranslateAnimation(0, endX, 0, endY);// 移动
		mTranslateAnimation.setDuration(AnimationDuration);

		AnimationSet mAnimationSet = new AnimationSet(false);
		// 这块要注意，必须设为false,不然组件动画结束后，不会归位。
		mAnimationSet.setFillAfter(false);
		mAnimationSet.addAnimation(mScaleAnimation);
		mAnimationSet.addAnimation(mTranslateAnimation);
		view.startAnimation(mAnimationSet);

		mTranslateAnimation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				tvNumber.setText(goodsNumber + "");
				view.clearAnimation();
			}
		});
	}
	
	public void setGoodsNumber(boolean flag)
	{
		if (flag)
			++goodsNumber;
		else 
			--goodsNumber;
	}

	/**
	 * 动画播放时间
	 */
	private int AnimationDuration = 3000;

	private int goodsNumber = 0;
}
