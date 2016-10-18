package com.zhang.map;

import java.util.HashMap;
import java.util.LinkedHashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.baidu.mapapi.overlayutil.OverlayManager;
import com.baidu.mapapi.overlayutil.PoiOverlay;
import com.baidu.mapapi.overlayutil.TransitRouteOverlay;
import com.baidu.mapapi.overlayutil.WalkingRouteOverlay;
import com.baidu.mapapi.search.core.CityInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteLine;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteLine;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
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

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

@SuppressWarnings("unused")
public class MainActivity extends Activity implements OnClickListener,
		BaiduMap.OnMapClickListener, OnGetRoutePlanResultListener,
		OnGetGeoCoderResultListener, OnGetPoiSearchResultListener,
		OnGetSuggestionResultListener {

	// 定位相关
	LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();
	private LocationMode mCurrentMode;
	BitmapDescriptor mCurrentMarker;
	RadioButton requestLocButton;
	boolean isFirstLoc = true;// 是否首次定位
	boolean isNormol = true;// 是否是一般模式
	boolean isFuJin = true;
	/**
	 * MapView 是地图主控件
	 */
	private MapView mMapView;
	private BaiduMap mBaiduMap;
	private static final String LTAG = MainActivity.class.getSimpleName();

	private RadioButton radio_btn_changemode;// 改变模式的radio_btn_changemode
	private RadioButton radio_btn_refresh;// 更新位置的radio_btn_refresh
	private RadioButton radio_btn_more;// 更新位置的radio_btn_refresh
	private RadioButton radio_btn_fujin;// 更新位置的radio_btn_refresh
	private RadioButton radio_btn_search_bus;// 公交路线
	private String starname = "";
	private String stopname = "";
	private int mIndex = 0;
	private String  mCityName = "";
	private String mAdressName = "";

	private GridView gv;
	private PopupWindow mPop;
	private View layout;
	private final int[] icons = { R.drawable.icon_class_ktv,
			R.drawable.icon_class_hotel, R.drawable.icon_class_meishi,
			R.drawable.icon_class_petrolstation,
			R.drawable.icon_class_supermarket, R.drawable.icon_company,
			R.drawable.icon_class_viewspot,
			R.drawable.icon_class_xiaochikuaican,
			R.drawable.icon_class_xingjijiudian, R.drawable.icon_class_bath,
			R.drawable.icon_class_atm, R.drawable.icon_class_bank };
	private final String[] items = { "KTV", "宾馆", "美食", "加油站", "超市", "公司", "景点",
			"快餐", "酒店", "洗浴", "取款机", "银行" };
	
	// 再按一次返回键退出程序”的实现
	private long exitTime = 0;
	
	
	// 浏览路线节点相关
	Button mBtnPre = null;// 上一个节点
	Button mBtnNext = null;// 下一个节点
	int nodeIndex = -1;// 节点索引,供浏览节点时使用
	@SuppressWarnings("rawtypes")
	RouteLine route = null;
	OverlayManager routeOverlay = null;
	boolean useDefaultIcon = false;
	private TextView popupText = null;// 泡泡view
	RoutePlanSearch mSearch = null; // 搜索模块，也可去掉地图模块独立使用
	GeoCoder mGeoCoderSearch = null; // 搜索模块，也可去掉地图模块独立使用

	private PoiSearch mPoiSearch = null;
	private SuggestionSearch mSuggestionSearch = null;
	
	
	public static String mModeMap = "";
	public static String mLocationMode="";
	public static boolean mIsHotCity = false;
	public static boolean mIstracffic = false;
	public static boolean mIsVoiedStart = false;
	
	private static String TAG = MainActivity.class.getSimpleName();
	// 语音听写对象
	private SpeechRecognizer mIat;
	// 语音听写UI
	private RecognizerDialog mIatDialog;
	private CommonString.LocSettingEnum mLocSettingName = CommonString.LocSettingEnum.loc_fujin;
	// 用HashMap存储听写结果
	private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();
	private Toast mToast;

	// private ArrayAdapter<String> sugAdapter = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.activity_main);
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		initCenter();
		initLocation();
		initRoutePlanSearch();
		initOnClicked();
		// 初始化搜索模块，注册事件监听
		mGeoCoderSearch = GeoCoder.newInstance();
		mGeoCoderSearch.setOnGetGeoCodeResultListener(this);
		mPoiSearch = PoiSearch.newInstance();
		mPoiSearch.setOnGetPoiSearchResultListener(this);
		mSuggestionSearch = SuggestionSearch.newInstance();
		mSuggestionSearch.setOnGetSuggestionResultListener(this);

		SharedPreferences spr = this.getSharedPreferences(CommonString.MSETTINGNAME,Context.MODE_WORLD_READABLE);
		mIsHotCity = spr.getBoolean(CommonString.FIELD_HOT_CITY_MAP_NAME, false);
		 mBaiduMap.setBaiduHeatMapEnabled(mIsHotCity);
		 String loctian_mode_name = spr.getString(CommonString.FIELD_LOCATION_MODE_NAME, "");
		 if(loctian_mode_name != null&&loctian_mode_name.equals(CommonString.STORAGE_COMPASS_MODE_NAME)==true)
		 {
			 mCurrentMode = LocationMode.COMPASS;
			 mBaiduMap
				.setMyLocationConfigeration(new MyLocationConfiguration(
						mCurrentMode, true, mCurrentMarker));
			 mLocationMode = CommonString.STORAGE_COMPASS_MODE_NAME;
			 
		 }else if(loctian_mode_name != null&&loctian_mode_name.equals(CommonString.STORAGE_FOLLOW_MODE_NAME)==true)
		 {
			 mCurrentMode = LocationMode.FOLLOWING;
			 mBaiduMap
				.setMyLocationConfigeration(new MyLocationConfiguration(
						mCurrentMode, true, mCurrentMarker));
			 mLocationMode = CommonString.STORAGE_FOLLOW_MODE_NAME;
		 }else{
			 mCurrentMode = LocationMode.NORMAL;
			 mBaiduMap
				.setMyLocationConfigeration(new MyLocationConfiguration(
						mCurrentMode, true, mCurrentMarker));
			 mLocationMode = CommonString.STORAGE_NORMAL_MODE_NAME;
		 }
		 String mode_map_name = spr.getString(CommonString.FIELD_MODE_MAP_NAME, "");
		 if(mode_map_name!=null&&mode_map_name.equals(CommonString.STORAGE_SATELLITE_MAP_NAME)==true)
		 {
			 mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
			 this.mModeMap = CommonString.STORAGE_SATELLITE_MAP_NAME;
			
		 }else
		 {
			 mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
			 this.mModeMap = CommonString.STORAGE_NORMAL_MAP_NAME;
		 }
		 this.mIsVoiedStart = spr.getBoolean(CommonString.FIELD_VOIED_START, false);
		 
		 this.mIstracffic=  spr.getBoolean(CommonString.FIELD_TRACFFICE_MAP_NAME, false);
		 mBaiduMap.setTrafficEnabled(mIstracffic);
		
		
		 /*
		 033.
		 在使用语音平台上传应用包的时候会自定生成一个appid   应该使用与包相对应的appid在申请提交后没有使用次数的限制
		 034.
		  
		 035.
		 */
		 //语音初始化，在使用应用使用时需要初始化一次就好，如果没有这句会出现10111初始化失败
		 SpeechUtility.createUtility(MainActivity.this, "appid=552b97f1");
		// 初始化识别对象
			mIat = SpeechRecognizer.createRecognizer(this, mInitListener);
			// 初始化听写Dialog，如果只使用有UI听写功能，无需创建SpeechRecognizer
			mIatDialog = new RecognizerDialog(this, mInitListener);
			mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
		
		// 注册 SDK 广播监听者
		IntentFilter iFilter = new IntentFilter();
		iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
		iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
		mReceiver = new SDKReceiver();
		registerReceiver(mReceiver, iFilter);
	}

	private void initCenter() {
		// 设定中心点坐标 中国宜春
		LatLng cenpt = new LatLng(27.8149, 114.41666);
		// 定义地图状态
		MapStatus mMapStatus = new MapStatus.Builder().target(cenpt).zoom(12)
				.build();
		// 定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
		MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory
				.newMapStatus(mMapStatus);
		// 改变地图状态
		mBaiduMap.setMapStatus(mMapStatusUpdate);
	}

	private void initLocation() {
		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);
		requestLocButton = (RadioButton) findViewById(R.id.radio_btn_changemode);
		mCurrentMode = LocationMode.NORMAL;
		// 定位初始化
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000);
		mLocClient.setLocOption(option);
		mLocClient.start();
		//new Thread(new LocationStartThread()).start();
		
	}
	private void initRoutePlanSearch() {
		mBtnPre = (Button) findViewById(R.id.pre);
		mBtnNext = (Button) findViewById(R.id.next);
		// 地图点击事件处理
		mBaiduMap.setOnMapClickListener(this);
		// 初始化搜索模块，注册事件监听
		mSearch = RoutePlanSearch.newInstance();
		mSearch.setOnGetRoutePlanResultListener(this);
	}

	private void initOnClicked() {
		radio_btn_changemode = (RadioButton) findViewById(R.id.radio_btn_changemode);
		radio_btn_refresh = (RadioButton) findViewById(R.id.radio_btn_refresh);
		radio_btn_more = (RadioButton) findViewById(R.id.radio_btn_more);
		radio_btn_fujin = (RadioButton) findViewById(R.id.radio_btn_fujin);
		radio_btn_search_bus = (RadioButton) findViewById(R.id.radio_btn_search_bus);
		radio_btn_changemode.setOnClickListener(this);
		radio_btn_search_bus.setOnClickListener(this);
		radio_btn_refresh.setOnClickListener(this);
		radio_btn_more.setOnClickListener(this);
		radio_btn_fujin.setOnClickListener(this);
		layout = View.inflate(this, R.layout.window, null);
		gv = (GridView) layout.findViewById(R.id.gridview);
		MyAdapter adapter = new MyAdapter(this, items, icons);
		gv.setAdapter(adapter);
		gv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long id) {
				// 每个item点击进行搜索
				// mkSearch.poiSearchNearBy(items[position], pt1, 5000);
				String address =items[position];
				
				///该城市查询
				//mPoiSearch.searchInCity((new PoiCitySearchOption()).city(mCityName).keyword(address).pageNum(0));
				
				///定位之后查询附近的地点
				mPoiSearch.searchNearby((new PoiNearbySearchOption()).location(new LatLng(
							mBaiduMap.getLocationData().latitude,
							mBaiduMap.getLocationData().longitude)).keyword(address).pageNum(0).radius(2000));
				//mSuggestionSearch.requestSuggestion((new SuggestionSearchOption()).keyword(items[position]).city(mCityName));
				
				if (mPop.isShowing()) {
					mPop.dismiss();
				}
			}
		});
	}

	@Override
	protected void onResume() {
		mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// 退出时销毁定位
		mLocClient.stop();
		// 关闭定位图层
		mBaiduMap.setMyLocationEnabled(false);
		mMapView.onDestroy();
		super.onDestroy();
		// 取消监听 SDK 广播
		unregisterReceiver(mReceiver);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if (System.currentTimeMillis() - exitTime > 1500) {
				Toast.makeText(this, "再按一次返回键将退出ZhangMap", Toast.LENGTH_SHORT)
						.show();
				exitTime = System.currentTimeMillis();
			} else {
				finish();
				android.os.Process.killProcess(android.os.Process.myPid());
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CommonString.RESULT_MAIN_CODE) {
			Bundle bundle = null;
			if (data != null && (bundle = data.getExtras()) != null) {
				switch (resultCode) {
				case 1:
					mIndex = 1;
					starname = bundle.getString(CommonString.BUNDLE_START_NAME);
					stopname = bundle.getString(CommonString.BUNDLE_STOP_NAME);
					break;
				case 2:
					mIndex = 2;
					starname = bundle.getString(CommonString.BUNDLE_START_NAME);
					stopname = bundle.getString(CommonString.BUNDLE_STOP_NAME);

					break;
				case 3:
					mIndex = 3;
					starname = bundle.getString(CommonString.BUNDLE_START_NAME);
					stopname = bundle.getString(CommonString.BUNDLE_STOP_NAME);
					break;
				default:
					break;
				}
				try {
					LatLng ptCenter = new LatLng(
							mBaiduMap.getLocationData().latitude,
							mBaiduMap.getLocationData().longitude);
					// 反Geo搜索
					mGeoCoderSearch.reverseGeoCode(new ReverseGeoCodeOption()
							.location(ptCenter));
				} catch (Exception e) {
					Toast.makeText(this, "抱歉，查找出错", Toast.LENGTH_SHORT).show();
				}

			}
		}else if(requestCode == CommonString.RESULT_SEETING_CODE)
		{
			mBaiduMap.setTrafficEnabled(mIstracffic);
			 mBaiduMap.setBaiduHeatMapEnabled(mIsHotCity);
			 if(mModeMap.equals(CommonString.STORAGE_NORMAL_MAP_NAME)==true)
			 {
				 mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
			 }else
			 {
				 mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
			 }
			 if(mLocationMode.equals(CommonString.STORAGE_NORMAL_MODE_NAME)==true)
			 {
				 mCurrentMode = LocationMode.NORMAL;
				 mBaiduMap
					.setMyLocationConfigeration(new MyLocationConfiguration(
							mCurrentMode, true, mCurrentMarker));
				 
			 }else if(mLocationMode.equals(CommonString.STORAGE_FOLLOW_MODE_NAME)==true)
			 {
				 mCurrentMode = LocationMode.FOLLOWING;
				 mBaiduMap
					.setMyLocationConfigeration(new MyLocationConfiguration(
							mCurrentMode, true, mCurrentMarker));
			 }else if(mLocationMode.equals(CommonString.STORAGE_COMPASS_MODE_NAME)==true)
			 {
				 mCurrentMode = LocationMode.COMPASS;
				 mBaiduMap
					.setMyLocationConfigeration(new MyLocationConfiguration(
							mCurrentMode, true, mCurrentMarker));
			 }
		}
	}
	int ret = 0; // 函数调用返回值
	@Override
	public void onClick(View v) {
		// mMapView.setSatellite(true);//设置卫星地图
		// mMapView.setTraffic(true); 设置普通地图
		switch (v.getId()) {
		case R.id.radio_btn_changemode: // 改变模式的点击事件
			Intent intent2 = new Intent();
			intent2.setClass(MainActivity.this, SetingActivity.class);
			startActivityForResult(intent2, CommonString.RESULT_SEETING_CODE);
			mLocSettingName = CommonString.LocSettingEnum.loc_setting;
			/*switch (mCurrentMode) {
			case NORMAL:
				requestLocButton.setText("跟随");
				mCurrentMode = LocationMode.FOLLOWING;
				mBaiduMap
						.setMyLocationConfigeration(new MyLocationConfiguration(
								mCurrentMode, true, mCurrentMarker));
				break;
			case COMPASS:
				requestLocButton.setText("普通");
				mCurrentMode = LocationMode.NORMAL;
				mBaiduMap
						.setMyLocationConfigeration(new MyLocationConfiguration(
								mCurrentMode, true, mCurrentMarker));
				break;
			case FOLLOWING:
				requestLocButton.setText("罗盘");
				mCurrentMode = LocationMode.COMPASS;
				mBaiduMap
						.setMyLocationConfigeration(new MyLocationConfiguration(
								mCurrentMode, true, mCurrentMarker));
				break;
			
			}
		*/
			break;
		case R.id.radio_btn_refresh: // 更新位置的点击事件
			isFirstLoc = true;
			mLocSettingName = CommonString.LocSettingEnum.loc_location;
			// mkSearch.poiSearchInbounds(content, pt1, pt1);//全国信息查询
			break;
		case R.id.radio_btn_more: // 关于
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, AboutInfoActivity.class);
			startActivity(intent);
			mLocSettingName = CommonString.LocSettingEnum.loc_about;
			break;
		case R.id.radio_btn_fujin: // 附近
			mLocSettingName = CommonString.LocSettingEnum.loc_fujin;
			if(mIsVoiedStart)
			{
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
			}else
			{
				initPopWindow();
				// mPop.showAsDropDown(v,20,-284);//以这个Button为anchor（可以理解为锚，基准），在下方弹出
				mPop.showAtLocation(this.findViewById(R.id.rl), Gravity.CENTER, 0,0);// 在屏幕居中，无偏移
			}			
			break;
		case R.id.radio_btn_search_bus: // 公交路线(底部导航的)
			Intent intent1 = new Intent();
			intent1.setClass(this, RoutePlanActivity.class);
			startActivityForResult(intent1, CommonString.RESULT_MAIN_CODE);
			break;
		}
		mBaiduMap.clear();
	}

	/**
	 * 设置底图显示模式
	 * 
	 * @param view
	 */
	public void setMapMode(View view) {
		boolean checked = ((RadioButton) view).isChecked();
		switch (view.getId()) {
		case R.id.normal:
			if (checked)
				mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
			isNormol = true;
			break;
		case R.id.statellite:
			if (checked)
				mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
			isNormol = false;
			break;
		}
	}

	/**
	 * 设置是否显示交通图
	 * 
	 * @param view
	 */
	public void setTraffic(View view) {
		mBaiduMap.setTrafficEnabled(((CheckBox) view).isChecked());
	}

	/**
	 * 设置是否显示百度热力图
	 * 
	 * @param view
	 */
	public void setBaiduHeatMap(View view) {
		mBaiduMap.setBaiduHeatMapEnabled(((CheckBox) view).isChecked());
	}

	@Override
	protected void onPause() {
		// MapView的生命周期与Activity同步，当activity挂起时需调用MapView.onPause()
		mMapView.onPause();
		super.onPause();
	}

	/**
	 * 构造广播监听类，监听 SDK key 验证以及网络异常广播
	 */
	public class SDKReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			String s = intent.getAction();
			Log.d(LTAG, "action: " + s);
			if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
				Toast.makeText(context,
						"key 验证出错! 请检查网络设置",
						Toast.LENGTH_SHORT).show();

			} else if (s
					.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
				Toast.makeText(context, "网络出错", Toast.LENGTH_SHORT).show();
			}
		}
	}

	private SDKReceiver mReceiver;

	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null || mMapView == null)
				return;
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(100).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			if (isFirstLoc) {
				isFirstLoc = false;
				LatLng ll = new LatLng(location.getLatitude(),
						location.getLongitude());
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				mBaiduMap.animateMapStatus(u);
				try{
					LatLng ptCenter = new LatLng(
							mBaiduMap.getLocationData().latitude,
							mBaiduMap.getLocationData().longitude);
					// 反Geo搜索
					mGeoCoderSearch.reverseGeoCode(new ReverseGeoCodeOption()
							.location(ptCenter));
				} catch (Exception e) {
					//Toast.makeText(this, "抱歉，查找出错", Toast.LENGTH_SHORT).show();
				}
			}
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}

	/* 初始化一个弹窗 */
	@SuppressWarnings("deprecation")
	private void initPopWindow() {
		if (mPop == null) {
			mPop = new PopupWindow(layout, LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			mPop.setBackgroundDrawable(new BitmapDrawable());
			mPop.setOutsideTouchable(true);
			mPop.setFocusable(true);
		}
		if (mPop.isShowing()) {
			mPop.dismiss();
		}
	}

	// *****************************************************************************
	// ******************************************路线选择**************************
	// *****************************************************************************

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	public void onGetWalkingRouteResult(WalkingRouteResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
		}
		if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
			// 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
			// result.getSuggestAddrInfo()
			return;
		}
		if (result.error == SearchResult.ERRORNO.NO_ERROR) {
			nodeIndex = -1;
			mBtnPre.setVisibility(View.VISIBLE);
			mBtnNext.setVisibility(View.VISIBLE);
			route = result.getRouteLines().get(0);
			WalkingRouteOverlay overlay = new MyWalkingRouteOverlay(mBaiduMap);
			mBaiduMap.setOnMarkerClickListener(overlay);
			routeOverlay = overlay;
			overlay.setData(result.getRouteLines().get(0));
			overlay.addToMap();
			overlay.zoomToSpan();
		}

	}

	@Override
	public void onGetTransitRouteResult(TransitRouteResult result) {

		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
		}
		if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
			// 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
			// result.getSuggestAddrInfo()
			return;
		}
		if (result.error == SearchResult.ERRORNO.NO_ERROR) {
			nodeIndex = -1;
			mBtnPre.setVisibility(View.VISIBLE);
			mBtnNext.setVisibility(View.VISIBLE);
			route = result.getRouteLines().get(0);
			TransitRouteOverlay overlay = new MyTransitRouteOverlay(mBaiduMap);
			mBaiduMap.setOnMarkerClickListener(overlay);
			routeOverlay = overlay;
			overlay.setData(result.getRouteLines().get(0));
			overlay.addToMap();
			overlay.zoomToSpan();
		}
	}

	@Override
	public void onGetDrivingRouteResult(DrivingRouteResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
		}
		if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
			// 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
			// result.getSuggestAddrInfo()
			return;
		}
		if (result.error == SearchResult.ERRORNO.NO_ERROR) {
			nodeIndex = -1;
			mBtnPre.setVisibility(View.VISIBLE);
			mBtnNext.setVisibility(View.VISIBLE);
			route = result.getRouteLines().get(0);
			DrivingRouteOverlay overlay = new MyDrivingRouteOverlay(mBaiduMap);
			routeOverlay = overlay;
			mBaiduMap.setOnMarkerClickListener(overlay);
			overlay.setData(result.getRouteLines().get(0));
			overlay.addToMap();
			overlay.zoomToSpan();
		}
	}

	// 定制RouteOverly
	private class MyDrivingRouteOverlay extends DrivingRouteOverlay {

		public MyDrivingRouteOverlay(BaiduMap baiduMap) {
			super(baiduMap);
		}

		@Override
		public BitmapDescriptor getStartMarker() {
			if (useDefaultIcon) {
				return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
			}
			return null;
		}

		@Override
		public BitmapDescriptor getTerminalMarker() {
			if (useDefaultIcon) {
				return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
			}
			return null;
		}
	}

	private class MyWalkingRouteOverlay extends WalkingRouteOverlay {

		public MyWalkingRouteOverlay(BaiduMap baiduMap) {
			super(baiduMap);
		}

		@Override
		public BitmapDescriptor getStartMarker() {
			if (useDefaultIcon) {
				return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
			}
			return null;
		}

		@Override
		public BitmapDescriptor getTerminalMarker() {
			if (useDefaultIcon) {
				return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
			}
			return null;
		}
	}

	private class MyTransitRouteOverlay extends TransitRouteOverlay {

		public MyTransitRouteOverlay(BaiduMap baiduMap) {
			super(baiduMap);
		}

		@Override
		public BitmapDescriptor getStartMarker() {
			if (useDefaultIcon) {
				return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
			}
			return null;
		}

		@Override
		public BitmapDescriptor getTerminalMarker() {
			if (useDefaultIcon) {
				return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
			}
			return null;
		}
	}

	@Override
	public void onMapClick(LatLng point) {
		mBaiduMap.hideInfoWindow();
	}

	@Override
	public boolean onMapPoiClick(MapPoi poi) {
		return false;
	}

	/**
	 * 发起路线规划搜索示例
	 * 
	 * @param v
	 */
	public void SearchButtonProcess(View v) {
		// 重置浏览节点的路线数据
		route = null;
		mBtnPre.setVisibility(View.INVISIBLE);
		mBtnNext.setVisibility(View.INVISIBLE);
		mBaiduMap.clear();
		// 处理搜索按钮响应
		EditText editSt = (EditText) findViewById(R.id.start);
		EditText editEn = (EditText) findViewById(R.id.end);
		// 设置起终点信息，对于tranist search 来说，城市名无意义
		PlanNode stNode = PlanNode.withCityNameAndPlaceName("深圳", editSt
				.getText().toString());
		PlanNode enNode = PlanNode.withCityNameAndPlaceName("深圳", editEn
				.getText().toString());

		// 实际使用中请对起点终点城市进行正确的设定
		if (v.getId() == R.id.drive) {
			mSearch.drivingSearch((new DrivingRoutePlanOption()).from(stNode)
					.to(enNode));
		} else if (v.getId() == R.id.transit) {
			mSearch.transitSearch((new TransitRoutePlanOption()).from(stNode)
					.city("北京").to(enNode));
		} else if (v.getId() == R.id.walk) {
			mSearch.walkingSearch((new WalkingRoutePlanOption()).from(stNode)
					.to(enNode));
		}
	}

	/**
	 * 节点浏览示例
	 * 
	 * @param v
	 */
	public void nodeClick(View v) {
		if (route == null || route.getAllStep() == null) {
			return;
		}
		if (nodeIndex == -1 && v.getId() == R.id.pre) {
			return;
		}
		// 设置节点索引
		if (v.getId() == R.id.next) {
			if (nodeIndex < route.getAllStep().size() - 1) {
				nodeIndex++;
			} else {
				return;
			}
		} else if (v.getId() == R.id.pre) {
			if (nodeIndex > 0) {
				nodeIndex--;
			} else {
				return;
			}
		}
		// 获取节结果信息
		LatLng nodeLocation = null;
		String nodeTitle = null;
		Object step = route.getAllStep().get(nodeIndex);
		if (step instanceof DrivingRouteLine.DrivingStep) {
			nodeLocation = ((DrivingRouteLine.DrivingStep) step).getEntrace()
					.getLocation();
			nodeTitle = ((DrivingRouteLine.DrivingStep) step).getInstructions();
		} else if (step instanceof WalkingRouteLine.WalkingStep) {
			nodeLocation = ((WalkingRouteLine.WalkingStep) step).getEntrace()
					.getLocation();
			nodeTitle = ((WalkingRouteLine.WalkingStep) step).getInstructions();
		} else if (step instanceof TransitRouteLine.TransitStep) {
			nodeLocation = ((TransitRouteLine.TransitStep) step).getEntrace()
					.getLocation();
			nodeTitle = ((TransitRouteLine.TransitStep) step).getInstructions();
		}

		if (nodeLocation == null || nodeTitle == null) {
			return;
		}
		// 移动节点至中心
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(nodeLocation));
		// show popup
		popupText = new TextView(this);
		// popupText.setBackgroundResource(R.drawable.popup);
		popupText.setTextColor(0xFF000000);
		popupText.setText(nodeTitle);
		mBaiduMap.showInfoWindow(new InfoWindow(popupText, nodeLocation, 0));

	}

	/**
	 * 切换路线图标，刷新地图使其生效 注意： 起终点图标使用中心对齐.
	 */
	public void changeRouteIcon(View v) {
		if (routeOverlay == null) {
			return;
		}
		if (useDefaultIcon) {
			((Button) v).setText("自定义起终点图标");
			Toast.makeText(this, "将使用系统起终点图标", Toast.LENGTH_SHORT).show();

		} else {
			((Button) v).setText("系统起终点图标");
			Toast.makeText(this, "将使用自定义起终点图标", Toast.LENGTH_SHORT).show();

		}
		useDefaultIcon = !useDefaultIcon;
		routeOverlay.removeFromMap();
		routeOverlay.addToMap();
	}

	// ********************************************************************************
	// ********************************************************************************
	// ********************************************************************************

	// ********************************************************************************
	// *************************************编码查询**********************************
	// ********************************************************************************
	@Override
	public void onGetGeoCodeResult(GeoCodeResult result) {
		// TODO Auto-generated method stub

		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(this, "抱歉，未能找到结果", Toast.LENGTH_LONG).show();
			return;
		}
		mBaiduMap.clear();
		mBaiduMap.addOverlay(new MarkerOptions().position(result.getLocation())
				.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.icon_marka)));
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result
				.getLocation()));
		String strInfo = String.format("纬度：%f 经度：%f",
				result.getLocation().latitude, result.getLocation().longitude);
		Toast.makeText(this, strInfo, Toast.LENGTH_LONG).show();

	}

	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
		// TODO Auto-generated method stub
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(this, "抱歉，未能找到结果", Toast.LENGTH_LONG).show();
			return;
		}
		String address1 = result.getAddress();
		mAdressName = address1;
		String address = result.getAddressDetail().city.toString();
		mCityName = address;
		double la = result.getLocation().latitude;
		double lon = result.getLocation().longitude;
		PlanNode stNode = null;
		PlanNode enNode = null;
		switch (mIndex) {
		case 1:
			// 设置起终点信息，对于tranist search 来说，城市名无意义
			stNode = PlanNode.withCityNameAndPlaceName(address, starname);
			enNode = PlanNode.withCityNameAndPlaceName(address, stopname);
			try {
				mSearch.drivingSearch((new DrivingRoutePlanOption()).from(
						stNode).to(enNode));
			} catch (Exception e) {
				Toast.makeText(this, "抱歉，查找出错", Toast.LENGTH_SHORT).show();
			}

			break;
		case 2:
			// 设置起终点信息，对于tranist search 来说，城市名无意义
			stNode = PlanNode.withCityNameAndPlaceName(address, starname);
			enNode = PlanNode.withCityNameAndPlaceName(address, stopname);
			try {
				mSearch.transitSearch((new TransitRoutePlanOption()).from(
						stNode).to(enNode));
			} catch (Exception e) {
				Toast.makeText(this, "抱歉，查找出错", Toast.LENGTH_SHORT).show();
			}

			break;
		case 3:
			// 设置起终点信息，对于tranist search 来说，城市名无意义
			stNode = PlanNode.withCityNameAndPlaceName(address, starname);
			enNode = PlanNode.withCityNameAndPlaceName(address, stopname);
			try {
				mSearch.walkingSearch((new WalkingRoutePlanOption()).from(
						stNode).to(enNode));
			} catch (Exception e) {
				Toast.makeText(this, "抱歉，查找出错", Toast.LENGTH_SHORT).show();
			}
			break;
		default:
			break;
		}
		mIndex = 0;
	}

	@Override
	public void onGetSuggestionResult(SuggestionResult res) {
		// TODO Auto-generated method stub

		if (res == null || res.getAllSuggestions() == null) {
			return;
		}
		// sugAdapter.clear();
		String ss;
		for (SuggestionResult.SuggestionInfo info : res.getAllSuggestions()) {
			// if (info.key != null)
			// sugAdapter.add(info.key);
			ss  =info.district.toString();
			ss = info.city.toString();
			ss = "";
		}
		// sugAdapter.notifyDataSetChanged();

	}

	@Override
	public void onGetPoiDetailResult(PoiDetailResult result) {
		// TODO Auto-generated method stub
		if (result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(this, result.getName() + ": " + result.getAddress(),
					Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onGetPoiResult(PoiResult result) {
		// TODO Auto-generated method stub

		if (result == null
				|| result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
			Toast.makeText(this, "未找到结果", Toast.LENGTH_LONG).show();
			return;
		}
		if (result.error == SearchResult.ERRORNO.NO_ERROR) {
			mBaiduMap.clear();
			PoiOverlay overlay = new MyPoiOverlay(mBaiduMap);
			mBaiduMap.setOnMarkerClickListener(overlay);
			overlay.setData(result);
			overlay.addToMap();
			overlay.zoomToSpan();
			return;
		}
		if (result.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD) {

			// 当输入关键字在本市没有找到，但在其他城市找到时，返回包含该关键字信息的城市列表
			String strInfo = "在";
			for (CityInfo cityInfo : result.getSuggestCityList()) {
				strInfo += cityInfo.city;
				strInfo += ",";
			}
			strInfo += "找到结果";
			Toast.makeText(this, strInfo, Toast.LENGTH_LONG).show();
		}

	}

	// ************************************************************************
	// ************************************************************************
	// *************************************************************************

	private class MyPoiOverlay extends PoiOverlay {

		public MyPoiOverlay(BaiduMap baiduMap) {
			super(baiduMap);
		}

		@Override
		public boolean onPoiClick(int index) {
			super.onPoiClick(index);
			PoiInfo poi = getPoiResult().getAllPoi().get(index);
			// if (poi.hasCaterDetails) {
			mPoiSearch.searchPoiDetail((new PoiDetailSearchOption())
					.poiUid(poi.uid));
			// }
			return true;
		}
	}
	
	private void printResult(RecognizerResult results,CommonString.LocSettingEnum locenum) {
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
		if(locenum == CommonString.LocSettingEnum.loc_fujin)
		{
			///定位之后查询附近的地点
			mPoiSearch.searchNearby((new PoiNearbySearchOption()).location(new LatLng(
						mBaiduMap.getLocationData().latitude,
						mBaiduMap.getLocationData().longitude)).keyword(resultBuffer.toString()).pageNum(0).radius(2000));
			showTip("查询附近地点：" + resultBuffer.toString());
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
			printResult(results,mLocSettingName);
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
			printResult(results,mLocSettingName);

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
