package com.softmicro.xpermission;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by wangshuainan on 2017/9/20.
 */

public class XPermission {

    private static int specialPermissNums = 1;
    private static List<String> SpecialPermissionLists = new ArrayList<>();

    public static boolean hasPermission(Context context, String... perms){
        return !PermissionsChecker.getInstance(context).isLacksPermissions(perms);
    }
    public static void requestPermission(Activity activity, String[] perms, PermissionCallBack callBack){
        if(!PermissionsChecker.getInstance(activity).isOverMarshmallow()){
            doExecuteSuccess(Arrays.asList(perms),callBack);
            return;
        }
        List<String> deniedPermissions = PermissionsChecker.getInstance(activity).lacksPermissions(perms);
        if(deniedPermissions.size() > 0)
            doRequestPermission(activity,deniedPermissions.toArray(new String[deniedPermissions.size()]));
        else
            doExecuteSuccess(Arrays.asList(perms),callBack);
    }

    public static void requestPermission(Fragment fragment, String[] perms,PermissionCallBack callBack){
        if(!PermissionsChecker.getInstance(fragment.getContext()).isOverMarshmallow())
            doExecuteSuccess(Arrays.asList(perms),callBack);
        List<String> deniedPermissions = PermissionsChecker.getInstance(fragment.getContext()).lacksPermissions(perms);
        if(deniedPermissions.size() > 0)
            doRequestPermission(fragment,perms);
        else
            doExecuteSuccess(Arrays.asList(perms),callBack);
    }

    public static void requestSpecialPermission(Activity activity, String[] perms, SpecialPermissionCallBack callback){
        if(!PermissionsChecker.getInstance(activity).isOverMarshmallow()){
            doExecuteSpecialSuccess(Arrays.asList(perms),callback);
            return;
        }
        List<String> deniedPermissions = PermissionsChecker.getInstance(activity).lacksPermissions(perms);
        if(deniedPermissions.size() > 0)
            doRequestSpecialPermission(activity,deniedPermissions.toArray(new String[deniedPermissions.size()]),callback);
        else
            doExecuteSpecialSuccess(Arrays.asList(perms),callback);
    }

    public static void requestSpecialPermission(Fragment fragment, String[] perms, SpecialPermissionCallBack callback){
        if(!PermissionsChecker.getInstance(fragment.getContext()).isOverMarshmallow()){
            doExecuteSpecialSuccess(Arrays.asList(perms),callback);
            return;
        }
        List<String> deniedPermissions = PermissionsChecker.getInstance(fragment.getContext()).lacksPermissions(perms);
        if(deniedPermissions.size() > 0)
            doRequestSpecialPermission(fragment,deniedPermissions.toArray(new String[deniedPermissions.size()]),callback);
        else
            doExecuteSpecialSuccess(Arrays.asList(perms),callback);
    }

    private static void doRequestPermission(Object context, String... perms){

        if(context instanceof Activity)
            ActivityCompat.requestPermissions((Activity)context, perms, PermissionConstants.REQUEST_CODE_DANGER_PERMISSION_IN_ACTIVITY);
        else if(context instanceof Fragment)
            ((Fragment) context).requestPermissions(perms,PermissionConstants.REQUEST_CODE_DANGER_PERMISSION_IN_FRAGMENT);
        else
            throw new IllegalArgumentException(context.getClass().getName() + " is not supported!");
    }

    private static void doRequestSpecialPermission(Object context, String[] perms,SpecialPermissionCallBack callback){
        if(context instanceof Activity)
            requestSpecialPermission(context,new ArrayList<>(Arrays.asList(perms)),callback);
        else if(context instanceof Fragment)
            requestSpecialPermission(context,new ArrayList<>(Arrays.asList(perms)),callback);
        else
            throw new IllegalArgumentException(context.getClass().getName() + " is not supported!");
    }


    private static void requestSpecialPermission(Object receiver, List<String> specialPermissionLists, SpecialPermissionCallBack callback){
        if(specialPermissionLists.size() == 0)
            callback.onSpecialPermissionsGranted(specialPermissionLists);

        SpecialPermissionLists = specialPermissionLists;

        for(String specialpermission : specialPermissionLists){
            if(specialpermission.equals(Manifest.permission.SYSTEM_ALERT_WINDOW))
                requestAlertWindow(receiver);
            if(specialpermission.equals(Manifest.permission.WRITE_SETTINGS))
                requestWriteSettings(receiver);
        }
    }

