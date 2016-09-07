package lxy.liying.circletodo.app;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import lxy.liying.circletodo.db.DateEventService;
import lxy.liying.circletodo.db.SelectedColorService;
import lxy.liying.circletodo.domain.CircleUser;
import lxy.liying.circletodo.domain.SelectedColor;
import lxy.liying.circletodo.utils.Constants;

/**
 * =======================================================
 * 版权：©Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying
 * 日期：2016/7/24 19:48
 * 版本：1.0
 * 描述：Application
 * 备注：
 * =======================================================
 */
public class App extends Application {
    private Toast toast = null;  // 全局Toast
    /**
     * 全局已选择的颜色的列表
     */
    public static List<SelectedColor> mColorLists;
    /**
     * 已选择但未添加到日期上的颜色的列表
     */
    public static List<SelectedColor> mRemainColorLists;
    /**
     * 全局prefs
     */
    public static SharedPreferences prefs;
    /**
     * 当前Application实例
     */
    private static App mInstance;
    /**
     * 数据库操作类对象（操作selected_color表）
     */
    public static SelectedColorService scService;
    /**
     * 数据库操作类对象（操作date_event表）
     */
    public static DateEventService deService;
    /**
     * 当前用户ID
     */
    public static String CURRENT_UID = Constants.UNLOGIN_UID;
    /**
     * 当前用户
     */
    public static CircleUser currentUser;
    /** 当前用户头像 */
    public static Bitmap icon;
    /** 获取分享者的标记时的冲突解决策略 */
    public static int SHARE_CONFLICT = 0;
    /** 统计标记时的统计范围 */
    public static int STATISTICS_SCOPE = 0;
    /** 每日标记的显示方式 */
    public static int DISPLAY_MODE = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        /*
        实例化SharedPreferences
         */
        App.prefs = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        mInstance = this;
        // 场景类型设置
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        scService = new SelectedColorService(this);
        deService = new DateEventService(this);
        // 获取所有已选择的颜色
        getSelectedColors();

        // 检查工作目录
        checkWorkDir();
        // Bmob默认初始化
        Bmob.initialize(this, Constants.BMOB_APPID);
        // 获取缓存用户对象，更新当前用户信息
        CircleUser cUser = BmobUser.getCurrentUser(CircleUser.class);
        if (cUser != null) {
            App.currentUser = cUser;
            App.CURRENT_UID = cUser.getUid();
        }
    }

    /**
     * 检查工作目录是否存在，不存在则创建
     */
    private void checkWorkDir() {
        File dir = new File(Constants.HEAD_ICON_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /**
     * 得到本Application实例
     */
    public synchronized static App getInstance() {
        return mInstance;
    }

    /**
     * 查询数据库，得到所有已选择的颜色
     *
     * @return
     */
    public static List<SelectedColor> getSelectedColors() {
        List<SelectedColor> colors = scService.findSelectedColors();
        mColorLists = colors;
//        System.out.println("colors = " + colors);
        return colors;
    }

    /**
     * 获取设置
     *
     * @param key      设置key
     * @param defValue 没有该设置将要返回的默认值
     * @return
     */
    public String getSetting(String key, String defValue) {
        return prefs.getString(key, defValue);
    }


    /**
     * 保存设置，调用该方法后会产生
     *
     * @param key   设置保存key
     * @param value 需要保存的值
     */
    public void putSetting(String key, String value) {
        prefs.edit().putString(key, value).apply();
    }

    /**
     * 获取当前版本名称
     */
    public String getVersionName() {
        PackageManager packageManager = getPackageManager();
        PackageInfo packInfo;
        String version = "";
        try {
            packInfo = packageManager.getPackageInfo(getPackageName(), 0);
            version = packInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }

    /**
     * 获取当前版本号
     * @return
     */
    public int getVersionCode() {
        PackageManager packageManager = getPackageManager();
        PackageInfo packInfo;
        int versionCode = 0;
        try {
            packInfo = packageManager.getPackageInfo(getPackageName(), 0);
            versionCode = packInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * MyToast
     *
     * @param msg
     */
    public void showToast(String msg) {
        if (toast != null) {
            toast.cancel();
            toast = null;
        }
        toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * MyToast
     *
     * @param msg
     */
    public void showToast(String msg, int duration) {
        if (toast != null) {
            toast.cancel();
            toast = null;
        }
        toast = Toast.makeText(this, msg, duration);
        toast.show();
    }

    /**
     * MyToast
     *
     * @param resId
     */
    public void showToast(@StringRes int resId) {
        if (toast != null) {
            toast.cancel();
            toast = null;
        }
        toast = Toast.makeText(this, resId, Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * 得到全局Toast对象
     *
     * @return
     */
    public Toast getToast() {
        if (toast == null) {
            // 只是为了得到Toast对象，不需要调用show方法
            toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        }
        return toast;
    }

    /**
     * 得到对话框构建器实例
     *
     * @param context You need to use your Activity as the Context for the Dialog not the Application.
     * @return
     */
    public static AlertDialog.Builder getAlertDialogBuilder(Activity context) {
        return new AlertDialog.Builder(context);
    }
}
