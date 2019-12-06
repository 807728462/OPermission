package com.oyf.permission;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.oyf.permission.utils.PermissionUtil;

/**
 * @创建者 oyf
 * @创建时间 2019/12/5 17:24
 * @描述
 **/
public class PermissionAspectActivity extends AppCompatActivity {

    private final static String PERMISSIONS_KEY = "permissions";
    private final static String REQUESTCODE_KEY = "requestCode";

    private static PermissionCallback mCallback;
    private int requestCode = 0;

    public static void startActivity(Context context, String[] permissions, int requestCode, PermissionCallback callback) {
        if (context == null) return;
        mCallback = callback;
        Intent intent = new Intent(context, PermissionAspectActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);//开启新的任务栈并且清除栈顶...为何要清除栈顶
        intent.putExtra(PERMISSIONS_KEY, permissions);
        intent.putExtra(REQUESTCODE_KEY, requestCode);
        context.startActivity(intent);

        if (context instanceof Activity) {//并且，如果是activity启动的，那么还要屏蔽掉activity切换动画
            ((Activity) context).overridePendingTransition(0, 0);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();

        String[] stringExtra = intent.getStringArrayExtra(PERMISSIONS_KEY);
        requestCode = intent.getIntExtra(REQUESTCODE_KEY, 0);
        if (stringExtra == null) {
            mCallback.PermissionGranted(requestCode);
            finish();
        }
        //检查权限是否已经允许了
        if (PermissionUtil.isGranted(this, stringExtra)) {
            mCallback.PermissionGranted(requestCode);
            finish();
            overridePendingTransition(0, 0);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(stringExtra, requestCode);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //判断requestCode
        if (requestCode == this.requestCode) {
            boolean isAllGranted = true;
            //循环检测是否有拒绝的权限
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    isAllGranted = false;
                    break;
                }
            }
            if (isAllGranted) {
                if (mCallback != null) {
                    //全都允许  成功回调
                    mCallback.PermissionGranted(requestCode);
                }
            } else {
                //检查是否勾选了不在提醒
                if (PermissionUtil.shouldShowRequestPermissionRationale(this, permissions)) {
                    mCallback.onPermissionDenied(requestCode);
                } else {
                    mCallback.onPermissionDeniedForever(requestCode);
                }
            }

            finish();
            //取消动画
            overridePendingTransition(0, 0);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    public interface PermissionCallback {
        /**
         * 授予权限
         */
        void PermissionGranted(int requestCode);

        /**
         * 权限点击不在提示
         */
        void onPermissionDeniedForever(int code);

        /**
         * 权限拒绝
         */
        void onPermissionDenied(int code);
    }
}
