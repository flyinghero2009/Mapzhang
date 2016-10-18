package com.zhang.map;

import com.iflytek.voiceads.AdError;
import com.iflytek.voiceads.AdKeys;
import com.iflytek.voiceads.IFLYAdListener;
import com.iflytek.voiceads.IFLYAdSize;
import com.iflytek.voiceads.IFLYBannerAd;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;

public class AboutInfoActivity extends Activity {
	
	private LinearLayout layout;
	private LinearLayout layout_ads;
	private IFLYBannerAd bannerView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dailog);
		
		layout=(LinearLayout)findViewById(R.id.layout);
		layout.setOnClickListener(new OnClickListener() {
		
			@Override
			public void onClick(View v) {
				finish();				
			}
		});
		createBannerAd();
	}
	public void createBannerAd() {
		//此广告位为Demo专用，广告的展示不产生费用
		String adUnitId = "1BD998D9EC82EAC38390D183418D7DC4";
		//创建旗帜广告，传入广告位ID
		bannerView = IFLYBannerAd.createBannerAd(this, adUnitId);
		//设置请求的广告尺寸
		bannerView.setAdSize(IFLYAdSize.BANNER);
		//设置下载广告前，弹窗提示
		bannerView.setParameter(AdKeys.DOWNLOAD_ALERT, "true");
		
		//请求广告，添加监听器
		bannerView.loadAd(mAdListener);
		//将广告添加到布局
		layout_ads = (LinearLayout)findViewById(R.id.layout_adview1);
		layout_ads.removeAllViews();
		layout_ads.addView(bannerView);
		
	}
	IFLYAdListener mAdListener = new IFLYAdListener(){

		/**
		 * 广告请求成功
		 */
		@Override
		public void onAdReceive() {
			//展示广告
		    bannerView.showAd();
		    
		    //mTextView.setText("success");
		    Log.d("Ad_Android_Demo", "onAdReceive");
		}
		
		/**
		 * 广告请求失败
		 */
		@Override
		public void onAdFailed(AdError error) {
			//mTextView.setText("failed:"+error.getErrorCode()+","+
			//			error.getErrorDescription());
		    Log.d("Ad_Android_Demo", "onAdFailed");
		}

		/**
		 * 广告被点击
		 */
		@Override
		public void onAdClick() {
		    //mTextView.setText("ad click");
		    Log.d("Ad_Android_Demo", "onAdClick");
		}

		/**
		 * 广告被关闭
		 */
		@Override
		public void onAdClose() {
		    //mTextView.setText("ad close");
		    Log.d("Ad_Android_Demo", "onAdClose");
		}
    };
	@Override
	public boolean onTouchEvent(MotionEvent event){
		finish();
		return true;
	}
}
