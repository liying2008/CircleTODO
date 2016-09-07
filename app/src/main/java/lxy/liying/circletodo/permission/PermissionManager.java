package lxy.liying.circletodo.permission;

import android.app.Activity;
import android.support.v4.app.ActivityCompat;

/**
 * =======================================================
 * 版权：©Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying
 * 日期：2016/8/4 12:17
 * 版本：1.0
 * 描述：Android系统隐私权限管理
 * 备注：
 * =======================================================
 */
public class PermissionManager {
    public static void grantPermission(Activity activity, String permission) {
        ActivityCompat.requestPermissions(activity, new String[]{permission}, 100);
    }
}
