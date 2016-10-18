package com.zhang.map;


import java.util.HashMap;
import java.util.LinkedHashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.zhang.map.util.JsonParser;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

@SuppressWarnings("unused")
public class RoutePlanActivity extends Activity implements OnClickListener{
	
	private EditText mEditTextStart;
	private EditText mEditeTextStop;
	private Button mDrive;
	private Button mTrantr;
	private Button mWalk;
	
	private static String TAG = RoutePlanActivity.class.getSimpleName();
	// 语音听写对象
	private SpeechRecognizer mIat;
	// 语音听写UI
	private RecognizerDialog mIatDialog;
	private CommonString.LocSettingEnum mLocSettingName = CommonString.LocSettingEnum.loc_fujin;
	// 用HashMap存储听写结果
	private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();
	private Toast mToast;
	private String mStartOrStop = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_routeplan);
		mEditTextStart = (EditText)this.findViewById(R.id.start);
		mEditTextStart.setOnTouchListener(new OnTouchListener()
		{

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				if(MainActivity.mIsVoiedStart)
				{
					mStartOrStop = "start";

					mIatResults.clear();
					// 设置参数
					setParam();
					if (true) {
						// 显示听写对话框
						mIatDialog.setListener(recognizerDialogListener);
						mIatDialog.show();
						showTip("请开始说话…");
					} else {
						// 不显示听写对话框
						ret = mIat.startListening(recognizerListener);
						if (ret != ErrorCode.SUCCESS) {
							showTip("听写失败,错误码：" + ret);
						} else {
							showTip("请开始说话…");
						}
					}
				return true;
				}
				else
				{
					return false;
				}
				
			}
			
		});
		mEditeTextStop = (EditText)this.findViewById(R.id.end);
		mEditeTextStop.setOnTouchListener(new OnTouchListener()
		{

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				if(MainActivity.mIsVoiedStart)
				{
					mStartOrStop = "end";

					mIatResults.clear();
					// 设置参数
					setParam();
					if (true) {
						// 显示听写对话框
						mIatDialog.setListener(recognizerDialogListener);
						mIatDialog.show();
						showTip("请开始说话…");
					} else {
						// 不显示听写对话框
						ret = mIat.startListening(recognizerListener);
						if (ret != ErrorCode.SUCCESS) {
							showTip("听写失败,错误码：" + ret);
						} else {
							showTip("请开始说话…");
						}
					}
				return true;
				}else
				{
					return false;
				}
				
			}
			
		});
		mDrive = (Button)this.findViewById(R.id.drive);
		mTrantr = (Button)this.findViewById(R.id.transit);
		mWalk = (Button)this.findViewById(R.id.walk);
		mDrive.setOnClickListener(this);
		mTrantr.setOnClickListener(this);
		mWalk.setOnClickListener(this);
		 @SuppressWarnings("deprecation")
		SharedPreferences spr = this.getSharedPreferences(CommonString.MSETTINGNAME,Context.MODE_WORLD_READABLE);
		 String startname = spr.getString(CommonString.FIELD_START_NAME, "");
		 String stopname = spr.getString(CommonString.FIELD_STOP_NAME, "");
		 if(startname!=null)
		 {
			 mEditTextStart.setText(startname);
		 }
		 if(stopname!=null)
		 {
			 mEditeTextStop.setText(stopname);
		 }
		//startActivityForResult(intent, CommonString.FILE_RESULT_CODE);
		 
		//语音初始化，在使用应用使用时需要初始化一次就好，如果没有这句会出现10111初始化失败
		 SpeechUtility.createUtility(this, "appid=552b97f1");
		// 初始化识别对象
			mIat = SpeechRecognizer.createRecognizer(this, mInitListener);
			// 初始化听写Dialog，如果只使用有UI听写功能，无需创建SpeechRecognizer
			mIatDialog = new RecognizerDialog(this, mInitListener);
			mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
	}
	int ret = 0;
	@SuppressWarnings("deprecation")
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.drive:
			if(mEditTextStart.getText().toString().equals("")==false&&mEditeTextStop.getText().toString().equals("")==false)
			{
				 SharedPreferences spr = this.getSharedPreferences(CommonString.MSETTINGNAME,Context.MODE_WORLD_READABLE);
				 SharedPreferences.Editor prf =  spr.edit();
				 prf.putString(CommonString.FIELD_START_NAME, mEditTextStart.getText().toString());
				 prf.putString(CommonString.FIELD_STOP_NAME, mEditeTextStop.getText().toString());
				 prf.commit();
				Intent data = new Intent(this,
						MainActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString(CommonString.BUNDLE_START_NAME, mEditTextStart.getText().toString());
				bundle.putString(CommonString.BUNDLE_STOP_NAME,mEditeTextStop.getText().toString());
				data.putExtras(bundle);
				setResult(1, data);
				finish();
			}
			else
			{
				
			}
			break;
		case R.id.transit:
			if(mEditTextStart.getText().toString().equals("")==false&&mEditeTextStop.getText().toString().equals("")==false)
			{
				 SharedPreferences spr = this.getSharedPreferences(CommonString.MSETTINGNAME,Context.MODE_WORLD_READABLE);
				 SharedPreferences.Editor prf =  spr.edit();
				 prf.putString(CommonString.FIELD_START_NAME, mEditTextStart.getText().toString());
				 prf.putString(CommonString.FIELD_STOP_NAME, mEditeTextStop.getText().toString());
				 prf.commit();
				Intent data = new Intent(this,
						MainActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString(CommonString.BUNDLE_START_NAME,mEditTextStart.getText().toString());
				bundle.putString(CommonString.BUNDLE_STOP_NAME,mEditeTextStop.getText().toString());
				data.putExtras(bundle);
				setResult(2, data);
				finish();
			}else
			{
				
			}
			break;
		case R.id.walk:
			if(mEditTextStart.getText().toString().equals("")==false&&mEditeTextStop.getText().toString().equals("")==false)
			{
				 SharedPreferences spr = this.getSharedPreferences(CommonString.MSETTINGNAME,Context.MODE_WORLD_READABLE);
				 SharedPreferences.Editor prf =  spr.edit();
				 prf.putString(CommonString.FIELD_START_NAME, mEditTextStart.getText().toString());
				 prf.putString(CommonString.FIELD_STOP_NAME, mEditeTextStop.getText().toString());
				 prf.commit();
				Intent data = new Intent(this,
						MainActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString(CommonString.BUNDLE_START_NAME, mEditTextStart.getText().toString());
				bundle.putString(CommonString.BUNDLE_STOP_NAME,mEditeTextStop.getText().toString());
				data.putExtras(bundle);
				setResult(3, data);
				finish();
			}else
			{
				
			}
			break;
			default:
				break;
		}
	}
	
	private void printResult(RecognizerResult results,String ss) {
		String text = JsonParser.parseIatResult(results.getResultString());

		String sn = null;
		// 读取json结果中的sn字段
		try {
			JSONObject resultJson = new JSONObject(results.getResultString());
			sn = resultJson.optString("sn");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		mIatResults.put(sn, text);

		StringBuffer resultBuffer = new StringBuffer();
		for (String key : mIatResults.keySet()) {
			resultBuffer.append(mIatResults.get(key));
		}
		if(ss.equals("start"))
		{
			mEditTextStart.setText(resultBuffer.toString());
		}
		else
		{
			mEditeTextStop.setText(resultBuffer.toString());
		}
		///////mResultText.setText(resultBuffer.toString());
		///////mResultText.setSelection(mResultText.length());
	}
	/**
	 * 参数设置
	 * 
	 * @param param
	 * @return
	 */
	public void setParam() {
		// 清空参数
		mIat.setParameter(SpeechConstant.PARAMS, null);

		// 设置听写引擎
		mIat.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
		// 设置返回结果格式
		mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");

		String lag = "zh_cn";//mSharedPreferences.getString("iat_language_preference","mandarin");
		if (lag.equals("en_us")) {
			// 设置语言
			mIat.setParameter(SpeechConstant.LANGUAGE, "en_us");
		} else {
			// 设置语言
			mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
			// 设置语言区域
			mIat.setParameter(SpeechConstant.ACCENT, lag);
		}
		// 设置语音前端点
		mIat.setParameter(SpeechConstant.VAD_BOS, "4000");
		// 设置语音后端点
		mIat.setParameter(SpeechConstant.VAD_EOS, "1800");
		// 设置标点符号
		mIat.setParameter(SpeechConstant.ASR_PTT, "0");
		// 设置音频保存路径
		mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory()
				+ "/zhang/wavaudio.pcm");
		// 设置听写结果是否结果动态修正，为“1”则在听写过程中动态递增地返回结果，否则只在听写结束之后返回最终结果
		// 注：该参数暂时只对在线听写有效
		mIat.setParameter(SpeechConstant.ASR_DWA, "0");
	}
	private void showTip(final String str) {
		mToast.setText(str);
		mToast.show();
	}
	/**
	 * 听写UI监听器
	 */
	private RecognizerDialogListener recognizerDialogListener = new RecognizerDialogListener() {

		public void onResult(RecognizerResult results, boolean isLast) {
			printResult(results,mStartOrStop);
		}

		/**
		 * 识别回调错误.
		 */
		public void onError(SpeechError error) {
			showTip(error.getPlainDescription(true));
		}

	};
	
	
	/**
	 * 听写监听器。
	 */
	private RecognizerListener recognizerListener = new RecognizerListener() {


		@Override
		public void onBeginOfSpeech() {
			showTip("开始说话");
		}

		@Override
		public void onError(SpeechError error) {
			// Tips：
			// 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
			// 如果使用本地功能（语音+）需要提示用户开启语音+的录音权限。
			showTip(error.getPlainDescription(true));
		}

		@Override
		public void onEndOfSpeech() {
			showTip("结束说话");
		}

		@Override
		public void onResult(RecognizerResult results, boolean isLast) {
			Log.d(TAG, results.getResultString());
			printResult(results,mStartOrStop);

			if (isLast) {
				// TODO 最后的结果
			}
		}

		@Override
		public void onVolumeChanged(int volume) {
			showTip("当前正在说话，音量大小：" + volume);
		}

		@Override
		public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
		}
	};
	/**
	 * 初始化监听器。
	 */
	private InitListener mInitListener = new InitListener() {

		@Override
		public void onInit(int code) {
			Log.d(TAG, "SpeechRecognizer init() code = " + code);
			if (code != ErrorCode.SUCCESS) {
				showTip("初始化失败，错误码：" + code);
			}
		}
	};
}
