package com.macernow.djstava.ljnavigation.navigation.activity;

import java.util.ArrayList;
import java.util.List;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.macernow.djstava.ljnavigation.MainActivity;
import com.macernow.djstava.ljnavigation.adapter.AdapterTextview;
import com.macernow.djstava.ljnavigation.navigation.MainApplication;
import com.macernow.djstava.ljnavigation.R;
import com.macernow.djstava.ljnavigation.navigation.TTSController;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;

import android.view.View;

import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 初始化界面，用于设置起点终点，发起路径计算导航等
 */
public class NaviStartActivity extends Activity implements OnClickListener {
    private static final String TAG = NaviStartActivity.class.getSimpleName();
    private static final int START_POINT_REQUEST_CODE = 1;
    private static final int WAY_POINT_1_REQUEST_CODE = 2;
    private static final int WAY_POINT_2_REQUEST_CODE = 3;
    private static final int WAY_POINT_3_REQUEST_CODE = 4;
    private static final int END_POINT_REQUEST_CODE = 5;

    // --------------View基本控件---------------------
    private MapView mMapView;// 地图控件
    //private RadioGroup mNaviMethodGroup;// 步行驾车选择控件
    //private AutoCompleteTextView mStartPointText;// 起点输入
    private EditText mStartPointText;// 起点输入
    private EditText mWay1PointText, mWay2PointText, mWay3PointText;// 途经点输入
    private EditText mEndPointText;// 终点输入
    private AutoCompleteTextView mStrategyText;// 行车策略输入
    private Button mRouteButton;// 路径规划按钮
    private Button mNaviButton;// 模拟导航按钮
    private ProgressDialog mProgressDialog;// 路径规划过程显示状态
    private ProgressDialog mGPSProgressDialog;// GPS过程显示状态
    private ImageView mStartImage;// 起点下拉按钮
    private ImageView mWayImage;// 途经点点击按钮
    private ImageView mEndImage;// 终点点击按钮
    private ImageView mStrategyImage;// 行车策略点击按钮
    // 地图和导航核心逻辑类
    private AMap mAmap;
    private AMapNavi mAmapNavi;
    // ---------------------变量---------------------
    private String[] mStrategyMethods;// 记录行车策略的数组
    private String[] mPositionMethods;// 记录起点我的位置、地图点选数组
    // 驾车路径规划起点，途经点，终点的list
    private List<NaviLatLng> mStartPoints = new ArrayList<NaviLatLng>();
    private List<NaviLatLng> mWayPoints = new ArrayList<NaviLatLng>();
    private List<NaviLatLng> mEndPoints = new ArrayList<NaviLatLng>();
    // 记录起点、终点、途经点位置
    private NaviLatLng mStartPoint = new NaviLatLng();
    private NaviLatLng mEndPoint = new NaviLatLng();
    private NaviLatLng mWay1Point = new NaviLatLng();
    private NaviLatLng mWay2Point = new NaviLatLng();
    private NaviLatLng mWay3Point = new NaviLatLng();
    // 记录起点、终点、途经点在地图上添加的Marker
    private Marker mStartMarker;
    private Marker mWayMarker;
    private Marker mEndMarker;
    private Marker mGPSMarker;
    private boolean mIsGetGPS = false;// 记录GPS定位是否成功
    private boolean mIsStart = false;// 记录是否已我的位置发起路径规划

    private ArrayAdapter<String> mPositionAdapter;

    private AMapNaviListener mAmapNaviListener;

    private ImageView imageView_voice, imageView_config, imageView_home, imageView_company;
    private ListView listView_poi_result;

    private ArrayList<String> startPoiAddressList = new ArrayList<String>();
    private ArrayList<LatLonPoint> startPoiLatLonList = new ArrayList<LatLonPoint>();
    private String[] startPoiAddressArray;
    private LatLonPoint[] startPoiLatLngArray;
    private AdapterTextview adapterTextview_start_poi_result;

