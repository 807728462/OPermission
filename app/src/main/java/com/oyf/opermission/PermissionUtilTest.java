package com.oyf.opermission;

import android.Manifest;
import android.util.Log;

import com.oyf.permission.annotation.PermissionDenied;
import com.oyf.permission.annotation.PermissionDeniedForever;
import com.oyf.permission.annotation.PermissionNeed;

/**
 * @创建者 oyf
 * @创建时间 2019/12/6 14:23
 * @描述
 **/
public class PermissionUtilTest {
    @PermissionNeed(permissions = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA
    }, requestCode = 100)
    public void permission() {
        Log.d("test", "PermissionUtilTest权限申请");
    }

    @PermissionDenied
    public void denied(int requestCode) {
        Log.d("test", "PermissionUtilTest拒绝permissions");
    }

    @PermissionDeniedForever
    public void deniedForever(int requestCode) {
        Log.d("test", "PermissionUtilTest永久拒绝permissions");
    }
}
