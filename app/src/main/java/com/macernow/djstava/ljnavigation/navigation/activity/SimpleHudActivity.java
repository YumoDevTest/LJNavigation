package com.macernow.djstava.ljnavigation.navigation.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import com.amap.api.navi.AMapHudView;
import com.amap.api.navi.AMapHudViewListener;
import com.amap.api.navi.AMapNavi;
import com.macernow.djstava.ljnavigation.R;
import com.macernow.djstava.ljnavigation.navigation.TTSController;
import com.macernow.djstava.ljnavigation.navigation.util.Utils;

import java.awt.font.TextAttribute;

/**
 * HUD显示界面
 */
public class SimpleHudActivity extends Activity implements AMapHudViewListener {
    private int code = -1;

    private AMapHudView mAmapHudView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_hud);
        mAmapHudView = (AMapHudView) findViewById(R.id.hudview);
        mAmapHudView.setHudViewListener(this);
        // 语音播报开始
        TTSController.getInstance(this).startSpeaking();

    }

    //-----------------HUD返回键按钮事件-----------------------
    @Override
    public void onHudViewCancel() {
        if (code == Utils.SIMPLEHUDNAVIE) {
            //Intent hudIntent = new Intent(SimpleHudActivity.this, SimpleHUDNaviActivity.class);
            Intent hudIntent = new Intent(SimpleHudActivity.this, NaviCustomActivity.class);
            hudIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(hudIntent);
            AMapNavi.getInstance(this).stopNavi();
            finish();
            TTSController.getInstance(this).stopSpeaking();
        } else if (code == Utils.EMULATORNAVI) {
//			Intent customIntent = new Intent(SimpleHudActivity.this, NaviEmulatorActivity.class);
            Intent customIntent = new Intent(SimpleHudActivity.this, NaviCustomActivity.class);
            customIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(customIntent);
            finish();
        } else {
            AMapNavi.getInstance(this).stopNavi();
            finish();
            TTSController.getInstance(this).stopSpeaking();
        }

    }

    protected void onResume() {
        super.onResume();
        Bundle bundle = getIntent().getExtras();
        processBundle(bundle);
    }

    private void processBundle(Bundle bundle) {
        if (bundle != null) {

            code = bundle.getInt(Utils.ACTIVITYINDEX, -1);
            if (code == Utils.SIMPLEHUDNAVIE) {
                AMapNavi.getInstance(this).startNavi(AMapNavi.EmulatorNaviMode);
            }

        }

    }

    /**
     * 返回键监听
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if (code == Utils.SIMPLEHUDNAVIE) {
                //Intent hudIntent = new Intent(SimpleHudActivity.this, SimpleHUDNaviActivity.class);
                Intent hudIntent = new Intent(SimpleHudActivity.this, NaviCustomActivity.class);
                hudIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(hudIntent);
                finish();
                //AMapNavi.getInstance(this).stopNavi();
                //TTSController.getInstance(this).stopSpeaking();
            } else if (code == Utils.EMULATORNAVI) {
                //Intent customIntent = new Intent(SimpleHudActivity.this, NaviEmulatorActivity.class);
                Intent customIntent = new Intent(SimpleHudActivity.this, NaviCustomActivity.class);
                customIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(customIntent);
                finish();
            } else {
                AMapNavi.getInstance(this).stopNavi();
                finish();
                TTSController.getInstance(this).stopSpeaking();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onPause() {
        super.onPause();
        mAmapHudView.onPause();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAmapHudView.onDestroy();
    }


}
