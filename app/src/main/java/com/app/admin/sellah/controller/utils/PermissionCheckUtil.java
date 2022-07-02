package com.app.admin.sellah.controller.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

public class PermissionCheckUtil extends AppCompatActivity {

    public static final int PERMISSION_REQUEST_CODE = 123;
    private Context mContext;
    private onPermissionCheckCallback permissionCheckCallback;

    private PermissionCheckUtil(Context context, onPermissionCheckCallback callback) {
        this.mContext=context;
        this.permissionCheckCallback=callback;
        permissionsCheck();
    }

    public void permissionsCheck() {

        if (Build.VERSION.SDK_INT >= 23) {

            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA)
                            == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(mContext, Manifest.permission.RECORD_AUDIO)
                    == PackageManager.PERMISSION_GRANTED
             ) {
                permissionCheckCallback.onPermissionSuccess();

            } else {
                ActivityCompat.requestPermissions((Activity) mContext,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA,
                                Manifest.permission.RECORD_AUDIO}, PERMISSION_REQUEST_CODE);

            }

        } else {
            permissionCheckCallback.onPermissionSuccess();
        }

    }

    public static void create(Context context,onPermissionCheckCallback callback) {
        new PermissionCheckUtil(context,callback);
    }

    public interface onPermissionCheckCallback {
        void onPermissionSuccess();
    }

}
