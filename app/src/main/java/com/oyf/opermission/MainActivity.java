package com.oyf.opermission;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.oyf.permission.annotation.PermissionDenied;
import com.oyf.permission.annotation.PermissionDeniedForever;
import com.oyf.permission.annotation.PermissionNeed;
import com.oyf.permission.utils.PermissionUtil;

public class MainActivity extends AppCompatActivity {

    PermissionUtilTest mPermissionUtilTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public String[] strings = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA
    };

    public void click(View view) {
        //检查权限是否已经允许了
        if (PermissionUtil.isGranted(this, strings)) {
            Log.d("test", "权限都有");
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //requestPermissions(strings, requestCode);
            Log.d("test", "click");
            permission();
        }
    }

    @PermissionNeed(permissions = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA
    }, requestCode = 100)
    public void permission() {
        Log.d("test", "权限申请");
    }

    @PermissionDenied
    public void denied(int requestCode) {
        Log.d("test", "拒绝permissions");
    }

    @PermissionDeniedForever
    public void deniedForever(int requestCode) {
        Log.d("test", "永久拒绝permissions");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("test", "requestcode=" + requestCode);
        for (int i = 0; i < permissions.length; i++) {
            if (PermissionUtil.shouldShowRequestPermissionRationale(this, permissions[i])) {
                Log.d("test", "永久拒绝permissions=" + permissions[i] + ",result=" + grantResults[i]);
            } else {
                Log.d("test", "拒绝permissions=" + permissions[i] + ",result=" + grantResults[i]);
            }
        }
    }


    public void clickFile(View view) {
        if (mPermissionUtilTest == null) {
            mPermissionUtilTest = new PermissionUtilTest();
        }
        mPermissionUtilTest.permission();
    }
}
