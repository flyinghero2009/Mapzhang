package com.zhang.map;

public class CommonString {
	
	public enum LocModeEnum {
		loc_normal,loc_following,loc_compass;
	}
	public enum LocSettingEnum {
		loc_fujin,loc_location,loc_setting,loc_about;
	}
	public static final String MSETTINGNAME="zhangmapsettingname";
	//字段名称
	public static final String FIELD_START_NAME = "field_startname";
	public static final String FIELD_STOP_NAME="field_stopname";
	
	public static final String FIELD_MODE_MAP_NAME="field_modemap_name";
	public static final String FIELD_HOT_CITY_MAP_NAME="field_hotcitymap_name";
	public static final String FIELD_TRACFFICE_MAP_NAME="field_tracfficemap_name";
	public static final String FIELD_LOCATION_MODE_NAME="field_locationmode_name";
	public static final String FIELD_VOIED_START="field_voied_start";
	//绑定名称
	public static final String BUNDLE_START_NAME="bundle_start_name";
	public static final String BUNDLE_STOP_NAME="bundle_stop_name";
	//存入的名称
	public static final String STORAGE_NORMAL_MAP_NAME="storage_normalmap_name";
	public static final String STORAGE_SATELLITE_MAP_NAME="storage_satellitemap_name";
	public static final String STORAGE_NORMAL_MODE_NAME="storage_normal_mode_name";
	public static final String STORAGE_FOLLOW_MODE_NAME="storage_follow_mode_name";
	public static final String STORAGE_COMPASS_MODE_NAME="storage_compass_mode_name";
	
	//回调code
	public static final int RESULT_MAIN_CODE = 101;
	public static final int RESULT_SEETING_CODE=102;
}
