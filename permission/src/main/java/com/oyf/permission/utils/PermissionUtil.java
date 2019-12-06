package com.oyf.permission.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.aspectj.lang.ProceedingJoinPoint;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @创建者 oyf
 * @创建时间 2019/12/5 17:36
 * @描述
 **/
public class PermissionUtil {

    public static boolean isGranted(Context context, String... permissions) {
        for (String permission : permissions) {
            if (!isGranted(context, permission)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isGranted(Context context, String permission) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M
                || PackageManager.PERMISSION_GRANTED
                == ContextCompat.checkSelfPermission(context, permission);
    }

    /**
     * 检查所给权限List是否需要给提示，是否勾选不在拒绝
     *
     * @param activity    Activity
     * @param permissions 权限list
     * @return 如果某个权限需要提示则返回true
     */
    public static boolean shouldShowRequestPermissionRationale(Activity activity, String... permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                return true;
            }
        }
        return false;
    }

    private static Application mApplication;

    public static Application getApplication() {
        if (mApplication != null) {
            return mApplication;
        } else {
            mApplication = getApplicationByReflect();
        }
        return mApplication;
    }

    private static Application getApplicationByReflect() {
        try {
            Class<?> clazz = Class.forName("android.app.ActivityThread");
            Object currentActivityThread = clazz.getMethod("currentActivityThread").invoke(null);
            Object application = clazz.getMethod("getApplication").invoke(currentActivityThread);
            return (Application) application;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        throw new NullPointerException("application 为空");
    }


}
