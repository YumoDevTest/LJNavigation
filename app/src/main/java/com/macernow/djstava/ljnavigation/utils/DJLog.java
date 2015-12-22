package com.macernow.djstava.ljnavigation.utils;

import android.util.Log;

import com.macernow.djstava.ljnavigation.BuildConfig;

/**
 * 包名称: com.macernow.djstava.ljnavigation.utils
 * 创建人: djstava
 * 创建时间: 15/10/12 下午2:51
 */
public class DJLog {
    private static String className;
    private static String methodName;
    private static int lineNumber;

    /*显示Verbose及以上的Log*/
    public static final int VERBOSE = 1;

    /*显示Debug及以上的Log*/
    public static final int DEBUG = 2;

    /*显示Info及以上的Log*/
    public static final int INFO = 3;

    /*显示WARN及以上的Log*/
    public static final int WARN = 4;

    /*显示Error及以上的Log*/
    public static final int ERROR = 5;

    /*不显示Log*/
    public static final int NOTHING = 6;

    /*控制显示的级别*/
    public static final int LEVEL = VERBOSE;

    private DJLog() {

    }

    public static boolean isDebuggable() {
        return BuildConfig.DEBUG;
    }

    private static String createLog(String log) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[");
        stringBuffer.append(methodName);
        stringBuffer.append(":");
        stringBuffer.append(lineNumber);
        stringBuffer.append("]");
        stringBuffer.append(log);

        return stringBuffer.toString();
    }

    private static void getMethodNames(StackTraceElement[] stackTraceElements) {
        className = stackTraceElements[1].getClassName();
        methodName = stackTraceElements[1].getMethodName();
        lineNumber = stackTraceElements[1].getLineNumber();
    }

    public static void v(String message) {
        if (!isDebuggable()) {
            return;
        }

        if (LEVEL <= VERBOSE) {
            getMethodNames(new Throwable().getStackTrace());
            Log.v(className, createLog(message));
        }
    }

    public static void d(String message) {
        if (!isDebuggable()) {
            return;
        }

        if (LEVEL <= DEBUG) {
            getMethodNames(new Throwable().getStackTrace());
            Log.d(className, createLog(message));
        }
    }

    public static void i(String message) {
        if (!isDebuggable()) {
            return;
        }

        if (LEVEL <= INFO) {
            getMethodNames(new Throwable().getStackTrace());
            Log.i(className, createLog(message));
        }
    }

    public static void w(String message) {
        if (!isDebuggable()) {
            return;
        }

        if (LEVEL <= WARN) {
            getMethodNames(new Throwable().getStackTrace());
            Log.w(className, createLog(message));
        }
    }

    public static void e(String message) {
        if (!isDebuggable()) {
            return;
        }

        if (LEVEL <= ERROR) {
            getMethodNames(new Throwable().getStackTrace());
            Log.e(className, createLog(message));
        }
    }
}
