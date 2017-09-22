package com.softmicro.xpermission;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangshuainan on 2017/8/6.
 */

public class PermissionsChecker {

    private String TAG = "PermissionsChecker";
    private final Context mContext;

    //singleInstance and thread safety
    private static volatile PermissionsChecker checker = null;

    public static PermissionsChecker getInstance(Context context){
        if(checker == null) {
            synchronized (PermissionsChecker.class) {
                if (checker == null)
                    checker = new PermissionsChecker(context);
            }
        }
        return checker;
    }

    public PermissionsChecker(Context context) {
        mContext = context.getApplicationContext();
    }

    // 缺少的权限集合
    public List<String> lacksPermissions(String... permissions) {
        List<String> lackPermissionLists = new ArrayList<>();
        for (String permission : permissions) {
            if (lacksPermission(permission)) {
                Log.v(TAG,"permission lack:"+permission);
                lackPermissionLists.add(permission);
            }
        }
        return lackPermissionLists;
    }

    //判断是否缺少权限集
    public boolean isLacksPermissions(String... permissions){
        for(String permission : permissions){
            if(lacksPermission(permission))
                return true;
        }
        return false;
    }

    // 判断是否缺少单个权限
    public boolean lacksPermission(String permission) {
        if(permission.equals(Manifest.permission.SYSTEM_ALERT_WINDOW)) {
            return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || !Settings.canDrawOverlays(mContext);
        }
        if(permission.equals(Manifest.permission.WRITE_SETTINGS)){
            return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.System.canWrite(mContext);
        }
        return ContextCompat.checkSelfPermission(mContext, permission) ==
                PackageManager.PERMISSION_DENIED;
    }

    public boolean isOverMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

}
