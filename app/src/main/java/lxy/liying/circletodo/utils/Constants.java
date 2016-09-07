package lxy.liying.circletodo.utils;

import android.os.Environment;

import java.io.File;

/**
 * =======================================================
 * 版权：©Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying
 * 日期：2016/7/24 19:50
 * 版本：1.0
 * 描述：全局常量类
 * 备注：
 * =======================================================
 */
public class Constants {
    /** SharedPreferences的名字 */
    public static final String PREFS_NAME = "circle";
    /** SharedPreferences储存的版本号 */
    public static final String VERSION = "VERSION_CODE";
    /**
     * SharedPreferences储存的统计标记范围 <br/>
     * "0":本日后 <br/>
     * "1":本日前 <br/>
     * "2":所有 <br/>
     */
    public static final String STATISTICS_SCOPE= "STATISTICS_SCOPE";
    /**
     * SharedPreferences储存的冲突解决策略 <br/>
     * "0":覆盖已有标记 <br/>
     * "1":忽略新标记 <br/>
     */
    public static final String SHARE_CONFLICT = "SHARE_CONFLICT";
    /**
     * SharedPreferences储存的每日标记的显示方式 <br/>
     * "0":显示在界面底部 <br/>
     * "1":以弹窗方式显示 <br/>
     */
    public static final String DISPLAY_MODE = "DISPLAY_MODE";
    /** 日最大标记数 */
    public static final int DAY_MAX_MARK_NUM = 10;
    /**
     * ResultCode:刷新GridView
     */
    public static final int REFRESH_VIEW_CODE = 0;
    /**
     * ResultCode:刷新用户信息
     */
    public static final int REFRESH_USER_INFO_CODE = 1;
    /**
     * ResultCode:退出应用程序
     */
    public static final int EXIT_APP_CODE = 2;
    /**
     * ResultCode:刷新“每日标记”显示方式
     */
    public static final int DATE_MARK_MODE = 3;
    /**
     * ResultCode:刷新GridView并且刷新“每日标记”显示
     */
    public static final int REFRESH_GRID_AND_MARK_MODE = 4;
    /**
     * ResultCode:刷新可选择列表
     */
    public static final int REFRESH_REMAIN_LIST = 5;
    /**
     * RequestCode：定值0
     */
    public static final int REQUEST_CODE = 0;
    /** 未登录用户的ID */
    public static final String UNLOGIN_UID = "00000000000000000000000000000000";
    /** 头像宽度 */
    public static final int HEAD_WIDTH = 240;
    /** 头像高度 */
    public static final int HEAD_HEIGHT = 240;
    /** 加密和解密密钥 */
    public static final String SEED = "CIRCLETODO";
    /** 工作目录 */
    public static final String WORK_DIR = Environment.getExternalStorageDirectory() + File.separator + "CircleTODO";
    /** 分享生成的二维码图片的路径 */
    public static final String QR_FILE_NAME = WORK_DIR + File.separator + "my_marks.png";
    /** 头像存储的目录 */
    public static final String HEAD_ICON_DIR = WORK_DIR + File.separator + "head";
    /** 存储卡上的Pictures目录 */
    public static final String PICTURES_DIR = Environment.getExternalStorageDirectory() + File.separator + "Pictures";

    /** 微信分享的APP_ID */
    public static final String WX_APP_ID = "wxbe6752d561366894";
    /** 微博分享的APP_KEY */
    public static final String WB_APP_KEY = "4282571007";
    /** QQ享的APP_ID */
    public static final String QQ_APP_ID = "1105587828";
    public static final String BMOB_APPID = "2e4594300115428d20483420f5c5c862";

    /**
     * 友盟统计相关常量
     */
    public static class UmengStatistics{
        /** 备份标记到云端 */
        public static final String BACKUP = "backup";
        /** 检查更新 */
        public static final String CHECK_UPDATE = "check_update";
        /** 清理标记 */
        public static final String CLEANUP_MARK = "cleanup_mark";
        /** 个人中心 */
        public static final String PERSONAL = "personal";
        /** 注册 */
        public static final String REGISTER = "register";
        /** 从云端恢复标记 */
        public static final String RESTORE = "restore";
        /** 设置标记 */
        public static final String SET_MARK = "set_mark";
        /** 分享应用 */
        public static final String SHARE_APP = "share_app";
        /** 分享标记 */
        public static final String SHAER_MARK = "share_mark";
        /** 统计标记 */
        public static final String STATISTICS_MARK = "statistics_mark";
        /** 应用推荐 */
        public static final String RECOMMEND = "recommend";
    }

}
