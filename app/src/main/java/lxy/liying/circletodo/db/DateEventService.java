package lxy.liying.circletodo.db;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import lxy.liying.circletodo.app.App;
import lxy.liying.circletodo.domain.DateEvent;
import lxy.liying.circletodo.utils.Constants;
import lxy.liying.circletodo.utils.StringUtils;

/**
 * =======================================================
 * 版权：©Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying
 * 日期：2016/7/24 13:07
 * 版本：1.0
 * 描述：数据库操作层——操作表 date_event
 * 备注：
 * =======================================================
 */
public class DateEventService {
    private DatabaseHelper dbHelper;
    private String[] columns = {DateEvent.D_ID, DateEvent.UID, DateEvent.E_DATE, DateEvent.COLOR_ID};
    /**
     * 升序
     */
    public static final String ORDER_ASC = "ASC";
    /**
     * 降序
     */
    public static final String ORDER_DESC = "DESC";

    /**
     * 构造方法
     *
     * @param context
     */
    public DateEventService(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    /**
     * 添加日期标记到数据库中 <br/>
     * d_id为时间戳，调用者不需要传入 <br/>
     * 日期将被格式化为“yyyy-MM-dd”格式 <br/>
     *
     * @param de
     */
    public void addDateEvent(DateEvent de) {
        SQLiteDatabase db = this.dbHelper.getWritableDatabase();
        String sql = "INSERT INTO " + DBInfo.Table.TB_DATE_EVENT + " VALUES (" + System.currentTimeMillis() + ",'" + App.CURRENT_UID + "','" + StringUtils.formatDate(de.getE_date()) + "', " + de.getColor_id() + ");";
        db.execSQL(sql);
//        System.out.println("addDateEvent:" + sql);
        db.close();
    }

    /**
     * 根据日期获得colorId
     *
     * @param date
     * @return
     */
    public List<Integer> getColorId(String date) {
        date = StringUtils.formatDate(date);
        SQLiteDatabase db = this.dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DBInfo.Table.TB_DATE_EVENT, columns,
                DateEvent.E_DATE + "=? AND " + DateEvent.UID + "=?", new String[]{date, App.CURRENT_UID}, null, null, null);

        List<Integer> colorIds = new ArrayList<>(Constants.DAY_MAX_MARK_NUM);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                int colorId = cursor.getInt(cursor.getColumnIndex(DateEvent.COLOR_ID));
                colorIds.add(colorId);
            }
            cursor.close();
        }
        db.close();
        return colorIds;
    }

    /**
     * 删除指定的标记
     *
     * @param date    日期
     * @param colorId ColorId
     */
    public void removeMark(String date, int colorId) {
        date = StringUtils.formatDate(date);
        SQLiteDatabase db = this.dbHelper.getWritableDatabase();
        String sql = "DELETE FROM " + DBInfo.Table.TB_DATE_EVENT + " WHERE " + DateEvent.UID + " = '" + App.CURRENT_UID + "' AND " + DateEvent.E_DATE + " = '" + date + "' AND " +
                DateEvent.COLOR_ID + " = " + colorId;
        db.execSQL(sql);
//        System.out.println("removeMark:" + sql);
        db.close();
    }

    /**
     * 删除指定日期的标记
     *
     * @param date 日期
     */
    public void removeMarksOfDate(String date) {
        date = StringUtils.formatDate(date);
        SQLiteDatabase db = this.dbHelper.getWritableDatabase();
        String sql = "DELETE FROM " + DBInfo.Table.TB_DATE_EVENT + " WHERE " + DateEvent.UID + " = '" + App.CURRENT_UID + "' AND " + DateEvent.E_DATE + " = '" + date + "'";
        db.execSQL(sql);
//        System.out.println("removeMarksOfDate:" + sql);
        db.close();
    }

    /**
     * 删除指定的标记
     *
     * @param d_id 添加标记时的时间戳
     */
    public void removeMark(long d_id) {
        SQLiteDatabase db = this.dbHelper.getWritableDatabase();
        String sql = "DELETE FROM " + DBInfo.Table.TB_DATE_EVENT + " WHERE " + DateEvent.UID + " = '" + App.CURRENT_UID + "' AND " + DateEvent.D_ID + " = " + d_id;
        db.execSQL(sql);
//        System.out.println("removeMark:" + sql);
        db.close();
    }

    /**
     * 删除所有该颜色的标记
     *
     * @param colorId
     */
    public void removeMarks(int colorId) {
        SQLiteDatabase db = this.dbHelper.getWritableDatabase();
        String sql = "DELETE FROM " + DBInfo.Table.TB_DATE_EVENT + " WHERE " +
                DateEvent.UID + " = '" + App.CURRENT_UID + "' AND " + DateEvent.COLOR_ID + " = " + colorId;
//        System.out.println("removeMarks:" + sql);
        db.execSQL(sql);
        db.close();
    }

    /**
     * 统计该颜色的标记数目
     *
     * @param colorId
     */
    public int marksCount(int colorId) {
        SQLiteDatabase db = this.dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DBInfo.Table.TB_DATE_EVENT, columns,
                DateEvent.UID + "=? AND " + DateEvent.COLOR_ID + "=? ", new String[]{App.CURRENT_UID, String.valueOf(colorId)}, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }

    /**
     * 删除该用户所有的标记 <br/>
     * 此方法删除数据时无法获取进度，暂时不用 <br/>
     */
    public void removeMarks() {
        SQLiteDatabase db = this.dbHelper.getWritableDatabase();
        String sql = "DELETE FROM " + DBInfo.Table.TB_DATE_EVENT + " WHERE " +
                DateEvent.UID + " = '" + App.CURRENT_UID + "'";
//        System.out.println("removeMarks:" + sql);
        db.execSQL(sql);
        db.close();
    }

    /**
     * 得到该用户所有日期标记 <br/>
     *
     * @param order 指定排序（可取值："ASC"：按日期从小到大排序；"DESC"：按日期从大到小排序）
     * @return
     */
    public List<DateEvent> findAllDateEvent(String order) {
        SQLiteDatabase db = this.dbHelper.getReadableDatabase();
        List<DateEvent> lists = null;
        Cursor cursor = db.query(DBInfo.Table.TB_DATE_EVENT, columns,
                DateEvent.UID + "=?", new String[]{App.CURRENT_UID}, null, null, DateEvent.E_DATE + " " + order);
        if (cursor.getCount() > 0) {
            lists = new ArrayList<>(cursor.getCount());
            DateEvent dateEvent = null;
            while (cursor.moveToNext()) {
                dateEvent = new DateEvent();
                long d_id = cursor.getLong(cursor.getColumnIndex(DateEvent.D_ID));
                String e_date = cursor.getString(cursor.getColumnIndex(DateEvent.E_DATE));
                int color_id = cursor.getInt(cursor.getColumnIndex(DateEvent.COLOR_ID));

                dateEvent.setD_id(d_id);
                dateEvent.setE_date(e_date);
                dateEvent.setColor_id(color_id);
                lists.add(dateEvent);
            }
            cursor.close();
        }
        db.close();
        return lists;
    }

    /**
     * 统计该用户所有标记的数目
     *
     * @return
     */
    public int getMarkCount() {
        SQLiteDatabase db = this.dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DBInfo.Table.TB_DATE_EVENT, columns,
                DateEvent.UID + "=?", new String[]{App.CURRENT_UID}, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }

    /**
     * 更新ColorId
     *
     * @param oldColorId
     * @param newColorId
     */
    public void updateColorId(int oldColorId, int newColorId) {
        SQLiteDatabase db = this.dbHelper.getReadableDatabase();
        String sql = "UPDATE " + DBInfo.Table.TB_DATE_EVENT + " SET " + DateEvent.COLOR_ID + "=" + newColorId + " WHERE " + DateEvent.UID + " = '" + App.CURRENT_UID + "' AND " + DateEvent.COLOR_ID + "=" + oldColorId;
        db.execSQL(sql);
//        System.out.println("updateEvent: " + sql);
        db.close();
    }
}
