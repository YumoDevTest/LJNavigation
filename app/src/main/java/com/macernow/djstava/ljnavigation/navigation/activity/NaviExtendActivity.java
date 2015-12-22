package com.macernow.djstava.ljnavigation.navigation.activity;

import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.AMapNaviViewOptions;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.navi.model.NaviInfo;
import com.macernow.djstava.ljnavigation.navigation.MainApplication;
import com.macernow.djstava.ljnavigation.R;
import com.macernow.djstava.ljnavigation.navigation.TTSController;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TextView;

/**
 * 模拟导航显示界面
 */
public class NaviExtendActivity extends Activity implements
        AMapNaviListener {
    private AMapNaviView mAmapAMapNaviView;

    private TextView mRouteInfoText;
    private TextView mNaviInfoText;
    public final static String[] strActions = {"无", "自车", "左转", "右转", "左前方行驶",
            "右前方行驶", "左后方行驶", "右后方行驶", "左转掉头", "直行", "到达途经点", "进入环岛", "驶出环岛",
            "到达服务区", "到达收费站", "到达目的地", "进入隧道", "靠左", "靠右", "通过人行横道", "通过过街天桥",
            "通过地下通道", "通过广场", "到道路斜对面"};

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_naviextend);
        mRouteInfoText = (TextView) findViewById(R.id.extend_route_info);
        mNaviInfoText = (TextView) findViewById(R.id.extend_navi_info);
        init(savedInstanceState);
        MainApplication.getInstance().addActivity(this);

    }

    /**
     * 初始化
     *
     * @param savedInstanceState
     */
    private void init(Bundle savedInstanceState) {
        mAmapAMapNaviView = (AMapNaviView) findViewById(R.id.extendnavimap);
        AMapNaviViewOptions viewOptions = mAmapAMapNaviView.getViewOptions();
        viewOptions.setLayoutVisible(false);

        mAmapAMapNaviView.onCreate(savedInstanceState);

        TTSController.getInstance(this).startSpeaking();

        // 设置模拟速度
        AMapNavi.getInstance(this).setEmulatorNaviSpeed(100);
        // 开启模拟导航
        AMapNavi.getInstance(this).startNavi(AMapNavi.EmulatorNaviMode);
        AMapNavi.getInstance(this).setAMapNaviListener(this);

        AMapNaviPath naviPath = AMapNavi.getInstance(this).getNaviPath();
        if (naviPath != null) {
            double length = ((int) (naviPath.getAllLength() / (double) 1000 * 10))
                    / (double) 10;
            // 不足分钟 按分钟计
            int time = (naviPath.getAllTime() + 59) / 60;
            StringBuffer sbf = new StringBuffer();
            sbf.append("路线总时间：");
            sbf.append(time + "分钟");
            sbf.append("  路线总距离：");
            sbf.append(length + "公里");

            mRouteInfoText.setText(sbf.toString());
        }

    }


    /**
     * 返回键监听事件
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(NaviExtendActivity.this,
                    SimpleExtendNaviActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            MainApplication.getInstance().deleteActivity(this);
            ;
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    // ------------------------------生命周期方法---------------------------
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mAmapAMapNaviView.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mAmapAMapNaviView.onResume();


    }

    @Override
    public void onPause() {
        super.onPause();
        mAmapAMapNaviView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAmapAMapNaviView.onDestroy();
        //界面结束 停止语音播报
        TTSController.getInstance(this).stopSpeaking();
    }


    @Override
    public void onArriveDestination() {

        // TODO Auto-generated method stub

    }

    @Override
    public void onArrivedWayPoint(int arg0) {

        // TODO Auto-generated method stub

    }

    @Override
    public void onCalculateRouteFailure(int arg0) {

        // TODO Auto-generated method stub

    }

    @Override
    public void onCalculateRouteSuccess() {

        // TODO Auto-generated method stub

    }

    @Override
    public void onEndEmulatorNavi() {

        // TODO Auto-generated method stub

    }

    @Override
    public void onGetNavigationText(int arg0, String arg1) {


    }

    @Override
    public void onGpsOpenStatus(boolean arg0) {

        // TODO Auto-generated method stub

    }

    @Override
    public void onInitNaviFailure() {

        // TODO Auto-generated method stub

    }

    @Override
    public void onInitNaviSuccess() {

        // TODO Auto-generated method stub

    }

    @Override
    public void onLocationChange(AMapNaviLocation arg0) {

        // TODO Auto-generated method stub

    }

    @Override
    public void onNaviInfoUpdate(NaviInfo naveinfo) {
        if (naveinfo == null) {
            return;
        }
        StringBuffer sbf = new StringBuffer();
        sbf.append("当前路名：");
        sbf.append(naveinfo.getCurrentRoadName());
        sbf.append(" 下条路名：");
        sbf.append(naveinfo.getNextRoadName());
        sbf.append(" 当前方向引导：");
        sbf.append(strActions[naveinfo.m_Icon]);
        sbf.append(" 下一导航点距离：");
        sbf.append(naveinfo.getCurStepRetainDistance());
        sbf.append(" 剩余旅程：");
        sbf.append(naveinfo.getPathRetainDistance());
        if (naveinfo.getCameraDistance() != -1) {
            sbf.append(" 摄像头类型：");
            if (naveinfo.getCameraType() == 0) {
                sbf.append("测速");
            }
            if (naveinfo.getCameraType() == 1) {
                sbf.append("监控");
            }
            sbf.append(" 摄像头距离：");
            sbf.append(naveinfo.getCameraDistance());
        }
        mNaviInfoText.setText(sbf.toString());

    }

    @Override
    public void onNaviInfoUpdated(AMapNaviInfo arg0) {

        // TODO Auto-generated method stub

    }

    @Override
    public void onReCalculateRouteForTrafficJam() {

        // TODO Auto-generated method stub

    }

    @Override
    public void onReCalculateRouteForYaw() {

        // TODO Auto-generated method stub

    }

    @Override
    public void onStartNavi(int arg0) {

        // TODO Auto-generated method stub

    }

    @Override
    public void onTrafficStatusUpdate() {

        // TODO Auto-generated method stub

    }


}
