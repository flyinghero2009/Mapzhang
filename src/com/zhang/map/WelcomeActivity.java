package com.zhang.map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

/**
 * ���ܣ���ӭ���棬����һЩ��������ʱ������Ϣ
 * 
 * @author κ����
 * @version 1.0
 * */
public class WelcomeActivity extends Activity {
	/** Called when the activity is first created. */
	int screenHeight,screenWidth;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);
		Animation animation = AnimationUtils.loadAnimation(this, R.anim.welcome_anim);
		ImageView view = (ImageView) findViewById(R.id.welcome_bg);
		view.setAnimation(animation);
		animation.setAnimationListener(new AnimationListener() {

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
				Intent intent = new Intent(WelcomeActivity.this,
						MainActivity.class);
				startActivity(intent);
				finish();
			}
		});
	}
}
