package com.macernow.djstava.ljnavigation.navigation.activity;
 

import com.amap.api.navi.AMapNavi;
import com.macernow.djstava.ljnavigation.navigation.MainApplication;
import com.macernow.djstava.ljnavigation.R;
import com.macernow.djstava.ljnavigation.navigation.TTSController;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
/**
 * 
 * 首页面
 * */
public class MainStartActivity extends Activity implements OnClickListener{
 
	private TextView mRouteTextView;
	private TextView mNaviTextView;
	private TextView mHudTextView;
	private TextView mComplexTextView;
	private TextView mExtendTextView;

	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mainstart);
		initView();
	}
	private void initView(){
		setTitle("导航SDK " + AMapNavi.getVersion());
		mRouteTextView=(TextView) findViewById(R.id.main_start_route_text);
		mNaviTextView=(TextView) findViewById(R.id.main_start_navi_text);
		mHudTextView=(TextView) findViewById(R.id.main_start_hud_text);
		mComplexTextView=(TextView) findViewById(R.id.main_start_complex_text);
		mExtendTextView=(TextView) findViewById(R.id.main_start_extend_text);
		mRouteTextView.setOnClickListener(this);
		mNaviTextView.setOnClickListener(this);
		mHudTextView.setOnClickListener(this);
		mComplexTextView.setOnClickListener(this);
		mExtendTextView.setOnClickListener(this);
		TTSController ttsManager = TTSController.getInstance(this);// 初始化语音模块
		ttsManager.init();
		AMapNavi.getInstance(this).setAMapNaviListener(ttsManager);// 设置语音模块播报
	}
	
	
	/**
	 * 返回键处理事件
	 * */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			MainApplication.getInstance().exit(); 
			finish();
			System.exit(0);// 退出程序
		}
		return super.onKeyDown(keyCode, event);
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		//路径规划
		case R.id.main_start_route_text:
			Intent routeIntent=new Intent(MainStartActivity.this,SimpleNaviRouteActivity.class);
			routeIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(routeIntent);
			break;
			//实时导航
		case R.id.main_start_navi_text:
			Intent gpsNaviIntent=new Intent(MainStartActivity.this,SimpleGPSNaviActivity.class);
			gpsNaviIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(gpsNaviIntent);
			break;
			//HUD导航
		case R.id.main_start_hud_text:
			Intent hudNaviIntent=new Intent(MainStartActivity.this,SimpleHUDNaviActivity.class);
			hudNaviIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(hudNaviIntent);
			break;
		case R.id.main_start_extend_text:
			Intent extendNaviIntent=new Intent(MainStartActivity.this,SimpleExtendNaviActivity.class);
			extendNaviIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(extendNaviIntent);
			break;
			//综合展示
		case R.id.main_start_complex_text:
			Intent naviStartIntent=new Intent(MainStartActivity.this,NaviStartActivity.class);
			naviStartIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(naviStartIntent);
			break;	
			
		}
		
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy(); 
		// 这是最后退出页，所以销毁导航和播报资源
		AMapNavi.getInstance(this).destroy();// 销毁导航
		TTSController.getInstance(this).stopSpeaking();
		TTSController.getInstance(this).destroy();

	}
	
}
