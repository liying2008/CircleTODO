package lxy.liying.circletodo.db;

import lxy.liying.circletodo.domain.DateEvent;
import lxy.liying.circletodo.domain.SelectedColor;

/**
 * =======================================================
 * 版权：©Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying
 * 日期：2016/7/24 10:39
 * 版本：1.0
 * 描述：数据库常量信息
 * 备注：
 * =======================================================
 */
public class DBInfo {
    /**
     * 数据库名称
     */
    public static final String DB_NAME = "circle.db";
    /**
     * 数据库版本
     */
    public static final int DB_VERSION = 1;

    /**
     * 数据库表
     *
     * @author 李颖
     */
    public static class Table {
        public static final String TB_SELECT_COLOR = "selected_color";
        public static final String TB_DATE_EVENT = "date_event";
        public static final String TB_SELECT_COLOR_CREATE = "CREATE TABLE IF NOT EXISTS " + TB_SELECT_COLOR + " (" +
                SelectedColor.UID + " CHAR(32) NOT NULL," +
                SelectedColor.C_TIME + " TIMESTAMP NOT NULL, " +
                SelectedColor.COLOR_ID + " INTEGER," +
                SelectedColor.COLOR_EVENT + " VARCHAR(100)," +
                "PRIMARY KEY (" + SelectedColor.UID + ", " + SelectedColor.COLOR_ID + ")" +
                ");";
        public static final String TB_DATE_EVENT_CREATE = "CREATE TABLE IF NOT EXISTS " + TB_DATE_EVENT + " (" +
                DateEvent.D_ID + " BIGINT NOT NULL," +
                DateEvent.UID + " CHAR(32) NOT NULL," +
                DateEvent.E_DATE + " VARCHAR(50)," +
                DateEvent.COLOR_ID + " INTEGER," +
                "PRIMARY KEY (" + DateEvent.D_ID + ", " + DateEvent.UID + ")" +
                ");";
    }
}
