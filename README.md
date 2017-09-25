# XPermission
一个好用的安卓6.0动态权限封装


1. 支持申请权限组，可以无区分的一次申请所有所需权限，包括特殊权限，用户无需自己辨别；
2. 链式调用，一句申请，不需要去做过多的判断（版本及权限检查）；
3. 更轻量自由的选择：XPermission，里面支持多种申请方式，更适合你的方式。
4. 用户勾选拒绝的话，可以自己定义权限解释弹窗，向用户解释申请权限的理由；
5. 用户勾选不再提醒，弹窗引导用户去设置打开；

# 引用
```
compile 'com.softmicro.xpermission:xpermission:0.1.0'
```

# 使用介绍
在Activity中继承BasePermissionActivity或者在Fragment中继承BasePermissionFragment

```
doUnderPermissionTask(new String[]{Manifest.permission.CAMERA,Manifest.permission.CALL_PHONE,Manifest.permission.SYSTEM_ALERT_WINDOW,Manifest.permission.WRITE_SETTINGS}, new OnPermissionResult() {
            @Override
            public void onSuccess() {
                doCall();
            }

            @Override
            public void onFail() {
                Snackbar.make(MainActivity.this.getWindow().getDecorView(), "onfail", Snackbar.LENGTH_SHORT).show();
            }
        });
```

直接使用xpermission

```
//实现PermissionCallBack接口
public class CallFragment extends Fragment implements PermissionCallBack,SpecialPermissionCallBack
```

危险权限申请重写onRequestPermissionsResult，并将回调内容传给XPermission

```
@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PermissionConstants.REQUEST_CODE_DANGER_PERMISSION_IN_FRAGMENT)
            XPermission.onRequestPermissionsResult(requestCode, permissions, grantResults, CallFragment.this,this);
    }
```

特殊权限重写onActivityResult，并将回调内容传给XPermission

```
@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        XPermission.onRequestSpecialPermissionResult(this.getContext(),requestCode,this);
    }
```

权限申请回调接口：

```
@Override
    public void onPermissionsGranted(List<String> perms) {
        //Toast.makeText(getActivity(),"success in fragment",Toast.LENGTH_SHORT).show();
        //doCall();
    }

    @Override
    public void onPermissionsReasoned(List<String> perms) {
        Toast.makeText(getActivity(),"reason in fragment",Toast.LENGTH_SHORT).show();
        XPermission.showReason(getActivity(),perms);
    }

    @Override
    public void onPermissionsDenied(List<String> perms) {
        //Toast.makeText(getActivity(),"deny in fragment",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSpecialPermissionsGranted(List<String> perms) {
        Toast.makeText(getActivity(),"special permission grant in fragment",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSpecialPermissionsDenied(List<String> perms) {
        Toast.makeText(getActivity(),"special permission fail in fragment",Toast.LENGTH_SHORT).show();
    }
```

