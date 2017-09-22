package com.softmicro.xpermission;

import java.util.List;

/**
 * Created by wangshuainan on 2017/9/22.
 */

public interface SpecialPermissionCallBack {

    void onSpecialPermissionsGranted(List<String> perms);

    void onSpecialPermissionsDenied(List<String> perms);
}
