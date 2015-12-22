package com.macernow.djstava.ljnavigation.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.AdapterView;
import android.widget.ListView;

import com.macernow.djstava.ljnavigation.R;

/**
 * Created by djstava on 15/8/26.
 */
public class CornerListView extends ListView {

    public CornerListView(Context context) {
        super(context);
    }

    public CornerListView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public CornerListView(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
    }

    /****
     * 拦截触摸事件
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                int x = (int) ev.getX();
                int y = (int) ev.getY();
                int itemnum = pointToPosition(x, y);
                if (itemnum == AdapterView.INVALID_POSITION)
                    break;
                else {
                    if (itemnum == 0) {
                        if (itemnum == (getAdapter().getCount() - 1)) {
                            // 只有一项数据，设置背景设置为圆角的
                            setSelector(R.drawable.list_round);
                        } else {
                            // 第一项，设置为上面为圆角的
                            setSelector(R.drawable.list_top_round);
                        }
                    } else if (itemnum == (getAdapter().getCount() - 1))
                        // 最后一项，设置为下面为圆角的
                        setSelector(R.drawable.list_bottom_round);
                    else {
                        // 中间项，不用设置为圆角
                        setSelector(R.drawable.list_center_round);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return super.onTouchEvent(ev);
    }
}
