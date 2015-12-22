package com.macernow.djstava.ljnavigation;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;


public class LoadAppActivity extends AppCompatActivity {

    private List<ResolveInfo> mApps;
    private GridView gridView;

    private AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ResolveInfo resolveInfo = mApps.get(position);
            String pkgName = resolveInfo.activityInfo.packageName;
            String actvityClass = resolveInfo.activityInfo.name;

            ComponentName componentName = new ComponentName(pkgName, actvityClass);
            Intent intent = new Intent();
            intent.setComponent(componentName);
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_load_app);

        gridView = (GridView) findViewById(R.id.gridview_loadapp);
        loadApps();

        gridView.setAdapter(new AppsAdapter());
        gridView.setOnItemClickListener(listener);
    }

    private void loadApps() {
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        mApps = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
    }

    public class AppsAdapter extends BaseAdapter {

        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            ViewHolder viewHolder;

            if (view == null) {
                view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.app_gridview_item,parent,false);
                viewHolder = new ViewHolder();
                viewHolder.imageView = (ImageView) view.findViewById(R.id.app_imageview);
                viewHolder.textView = (TextView)view.findViewById(R.id.app_textview);

                ResolveInfo resolveInfo = mApps.get(position);
                viewHolder.imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                viewHolder.imageView.setImageDrawable(resolveInfo.activityInfo.loadIcon(getPackageManager()));
                viewHolder.textView.setText(resolveInfo.loadLabel(getPackageManager()).toString());

                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder)view.getTag();
            }

            return view;
        }

        public final int getCount() {
            return mApps.size();
        }

        public final Object getItem(int position) {
            return mApps.get(position);
        }

        public final long getItemId(int position) {
            return position;
        }
    }

    static class ViewHolder{
        ImageView imageView;
        TextView textView;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_load_app, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
