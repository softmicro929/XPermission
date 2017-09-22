package com.softmicro.xpermission;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by wangshuainan on 2017/9/13.
 */

public class BasePermissionActivity extends AppCompatActivity{

    private String TAG = "BasePermissionActivity";

    private int layoutResourceID;

    public Context context;

    private OnPermissionResult myOnPermissionResult;

    private List<String> specialPermissionLists = new ArrayList<>();
    private List<String> normalPermissionLists = new ArrayList<>();
    private int specialPermissNums = 1;



    private String[] specialPermissions = new String[]{
            Manifest.permission.SYSTEM_ALERT_WINDOW,
            Manifest.permission.WRITE_SETTINGS
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layoutResourceID);

        initData();
        initView();
        initEvent();
    }

    protected void setLayoutResourceID(int layoutResourceID, Context context){
        this.context = context;
        this.layoutResourceID = layoutResourceID;
    }

    public void initData() {
    }

    public void initView() {
    }

    public void initEvent() {
    }

    public void doUnderPermissionTask(String[] PERMISSIONS, OnPermissionResult onPermissionResult){
        myOnPermissionResult = onPermissionResult;
        List<String> lackPermissoinLists = PermissionsChecker.getInstance(context).lacksPermissions(PERMISSIONS);
        if(lackPermissoinLists.size() > 0){
            //缺权限
            specialPermissionLists.clear();
            normalPermissionLists.clear();
            for(String permission : lackPermissoinLists){
                if(permission.equals(Manifest.permission.SYSTEM_ALERT_WINDOW) || permission.equals(Manifest.permission.WRITE_SETTINGS))
                    specialPermissionLists.add(permission);
                else
                    normalPermissionLists.add(permission);
            }
            Log.v(TAG,"normalPermissionLists:"+normalPermissionLists.size() + "  specialPermissionLists:"+specialPermissionLists.size());
            if(normalPermissionLists.size() > 0 )
                ActivityCompat.requestPermissions(this, normalPermissionLists.toArray(new String[normalPermissionLists.size()]), PermissionConstants.REQUEST_CODE_DANGER_PERMISSION_IN_ACTIVITY);
            else
                requestSpecialPermission(specialPermissionLists);
        }else{
            myOnPermissionResult.onSuccess();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case PermissionConstants.REQUEST_CODE_DANGER_PERMISSION_IN_ACTIVITY:
                if (grantResults.length > 0) {

                    for(int i=0;i<grantResults.length;i++){

                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            //普通的有没通过的
                            //Snackbar.make(this.getWindow().getDecorView(), "有权限被拒绝", Snackbar.LENGTH_SHORT).show();
                            Log.v(TAG, "permission:" + ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE));
                            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) {
                                //Snackbar.make(this.getWindow().getDecorView(), "权限被永久拒绝，你要去设置打开权限", Snackbar.LENGTH_SHORT).show();
                                //ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CALL_PHONE},1);
                                new AlertDialog.Builder(this)
                                        .setMessage("需要开启权限才能使用此功能")
                                        .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                //引导用户到设置中去进行设置
                                                Intent intent = new Intent();
                                                intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                                                intent.setData(Uri.fromParts("package", getPackageName(), null));
                                                startActivity(intent);

                                            }
                                        })
                                        .setNegativeButton("取消", null)
                                        .create()
                                        .show();
                                return;
                            }
                            myOnPermissionResult.onFail();
                            return;
                        }else
                            Log.v(TAG,permissions[i]+":permission granted");
                    }
                    Log.v(TAG,"all normal permission granted,begin special permission");
                    //普通都通过了，继续特殊的
                    if(specialPermissionLists.size() == 0 || Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
                        myOnPermissionResult.onSuccess();
                    else {
                        requestSpecialPermission(specialPermissionLists);
                    }

                }else
                    myOnPermissionResult.onFail();

                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v(TAG,"onActivityResult");

        if(specialPermissionLists.size() == specialPermissNums){
            List<String> lackPermissionLists = PermissionsChecker.getInstance(context).lacksPermissions(specialPermissionLists.toArray(new String[specialPermissionLists.size()]));
            if(lackPermissionLists.size() == 0){
                myOnPermissionResult.onSuccess();
            }else
                myOnPermissionResult.onFail();
            specialPermissNums = 1;
        }else
            specialPermissNums++;

    }

    private void requestSpecialPermission(List<String> specialPermissionLists){
        Log.v(TAG,"request special permission");
        if(specialPermissionLists.size() == 0)
            myOnPermissionResult.onSuccess();
        for(String specialpermission : specialPermissionLists){
            if(specialpermission.equals(Manifest.permission.SYSTEM_ALERT_WINDOW))
                requestAlertWindow();
            if(specialpermission.equals(Manifest.permission.WRITE_SETTINGS))
                requestWriteSettings();
        }
    }

    private void requestWriteSettings() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, PermissionConstants.REQUEST_CODE_WRITE_SETTINGS );
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void requestAlertWindow(){
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, PermissionConstants.REQUEST_CODE_ALERT_WINDOW);
    }

    public interface OnPermissionResult{
        void onSuccess();
        void onFail();
    }
}
