package com.macernow.djstava.ljnavigation.app;

import android.content.Intent;
import android.graphics.drawable.Drawable;

/**
 * Created by djstava on 15/7/2.
 */

public class AppInfo {

    private String appName;
    private String appVersion;
    private Drawable drawable;
    private Boolean isUserApp;
    private String packageName;
    private Boolean inSql;
    private Intent intent;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void setIntent(Intent intent){
        this.intent = intent;
    }
    public Intent getIntent(){
        return intent;
    }

    public String getAppName() {
        return appName;
    }

    public Boolean getIsUserApp() {
        return isUserApp;
    }

    public void setIsUserApp(Boolean isUserApp) {
        this.isUserApp = isUserApp;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }
    public void setinSql(Boolean inSql){
        this.inSql = inSql;
    }
    public Boolean getinSql(){
        return inSql;
    }

}