    private ArrayList<String> poiAddressList = new ArrayList<String>();
    private ArrayList<LatLonPoint> poiLatLonList = new ArrayList<LatLonPoint>();
    private String[] poiAddressArray;
    private LatLonPoint[] poiLatLngArray;
    private AdapterTextview adapterTextview_poi_result;

    // 记录地图点击事件相应情况，根据选择不同，地图响应不同
    private int mMapClickMode = MAP_CLICK_NO;
    private static final int MAP_CLICK_NO = 0;// 地图不接受点击事件
    private static final int MAP_CLICK_START = 1;// 地图点击设置起点
    private static final int MAP_CLICK_WAY = 2;// 地图点击设置途经点
    private static final int MAP_CLICK_END = 3;// 地图点击设置终点

    // 记录导航种类，用于记录当前选择是驾车还是步行
    private int mTravelMethod = DRIVER_NAVI_METHOD;
    private static final int DRIVER_NAVI_METHOD = 0;// 驾车导航
    private static final int WALK_NAVI_METHOD = 1;// 步行导航

    private int mNaviMethod;
    private static final int NAVI_METHOD = 0;// 执行模拟导航操作
    private static final int ROUTE_METHOD = 1;// 执行计算线路操作

    private int mStartPointMethod = BY_MY_POSITION;
    private static final int BY_MY_POSITION = 0;// 以我的位置作为起点
    private static final int BY_MAP_POSITION = 1;// 以地图点选的点为起点
    // 计算路的状态
    private final static int GPSNO = 0;// 使用我的位置进行计算、GPS定位还未成功状态
    private final static int CALCULATEERROR = 1;// 启动路径计算失败状态
    private final static int CALCULATESUCCESS = 2;// 启动路径计算成功状态

    //定位
    private LocationManagerProxy mLocationManger;

