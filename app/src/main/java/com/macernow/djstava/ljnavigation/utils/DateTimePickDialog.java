package com.macernow.djstava.ljnavigation.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TimePicker;

import com.macernow.djstava.ljnavigation.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * 包名称: com.macernow.djstava.ljnavigation.utils
 * 创建人: djstava
 * 创建时间: 15/8/31 下午1:49
 */
public class DateTimePickDialog implements DatePicker.OnDateChangedListener, TimePicker.OnTimeChangedListener {

    private Activity activity;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private String initDateTime;
    private AlertDialog alertDialog;
    private String dateTime;

    public DateTimePickDialog(Activity activity, String initDateTime) {
        this.activity = activity;
        this.initDateTime = initDateTime;
    }

    private void init(DatePicker datePicker, TimePicker timePicker) {
        Calendar calendar = Calendar.getInstance();
        if (!(null == initDateTime || "".equals(initDateTime))) {
            calendar = this.getCalendarByInintData(initDateTime);
        } else {
            initDateTime = calendar.get(Calendar.YEAR) + "年"
                    + calendar.get(Calendar.MONTH) + "月"
                    + calendar.get(Calendar.DAY_OF_MONTH) + "日 "
                    + calendar.get(Calendar.HOUR_OF_DAY) + ":"
                    + calendar.get(Calendar.MINUTE);
        }

        datePicker.init(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH), this);
        timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
        timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
    }

    public AlertDialog dateTimePicKDialog() {
        LinearLayout dateTimeLayout = (LinearLayout) activity
                .getLayoutInflater().inflate(R.layout.common_date_time, null);

        datePicker = (DatePicker) dateTimeLayout.findViewById(R.id.datepicker);
        timePicker = (TimePicker) dateTimeLayout.findViewById(R.id.timepicker);
        init(datePicker, timePicker);
        timePicker.setIs24HourView(true);
        timePicker.setOnTimeChangedListener(this);

        alertDialog = new AlertDialog.Builder(activity)
                .setTitle(initDateTime)
                .setCancelable(false)
                .setView(dateTimeLayout)
                .setPositiveButton(R.string.sys_date_time_set, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        /*
                        * TODO: 修改系统时间，需要相应的权限
                        * */
                        /*
                        String command = "date -s " + "20160831.154211";
                        try {
                            Runtime.getRuntime().exec(command);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        */
                    }
                })
                .setNegativeButton(R.string.sys_date_time_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();

        onDateChanged(null, 0, 0, 0);
        return alertDialog;
    }

    private Calendar getCalendarByInintData(String initDateTime) {
        Calendar calendar = Calendar.getInstance();

        //将日期时间拆分成年 月 日 时 分 秒
        String date = splitString(initDateTime, "日", "index", "front"); // 日期
        String time = splitString(initDateTime, "日", "index", "back"); // 时间

        String yearStr = splitString(date, "年", "index", "front"); // 年份
        String monthAndDay = splitString(date, "年", "index", "back"); // 月日

        String monthStr = splitString(monthAndDay, "月", "index", "front"); // 月
        String dayStr = splitString(monthAndDay, "月", "index", "back"); // 日

        String hourStr = splitString(time, ":", "index", "front"); // 时
        String minuteStr = splitString(time, ":", "index", "back"); // 分

        int currentYear = Integer.valueOf(yearStr.trim()).intValue();
        int currentMonth = Integer.valueOf(monthStr.trim()).intValue() - 1;
        int currentDay = Integer.valueOf(dayStr.trim()).intValue();
        int currentHour = Integer.valueOf(hourStr.trim()).intValue();
        int currentMinute = Integer.valueOf(minuteStr.trim()).intValue();

        calendar.set(currentYear, currentMonth, currentDay, currentHour,
                currentMinute);
        return calendar;
    }

    public static String splitString(String srcStr, String pattern,
                                     String indexOrLast, String frontOrBack) {
        String result = "";
        int loc = -1;

        if (indexOrLast.equalsIgnoreCase("index")) {
            loc = srcStr.indexOf(pattern); // 取得字符串第一次出现的位置
        } else {
            loc = srcStr.lastIndexOf(pattern); // 最后一个匹配串的位置
        }
        if (frontOrBack.equalsIgnoreCase("front")) {
            if (loc != -1)
                result = srcStr.substring(0, loc); // 截取子串
        } else {
            if (loc != -1)
                result = srcStr.substring(loc + 1, srcStr.length()); // 截取子串
        }
        return result;
    }

    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        onDateChanged(null, 0, 0, 0);
    }

    public void onDateChanged(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();

        calendar.set(datePicker.getYear(), datePicker.getMonth(),
                datePicker.getDayOfMonth(), timePicker.getCurrentHour(),
                timePicker.getCurrentMinute());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");

        dateTime = simpleDateFormat.format(calendar.getTime());
        alertDialog.setTitle(dateTime);
    }
}
