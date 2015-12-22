package com.macernow.djstava.ljnavigation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.macernow.djstava.ljnavigation.R;

/**
 * 包名称: com.macernow.djstava.ljnavigation.adapter
 * 创建人: djstava
 * 创建时间: 15/10/12 下午4:05
 */
public class AdapterImageTextView extends BaseAdapter{
    private Context context;
    private LayoutInflater layoutInflater;
    private int[] title;
    private int[] images;

    public AdapterImageTextView(Context context,int[] images,int[] title) {
        this.context = context;
        this.images = images;
        this.title = title;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder viewHolder;

        if (convertView == null) {
            view = layoutInflater.inflate(R.layout.adapter_image_text_item,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView)view.findViewById(R.id.imageView);
            viewHolder.textView = (TextView)view.findViewById(R.id.textView);
            view.setTag(viewHolder);
        }
        else {
            view = convertView;
            viewHolder = (ViewHolder)view.getTag();
        }

        viewHolder.imageView.setImageResource(images[position]);
        viewHolder.textView.setText(title[position]);

        return view;
    }

    static class ViewHolder {
        ImageView imageView;
        TextView textView;
    }
}