    private AMapLocationListener mLocationListener = new AMapLocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onLocationChanged(Location location) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onLocationChanged(AMapLocation location) {
            if (location != null && location.getAMapException().getErrorCode() == 0) {
                mIsGetGPS = true;
                mStartPoint = new NaviLatLng(location.getLatitude(), location.getLongitude());

                //Log.e(TAG, "GPS: " + location.getLatitude() + "," + location.getLongitude());

                mGPSMarker.setPosition(new LatLng(
                        mStartPoint.getLatitude(), mStartPoint
                        .getLongitude()));
                mStartPoints.clear();
                mStartPoints.add(mStartPoint);

                dissmissGPSProgressDialog();

                LatLng latLng = new LatLng(mStartPoint.getLatitude(), mStartPoint.getLongitude());

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);

                mAmap.addMarker(markerOptions);
                mAmap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));

                //calculateRoute();
            } else {
                showToast(getResources().getString(R.string.navi_location_fail_hint));
                mGPSProgressDialog.dismiss();

                //以上海市人民政府为中心显示定位时出错的初始地图
                LatLng latLng = new LatLng(31.230429, 121.473709);
                mAmap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
            }
        }
    };


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_navistart);

        TTSController ttsManager = TTSController.getInstance(this);// 初始化语音模块
        ttsManager.init();
        AMapNavi.getInstance(this).setAMapNaviListener(ttsManager);// 设置语音模块播报

        // 初始化所需资源、控件、事件监听
        initResources();
        initView(savedInstanceState);
        initListener();
        initMapAndNavi();
        MainApplication.getInstance().addActivity(this);

        //
        addPosition();
        removePosition();
    }

    //Add max of 3 EditText fields, let user to set pass through position.
    public void addPosition() {
        final ImageButton addButton = (ImageButton) findViewById(R.id.addPosition);

        final ImageButton removePosition1 = (ImageButton) findViewById(R.id.removePosition1);
        final ImageButton removePosition2 = (ImageButton) findViewById(R.id.removePosition2);
        final ImageButton removePosition3 = (ImageButton) findViewById(R.id.removePosition3);

        final EditText position1 = (EditText) findViewById(R.id.position1);
        final EditText position2 = (EditText) findViewById(R.id.position2);
        final EditText position3 = (EditText) findViewById(R.id.position3);

        position1.setVisibility(View.GONE);
        position2.setVisibility(View.GONE);
        position3.setVisibility(View.GONE);
        removePosition1.setVisibility(View.GONE);
        removePosition2.setVisibility(View.GONE);
        removePosition3.setVisibility(View.GONE);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position1.getVisibility() == View.GONE) {
                    position1.setVisibility(View.VISIBLE);
                    removePosition1.setVisibility(View.VISIBLE);
                } else if (position1.getVisibility() == View.VISIBLE && position2.getVisibility() == View.GONE) {
                    position2.setVisibility(View.VISIBLE);
                    removePosition2.setVisibility(View.VISIBLE);
                } else if (position2.getVisibility() == View.VISIBLE && position3.getVisibility() == View.GONE) {
                    position3.setVisibility(View.VISIBLE);
                    removePosition3.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    //Remove specific EditText field.
    public void removePosition() {
        final ImageButton removePosition1 = (ImageButton) findViewById(R.id.removePosition1);
        final ImageButton removePosition2 = (ImageButton) findViewById(R.id.removePosition2);
        final ImageButton removePosition3 = (ImageButton) findViewById(R.id.removePosition3);

        final EditText position1 = (EditText) findViewById(R.id.position1);
        final EditText position2 = (EditText) findViewById(R.id.position2);
        final EditText position3 = (EditText) findViewById(R.id.position3);

        removePosition1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!position1.getText().toString().isEmpty()) {
                    position1.setText("");
                }
                position1.setVisibility(View.GONE);
                removePosition1.setVisibility(View.GONE);
            }
        });

        removePosition2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!position2.getText().toString().isEmpty()) {
                    position2.setText("");
                }
                position2.setVisibility(View.GONE);
                removePosition2.setVisibility(View.GONE);
            }
        });

        removePosition3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!position3.getText().toString().isEmpty()){
                    position3.setText("");
                }
                position3.setVisibility(View.GONE);
                removePosition3.setVisibility(View.GONE);
            }
        });

    }
    // ----------具体处理方法--------------

    /**
     * 算路的方法，根据选择可以进行行车和步行两种方式进行路径规划
     */
    private void calculateRoute() {
            /*
            if(mStartPointMethod==BY_MY_POSITION&&!mIsGetGPS){
				mLocationManger=LocationManagerProxy.getInstance(this);
				//进行一次定位
				mLocationManger.requestLocationData(LocationProviderProxy.AMapNetwork, -1, 15, mLocationListener);
				showGPSProgressDialog();
				
				return;
			}
			*/

        mIsGetGPS = false;
        switch (mTravelMethod) {
            // 驾车导航
            case DRIVER_NAVI_METHOD:
                int driverIndex = calculateDriverRoute();
                if (driverIndex == CALCULATEERROR) {
                    showToast(getResources().getString(R.string.navi_routing_fail_hint));
                    return;
                } else if (driverIndex == GPSNO) {
                    return;
                }
                break;
            // 步行导航
            case WALK_NAVI_METHOD:
                Log.e(TAG, "driver WALK_NAVI_METHOD");
                int walkIndex = calculateWalkRoute();
                if (walkIndex == CALCULATEERROR) {
                    showToast(getResources().getString(R.string.navi_routing_fail_hint));
                    return;
                } else if (walkIndex == GPSNO) {
                    return;
                }
                break;
        }
        // 显示路径规划的窗体
        showProgressDialog();
    }

    /**
     * 对行车路线进行规划
     */
    private int calculateDriverRoute() {
        int driveMode = getDriveMode();
        int code = CALCULATEERROR;

        if (mAmapNavi.calculateDriveRoute(mStartPoints, mEndPoints,
                mWayPoints, driveMode)) {
            code = CALCULATESUCCESS;
        } else {

            code = CALCULATEERROR;
        }


        return code;
    }

    /**
     * 对步行路线进行规划
     */
    private int calculateWalkRoute() {
        int code = CALCULATEERROR;
        if (mAmapNavi.calculateWalkRoute(mStartPoint, mEndPoint)) {
            code = CALCULATESUCCESS;
        } else {

            code = CALCULATEERROR;
        }

        return code;
    }


    /**
     * 根据选择，获取行车策略
     */
    private int getDriveMode() {
        String strategyMethod = mStrategyText.getText().toString();
        // 速度优先
        if (mStrategyMethods[0].equals(strategyMethod)) {
            return AMapNavi.DrivingDefault;
        }
        // 花费最少
        else if (mStrategyMethods[1].equals(strategyMethod)) {
            return AMapNavi.DrivingSaveMoney;

        }
        // 距离最短
        else if (mStrategyMethods[2].equals(strategyMethod)) {
            return AMapNavi.DrivingShortDistance;
        }
        // 不走高速
        else if (mStrategyMethods[3].equals(strategyMethod)) {
            return AMapNavi.DrivingNoExpressways;
        }
        // 时间最短且躲避拥堵
        else if (mStrategyMethods[4].equals(strategyMethod)) {
            return AMapNavi.DrivingFastestTime;
        } else if (mStrategyMethods[5].equals(strategyMethod)) {
            return AMapNavi.DrivingAvoidCongestion;
        } else {
            return AMapNavi.DrivingDefault;
        }
    }

    // -----------------初始化-------------------

    /**
     * 初始化界面所需View控件
     *
     * @param savedInstanceState
     */
    private void initView(Bundle savedInstanceState) {
        mMapView = (MapView) findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        mAmap = mMapView.getMap();

        //mStartPointText = (AutoCompleteTextView) findViewById(R.id.navi_start_edit);
        mStartPointText = (EditText) findViewById(R.id.navi_start_edit);

        //mStartPointText.setDropDownBackgroundResource(R.drawable.whitedownborder);
        mWay1PointText = (EditText) findViewById(R.id.position1);
        mWay2PointText = (EditText) findViewById(R.id.position2);
        mWay3PointText = (EditText) findViewById(R.id.position3);

        mEndPointText = (EditText) findViewById(R.id.navi_end_edit);
        mStrategyText = (AutoCompleteTextView) findViewById(R.id.navi_strategy_edit);
        mStrategyText.setDropDownBackgroundResource(R.drawable.whitedownborder);

        mStartPointText.setInputType(InputType.TYPE_NULL);
        mWay1PointText.setInputType(InputType.TYPE_NULL);
        mWay2PointText.setInputType(InputType.TYPE_NULL);
        mWay3PointText.setInputType(InputType.TYPE_NULL);
        mEndPointText.setInputType(InputType.TYPE_NULL);
        mStrategyText.setInputType(InputType.TYPE_NULL);

        ArrayAdapter<String> strategyAdapter = new ArrayAdapter<String>(this,
                R.layout.strategy_inputs, mStrategyMethods);
        mStrategyText.setAdapter(strategyAdapter);

        mPositionAdapter = new ArrayAdapter<String>(this,
                R.layout.strategy_inputs, mPositionMethods);
        //mStartPointText.setAdapter(mPositionAdapter);

        mRouteButton = (Button) findViewById(R.id.navi_route_button);
        //mNaviButton = (Button) findViewById(R.id.navi_navi_button);
        //mNaviMethodGroup = (RadioGroup) findViewById(R.id.navi_method_radiogroup);

        //mStartImage = (ImageView) findViewById(R.id.navi_start_image);
        //mWayImage = (ImageView) findViewById(R.id.navi_way_image);
        //mEndImage = (ImageView) findViewById(R.id.navi_end_image);
        //mStrategyImage = (ImageView) findViewById(R.id.navi_strategy_image);

        //imageView_voice = (ImageView) findViewById(R.id.np_voice);
        //imageView_config = (ImageView) findViewById(R.id.np_fav);
        //imageView_home = (ImageView) findViewById(R.id.np_home);
        //imageView_company = (ImageView) findViewById(R.id.np_work);

        //listView_poi_result = (ListView) findViewById(R.id.listview_poi_result);
        //listView_poi_result = (ListView) findViewById(R.id.listview_result);
    }

    /**
     * 初始化资源文件，主要是字符串
     */
    private void initResources() {
        Resources res = getResources();
        mStrategyMethods = new String[]{
                res.getString(R.string.navi_strategy_speed),
                res.getString(R.string.navi_strategy_cost),
                res.getString(R.string.navi_strategy_distance),
                res.getString(R.string.navi_strategy_nohighway),
                res.getString(R.string.navi_strategy_timenojam),
                res.getString(R.string.navi_strategy_costnojam)};
        mPositionMethods = new String[]{res.getString(R.string.mypoistion),
                res.getString(R.string.mappoistion)};

    }

    /**
     * 初始化所需监听
     */
    private void initListener() {
        // 控件点击事件
        mStartPointText.setOnClickListener(this);
        mWay1PointText.setOnClickListener(this);
        mWay2PointText.setOnClickListener(this);
        mWay3PointText.setOnClickListener(this);
        mEndPointText.setOnClickListener(this);
        mStrategyText.setOnClickListener(this);
        mRouteButton.setOnClickListener(this);

        //mNaviButton.setOnClickListener(this);
        //mStartImage.setOnClickListener(this);
        //mWayImage.setOnClickListener(this);
        //mEndImage.setOnClickListener(this);
        //mStrategyImage.setOnClickListener(this);
        //mNaviMethodGroup.setOnCheckedChangeListener(this);
        // 设置地图点击事件
        //mAmap.setOnMapClickListener(this);
        // 起点下拉框点击事件监听
        //mStartPointText.setOnItemClickListener(getOnItemClickListener());
    }

    /**
     * 初始化地图和导航相关内容
     */
    private void initMapAndNavi() {
        // 初始语音播报资源
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        //定位到当前位置
        showYourPosition();

        mAmapNavi = AMapNavi.getInstance(this);// 初始化导航引擎

        // 初始化Marker添加到地图
        mStartMarker = mAmap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                        .decodeResource(getResources(), R.drawable.start))));
        mWayMarker = mAmap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                        .decodeResource(getResources(), R.drawable.way))));
        mEndMarker = mAmap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                        .decodeResource(getResources(), R.drawable.end))));
        mGPSMarker = mAmap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                        .decodeResource(getResources(),
                                R.drawable.location_marker))));

    }

    private void showYourPosition() {
        if (mStartPointMethod == BY_MY_POSITION && !mIsGetGPS) {
            mLocationManger = LocationManagerProxy.getInstance(this);
            //进行一次定位
            mLocationManger.requestLocationData(LocationProviderProxy.AMapNetwork, -1, 15, mLocationListener);
            showGPSProgressDialog();

        }
    }

    // ----------------------事件处理---------------------------

    /**
     * 控件点击事件监听
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 路径规划按钮处理事件
            case R.id.navi_route_button:
                String start = mStartPointText.getText().toString();
                String destination = mEndPointText.getText().toString();

                if (destination.isEmpty() || start.isEmpty()) {
                    Toast.makeText(NaviStartActivity.this, R.string.navi_routing_hint, Toast.LENGTH_SHORT).show();
                } else {
                    mNaviMethod = ROUTE_METHOD;
                    calculateRoute();
                }

                break;

            // 模拟导航处理事件
            /*
            case R.id.navi_navi_button:
                mNaviMethod = NAVI_METHOD;
                calculateRoute();
                break;
            */

            // 起点点击事件
            case R.id.navi_start_edit:

                mStartPointText.setText("");
                startActivityForResult(new Intent(NaviStartActivity.this, StartPointHistoryActivity.class), START_POINT_REQUEST_CODE);

                break;

            // 终点点击事件
            case R.id.navi_end_edit:

                mEndPointText.setText("");
                startActivityForResult(new Intent(NaviStartActivity.this, EndPointHistoryActivity.class), END_POINT_REQUEST_CODE);

                break;

            // 策略点击事件
            //case R.id.navi_strategy_image:
            case R.id.navi_strategy_edit:
                //首先关闭目的地的软键盘
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(mEndPointText.getWindowToken(), 0);

                mStrategyText.showDropDown();
                break;

            case R.id.position1:

                mWay1PointText.setText("");
                startActivityForResult(new Intent(NaviStartActivity.this,WayPointHistoryActivity.class),WAY_POINT_1_REQUEST_CODE);

                break;

            case R.id.position2:
                mWay2PointText.setText("");
                startActivityForResult(new Intent(NaviStartActivity.this,WayPointHistoryActivity.class),WAY_POINT_2_REQUEST_CODE);

                break;

            case R.id.position3:
                mWay3PointText.setText("");
                startActivityForResult(new Intent(NaviStartActivity.this,WayPointHistoryActivity.class),WAY_POINT_3_REQUEST_CODE);

                break;

            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case START_POINT_REQUEST_CODE:
                String start_point_history_string = data.getStringExtra("start_point_history_string");

                Double start_lat = data.getDoubleExtra("start_point_history_lat", 0);
                Double start_lng = data.getDoubleExtra("start_point_history_lng", 0);

                Log.e(TAG, "Start Point:" + start_point_history_string + " lat:" + start_lat + " lng:" + start_lng);

                mStartPointText.setText(start_point_history_string);
                mStartPoint = new NaviLatLng(start_lat, start_lng);
                mStartPoints.add(mStartPoint);

                break;

            case WAY_POINT_1_REQUEST_CODE:
                String way1_point_history_string = data.getStringExtra("way_point_history_string");

                Double way1_lat = data.getDoubleExtra("way_point_history_lat", 0);
                Double way1_lng = data.getDoubleExtra("way_point_history_lng", 0);

                Log.e(TAG, "WAY1 Point:" + way1_point_history_string + " lat:" + way1_lat + " lng:" + way1_lng);

                mWay1PointText.setText(way1_point_history_string);
                mWay1Point = new NaviLatLng(way1_lat, way1_lng);
                mWayPoints.add(mWay1Point);
                break;

            case WAY_POINT_2_REQUEST_CODE:
                String way2_point_history_string = data.getStringExtra("way_point_history_string");

                Double way2_lat = data.getDoubleExtra("way_point_history_lat", 0);
                Double way2_lng = data.getDoubleExtra("way_point_history_lng", 0);

                Log.e(TAG, "WAY2 Point:" + way2_point_history_string + " lat:" + way2_lat + " lng:" + way2_lng);

                mWay2PointText.setText(way2_point_history_string);
                mWay2Point = new NaviLatLng(way2_lat, way2_lng);
                mWayPoints.add(mWay2Point);
                break;

            case WAY_POINT_3_REQUEST_CODE:
                String way3_point_history_string = data.getStringExtra("way_point_history_string");

                Double way3_lat = data.getDoubleExtra("way_point_history_lat", 0);
                Double way3_lng = data.getDoubleExtra("way_point_history_lng", 0);

                Log.e(TAG, "WAY3 Point:" + way3_point_history_string + " lat:" + way3_lat + " lng:" + way3_lng);

                mWay3PointText.setText(way3_point_history_string);
                mWay3Point = new NaviLatLng(way3_lat, way3_lng);
                mWayPoints.add(mWay3Point);
                break;

            case END_POINT_REQUEST_CODE:
                String end_point_history_string = data.getStringExtra("end_point_history_string");
                Double end_lat = data.getDoubleExtra("end_point_history_lat", 0);
                Double end_lng = data.getDoubleExtra("end_point_history_lng", 0);

                Log.e(TAG, "End Point:" + end_point_history_string + " lat:" + end_lat + " lng:" + end_lng);

                if (!end_point_history_string.isEmpty()) {
                    mEndPointText.setText(end_point_history_string);
                }

                mEndPoint = new NaviLatLng(end_lat, end_lng);
                mEndPoints.add(mEndPoint);
                break;

            default:
                break;
        }
    }

    private void addressToGeocode(String address) {
        GeocodeSearch geocodeSearch = new GeocodeSearch(this);
        geocodeSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
            @Override
            public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
                //逆地理编码，经纬度转换成地址
            }

            @Override
            public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
                //地理编码，地址转换成经纬度
                Log.e(TAG, "onGeocodeSearched retCode = " + i);

                if (i == 0) {
                    if (geocodeResult != null && geocodeResult.getGeocodeAddressList() != null && geocodeResult.getGeocodeAddressList().size() > 0) {
                        GeocodeAddress geocodeAddress = geocodeResult.getGeocodeAddressList().get(0);
                        Log.e(TAG, "destination position: " + geocodeAddress.getLatLonPoint());

                        mEndPoint = new NaviLatLng(geocodeAddress.getLatLonPoint().getLatitude(), geocodeAddress.getLatLonPoint().getLongitude());
                        mEndPoints.add(mEndPoint);

                        Message message = new Message();
                        message.what = 1;
                        handler.sendMessage(message);
                    }
                } else {
                    Toast.makeText(NaviStartActivity.this, "该地址不存在!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        //目前只支持上海地区地址的查询
        GeocodeQuery geocodeQuery = new GeocodeQuery(address, "021");
        geocodeSearch.getFromLocationNameAsyn(geocodeQuery);

        mNaviMethod = ROUTE_METHOD;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case 1:
                    //地理转换完成,再开始导航规划
                    calculateRoute();
                    break;

                default:
                    Toast.makeText(NaviStartActivity.this, R.string.navi_routing_fail, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    /**
     * 地图点击事件监听
     * */
    /*
    @Override
	public void onMapClick(LatLng latLng) {
		// 默认不接受任何操作
		if (mMapClickMode == MAP_CLICK_NO) {
			return;
		}
		// 其他情况根据起点、途经点、终点不同逻辑处理不同
		addPointToMap(latLng);

	}
	*/

    /**
     * 地图点击事件核心处理逻辑
     * @param position
     */
    /*
    private void addPointToMap(LatLng position) {
		NaviLatLng naviLatLng = new NaviLatLng(position.latitude,
				position.longitude);
		switch (mMapClickMode) {
		//起点
		case MAP_CLICK_START:
			mStartMarker.setPosition(position);
			mStartPoint = naviLatLng;
			mStartPoints.clear();
			mStartPoints.add(mStartPoint);
			setTextDescription(mStartPointText, "已成功设置起点");
			break;
		//途经点	
		case MAP_CLICK_WAY:
			mWayMarker.setPosition(position);
			mWayPoints.clear();
			mWayPoint = naviLatLng;
			mWayPoints.add(mWayPoint);
			setTextDescription(mWayPointText, "已成功设置途经点");
			break;
			//终点
		case MAP_CLICK_END:
			mEndMarker.setPosition(position);
			mEndPoints.clear();
			mEndPoint = naviLatLng;
			mEndPoints.add(mEndPoint);
			setTextDescription(mEndPointText, "已成功设置终点");
			break;
		}

	}
	*/

    /**
     * 起点下拉框点击事件监听
     */
    private OnItemClickListener getOnItemClickListener() {
        return new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int index,
                                    long arg3) {
                switch (index) {
                    // 我的位置为起点进行导航或路径规划
                    case 0:
                        mStartPointMethod = BY_MY_POSITION;
                        break;
                    // 地图点选起点进行导航或路径规划
                    case 1:
                        mStartPointMethod = BY_MAP_POSITION;
                        mMapClickMode = MAP_CLICK_START;
                        showToast("点击地图添加起点");
                        break;
                }

            }

        };
    }

    /**
     * 导航回调函数
     *
     * @return
     */
    private AMapNaviListener getAMapNaviListener() {
        if (mAmapNaviListener == null) {

            mAmapNaviListener = new AMapNaviListener() {

                @Override
                public void onTrafficStatusUpdate() {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onStartNavi(int arg0) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onReCalculateRouteForYaw() {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onReCalculateRouteForTrafficJam() {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onLocationChange(AMapNaviLocation location) {


                }

                @Override
                public void onInitNaviSuccess() {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onInitNaviFailure() {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onGetNavigationText(int arg0, String arg1) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onEndEmulatorNavi() {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onCalculateRouteSuccess() {
                    dissmissProgressDialog();
                    switch (mNaviMethod) {
                        case ROUTE_METHOD:
                            Intent intent = new Intent(NaviStartActivity.this,
                                    NaviRouteActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            startActivity(intent);

                            break;
                        case NAVI_METHOD:
                            Intent standIntent = new Intent(NaviStartActivity.this,
                                    NaviEmulatorActivity.class);
                            standIntent
                                    .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            startActivity(standIntent);

                            break;
                    }
                }

                @Override
                public void onCalculateRouteFailure(int arg0) {
                    dissmissProgressDialog();
                    showToast("路径规划出错");
                }

                @Override
                public void onArrivedWayPoint(int arg0) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onArriveDestination() {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onGpsOpenStatus(boolean arg0) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onNaviInfoUpdated(AMapNaviInfo arg0) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onNaviInfoUpdate(NaviInfo arg0) {

                    // TODO Auto-generated method stub

                }
            };
        }
        return mAmapNaviListener;
    }

    /**
     * 返回键处理事件
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //Intent intent=new Intent(NaviStartActivity.this,MainStartActivity.class);
            Intent intent = new Intent(NaviStartActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            MainApplication.getInstance().deleteActivity(this);
            finish();

        }
        return super.onKeyDown(keyCode, event);
    }

    // ---------------UI操作----------------
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void setTextDescription(TextView view, String description) {
        view.setText(description);
    }

    /**
     * 显示进度框
     */
    private void showProgressDialog() {
        if (mProgressDialog == null)
            mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setMessage(getResources().getString(R.string.navi_routing));
        mProgressDialog.show();
    }

    /**
     * 隐藏进度框
     */
    private void dissmissProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    /**
     * 显示GPS进度框
     */
    private void showGPSProgressDialog() {
        if (mGPSProgressDialog == null)
            mGPSProgressDialog = new ProgressDialog(this);
        mGPSProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mGPSProgressDialog.setIndeterminate(false);
        mGPSProgressDialog.setCancelable(true);
        mGPSProgressDialog.setMessage(getResources().getString(R.string.navi_locating));
        mGPSProgressDialog.show();
    }

    /**
     * 隐藏进度框
     */
    private void dissmissGPSProgressDialog() {
        if (mGPSProgressDialog != null) {
            mGPSProgressDialog.dismiss();
        }
    }


    // -------------生命周期必须重写方法----------------
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        // 以上两句必须重写
        // 以下两句逻辑是为了保证进入首页开启定位和加入导航回调
        AMapNavi.getInstance(this).setAMapNaviListener(getAMapNaviListener());
        mAmapNavi.startGPS();
        TTSController.getInstance(this).startSpeaking();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
        // 以上两句必须重写
        // 下边逻辑是移除监听
        AMapNavi.getInstance(this)
                .removeAMapNaviListener(getAMapNaviListener());
        mAmapNavi.pauseNavi();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        mAmapNavi.destroy();
    }

}
