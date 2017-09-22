package com.softmicro.xpermission;

import java.util.List;

/**
 * Created by wangshuainan on 2017/9/21.
 */

public interface PermissionCallBack {

    void onPermissionsGranted(List<String> perms);

    void onPermissionsReasoned(List<String> perms);

    void onPermissionsDenied(List<String> perms);
}
