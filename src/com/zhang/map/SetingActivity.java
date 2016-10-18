package com.zhang.map;

import com.baidu.mapapi.map.BaiduMap;
import com.iflytek.voiceads.AdError;
import com.iflytek.voiceads.AdKeys;
import com.iflytek.voiceads.IFLYAdListener;
import com.iflytek.voiceads.IFLYAdSize;
import com.iflytek.voiceads.IFLYBannerAd;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;

public class SetingActivity  extends Activity{
	private LinearLayout layout_ads;
	private IFLYBannerAd bannerView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.setting);
		CheckBox ishotcitybox = (CheckBox)this.findViewById(R.id.baiduHeatMap);
		CheckBox istracfficbox = (CheckBox)this.findViewById(R.id.traffice);
		CheckBox isvoiedbox = (CheckBox)this.findViewById(R.id.voiedstart);
		RadioButton modemap = (RadioButton)this.findViewById(R.id.normal);
		RadioButton sattelitemap = (RadioButton)this.findViewById(R.id.statellite);
		RadioButton normalmode = (RadioButton)this.findViewById(R.id.normalmode);
		RadioButton followmode = (RadioButton)this.findViewById(R.id.followmode);
		RadioButton compassmode = (RadioButton)this.findViewById(R.id.compassmode);
		@SuppressWarnings("deprecation")
		SharedPreferences spr = this.getSharedPreferences(CommonString.MSETTINGNAME,Context.MODE_WORLD_READABLE);
		 boolean ishotcity = false;
		 ishotcity = spr.getBoolean(CommonString.FIELD_HOT_CITY_MAP_NAME, false);
		 String loctian_mode_name = spr.getString(CommonString.FIELD_LOCATION_MODE_NAME, "");
		 if(loctian_mode_name != null&&loctian_mode_name.equals(CommonString.STORAGE_COMPASS_MODE_NAME)==true)
		 {
			 compassmode.setChecked(true);
			 
		 }else if(loctian_mode_name!=null&&loctian_mode_name.equals(CommonString.STORAGE_FOLLOW_MODE_NAME)==true)
		 {
			 followmode.setChecked(true);
		 }else
		 {
			 normalmode.setChecked(true);
		 }
		 String mode_map_name = spr.getString(CommonString.FIELD_MODE_MAP_NAME, "");
		 if(mode_map_name!=null&&mode_map_name.equals(CommonString.STORAGE_SATELLITE_MAP_NAME)==true)
		 {
			 sattelitemap.setChecked(true);
		 }else
		 {
			 modemap.setChecked(true);
			 
		 }
		 boolean isvoied_start = false;
		 isvoied_start = spr.getBoolean(CommonString.FIELD_VOIED_START, false);
		 boolean istracffic = false;
		 istracffic =  spr.getBoolean(CommonString.FIELD_TRACFFICE_MAP_NAME, false);
		 if(ishotcity == true)
		 {
			 ishotcitybox.setChecked(true);
		 }else{
			 ishotcitybox.setChecked(false);
		 }
		 if(isvoied_start == true)
		 {
			 isvoiedbox.setChecked(true);
		 }else{
			 isvoiedbox.setChecked(false);
		 }
		 if(istracffic == true)
		 {
			 istracfficbox.setChecked(true);
		 }else{
			 istracfficbox.setChecked(false);
		 }
		 createBannerAd();
	}
	
	/**
	 * 设置底图显示模式
	 * 
	 * @param view
	 */
	public void setMapMode(View view) {
		boolean checked = ((RadioButton) view).isChecked();
		SharedPreferences spr = this.getSharedPreferences(CommonString.MSETTINGNAME,Context.MODE_WORLD_READABLE);
		 SharedPreferences.Editor prf =  spr.edit();
		switch (view.getId()) {
		case R.id.normal:			 
			 prf.putString(CommonString.FIELD_MODE_MAP_NAME, CommonString.STORAGE_NORMAL_MAP_NAME);	
			 MainActivity.mModeMap = CommonString.STORAGE_NORMAL_MAP_NAME;
			break;
		case R.id.statellite:
			prf.putString(CommonString.FIELD_MODE_MAP_NAME, CommonString.STORAGE_SATELLITE_MAP_NAME);
			 MainActivity.mModeMap =  CommonString.STORAGE_SATELLITE_MAP_NAME;
			break;
		}
		prf.commit();
	}
	public void setTraffic(View view)
	{
		SharedPreferences spr = this.getSharedPreferences(CommonString.MSETTINGNAME,Context.MODE_WORLD_READABLE);
		 SharedPreferences.Editor prf =  spr.edit();
		 prf.putBoolean(CommonString.FIELD_TRACFFICE_MAP_NAME, ((CheckBox) view).isChecked());
		 MainActivity.mIstracffic =( (CheckBox) view).isChecked();
		 prf.commit();
	}
	public void setBaiduHeatMap(View view)
	{
		SharedPreferences spr = this.getSharedPreferences(CommonString.MSETTINGNAME,Context.MODE_WORLD_READABLE);
		 SharedPreferences.Editor prf =  spr.edit();
		 prf.putBoolean(CommonString.FIELD_HOT_CITY_MAP_NAME, ((CheckBox) view).isChecked());
		 MainActivity.mIsHotCity =( (CheckBox) view).isChecked();
		 prf.commit();
	}
	public void setVoiedStart(View view)
	{
		SharedPreferences spr = this.getSharedPreferences(CommonString.MSETTINGNAME,Context.MODE_WORLD_READABLE);
		 SharedPreferences.Editor prf =  spr.edit();
		 prf.putBoolean(CommonString.FIELD_VOIED_START, ((CheckBox) view).isChecked());
		 MainActivity.mIsVoiedStart =( (CheckBox) view).isChecked();
		 prf.commit();
	}
	public void setModeSeclet(View view)
	{
		boolean checked = ((RadioButton) view).isChecked();
		SharedPreferences spr = this.getSharedPreferences(CommonString.MSETTINGNAME,Context.MODE_WORLD_READABLE);
		 SharedPreferences.Editor prf =  spr.edit();
		switch (view.getId()) {
		case R.id.normalmode:			 
			 prf.putString(CommonString.FIELD_LOCATION_MODE_NAME, CommonString.STORAGE_NORMAL_MODE_NAME);	
			 MainActivity.mLocationMode = CommonString.STORAGE_NORMAL_MODE_NAME;
			break;
		case R.id.followmode:
			prf.putString(CommonString.FIELD_LOCATION_MODE_NAME, CommonString.STORAGE_FOLLOW_MODE_NAME);
			MainActivity.mLocationMode = CommonString.STORAGE_FOLLOW_MODE_NAME;
			break;
		case R.id.compassmode:
			prf.putString(CommonString.FIELD_LOCATION_MODE_NAME, CommonString.STORAGE_COMPASS_MODE_NAME);
			MainActivity.mLocationMode = CommonString.STORAGE_COMPASS_MODE_NAME;
			break;
		}
		prf.commit();
	}

	public void createBannerAd() {
		//此广告位为Demo专用，广告的展示不产生费用
		String adUnitId = "2C601ACB7E77785B323AFC25B0CC61F8";
		//创建旗帜广告，传入广告位ID
		bannerView = IFLYBannerAd.createBannerAd(this, adUnitId);
		//设置请求的广告尺寸
		bannerView.setAdSize(IFLYAdSize.BANNER);
		//设置下载广告前，弹窗提示
		bannerView.setParameter(AdKeys.DOWNLOAD_ALERT, "true");
		
		//请求广告，添加监听器
		bannerView.loadAd(mAdListener);
		//将广告添加到布局
		layout_ads = (LinearLayout)findViewById(R.id.layout_adview);
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
}