    private static void requestWriteSettings(Object receiver) {
        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
        if(receiver instanceof Activity){
            intent.setData(Uri.parse("package:" + ((Activity) receiver).getPackageName()));
            ((Activity)receiver).startActivityForResult(intent, PermissionConstants.REQUEST_CODE_WRITE_SETTINGS );
        }else if(receiver instanceof Fragment){
            intent.setData(Uri.parse("package:" + ((Fragment)receiver).getActivity().getPackageName()));
            ((Fragment)receiver).startActivityForResult(intent, PermissionConstants.REQUEST_CODE_WRITE_SETTINGS );
        }

    }

    @TargetApi(Build.VERSION_CODES.M)
    private static void requestAlertWindow(Object receiver){
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        if(receiver instanceof Activity){
            intent.setData(Uri.parse("package:" + ((Activity) receiver).getPackageName()));
            ((Activity)receiver).startActivityForResult(intent, PermissionConstants.REQUEST_CODE_ALERT_WINDOW );
        }else if(receiver instanceof Fragment){
            intent.setData(Uri.parse("package:" + ((Fragment)receiver).getActivity().getPackageName()));
            ((Fragment)receiver).startActivityForResult(intent, PermissionConstants.REQUEST_CODE_ALERT_WINDOW );
        }
    }

    public static void onRequestPermissionsResult(int requestCode,
                                                  @NonNull String[] permissions,
                                                  @NonNull int[] grantResults,
                                                  @NonNull Object receiver,
                                                  @NonNull PermissionCallBack callBack) {
        Activity activity;
        if(receiver instanceof Fragment)
            activity = ((Fragment) receiver).getActivity();
        else if(receiver instanceof Activity)
            activity = (Activity) receiver;
        else
            throw new IllegalArgumentException(receiver.getClass().getName() + " is not supported!");

        // Make a collection of granted and denied permissions from the request.
        List<String> granted = new ArrayList<>();
        List<String> reasoned = new ArrayList<>();
        List<String> denied = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            String perm = permissions[i];
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                granted.add(perm);
            } else {
                denied.add(perm);
                //普通的有没通过的
                if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, perm) ){
                    reasoned.add(perm);
                }
            }
        }

        if(denied.size() > 0){
            doExecuteFail(denied,callBack);
            if(reasoned.size() > 0)
                doExecuteReason(reasoned,callBack);
        }
        else
            doExecuteSuccess(granted,callBack);

    }


    public static void onRequestSpecialPermissionResult(Context context, int requestCode, SpecialPermissionCallBack callback){
        if(SpecialPermissionLists.size() == specialPermissNums){
            List<String> lackPermissionLists = PermissionsChecker.getInstance(context).lacksPermissions(SpecialPermissionLists.toArray(new String[SpecialPermissionLists.size()]));
            if(lackPermissionLists.size() == 0){
                callback.onSpecialPermissionsGranted(SpecialPermissionLists);
            }else
                callback.onSpecialPermissionsDenied(SpecialPermissionLists);
            specialPermissNums = 1;
        }else
            specialPermissNums++;

        SpecialPermissionLists.clear();
    }


    private static void doExecuteSuccess(List<String> perms, PermissionCallBack callback){
        //success callback
        callback.onPermissionsGranted(perms);
    }

    private static void doExecuteReason(List<String> perms, PermissionCallBack callback){
        //reason callback
        callback.onPermissionsReasoned(perms);
    }

    private static void doExecuteFail(List<String> perms, PermissionCallBack callback){
        //fail callback
        callback.onPermissionsDenied(perms);
    }

    private static void doExecuteSpecialSuccess(List<String> perms, SpecialPermissionCallBack callback){
        //success callback
        callback.onSpecialPermissionsGranted(perms);
    }

    private static void doExecuteSpecialFail(List<String> perms, SpecialPermissionCallBack callback){
        //success callback
        callback.onSpecialPermissionsDenied(perms);
    }

    public static void showReason(final Context context, List<String> perms){

        new AlertDialog.Builder(context)
                .setMessage("需要开启"+perms.toString()+"才能使用此功能")
                .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //引导用户到设置中去进行设置
                        Intent intent = new Intent();
                        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                        intent.setData(Uri.fromParts("package", context.getPackageName(), null));
                        context.startActivity(intent);

                    }
                })
                .setNegativeButton("取消", null)
                .create()
                .show();
    }

}
