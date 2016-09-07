package lxy.liying.circletodo.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import lxy.liying.circletodo.app.App;
import lxy.liying.circletodo.domain.SelectedColor;
import lxy.liying.circletodo.utils.Constants;

/**
 * =======================================================
 * 版权：©Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying
 * 日期：2016/7/24 13:04
 * 版本：1.0
 * 描述：数据库操作层——操作表 selected_color
 * 备注：
 * =======================================================
 */
public class SelectedColorService {
    private DatabaseHelper dbHelper;
    private String[] columns = {SelectedColor.UID, SelectedColor.C_TIME, SelectedColor.COLOR_ID, SelectedColor.COLOR_EVENT};

    /**
     * 构造方法
     *
     * @param context
     */
    public SelectedColorService(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    /**
     * 添加选择的颜色到数据库中
     *
     * @param catchTime SelectedColor对象中是否含有c_time属性
     * @param sc
     */
    public void addColor(SelectedColor sc, boolean catchTime) {
        SQLiteDatabase db = this.dbHelper.getWritableDatabase();
        String sql;
        if (catchTime) {
            sql = "INSERT INTO " + DBInfo.Table.TB_SELECT_COLOR + " VALUES ('" + App.CURRENT_UID + "', '" + sc.getC_time() + "', " + sc.getColor_id() + ", '" + sc.getColor_event() + "')";
        } else {
            sql = "INSERT INTO " + DBInfo.Table.TB_SELECT_COLOR + " VALUES ('" + App.CURRENT_UID + "', datetime('now','localtime'), " + sc.getColor_id() + ", '" + sc.getColor_event() + "')";
        }
        db.execSQL(sql);
//        System.out.println("addColor: " + sql);
        db.close();        //关闭数据库
    }

    /**
     * 根据colorId获得colorEvent
     *
     * @param colorId
     * @return
     */
    public String getColorEvent(int colorId) {
        SQLiteDatabase db = this.dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DBInfo.Table.TB_SELECT_COLOR, columns,
                SelectedColor.COLOR_ID + "=? AND " + SelectedColor.UID + "=?", new String[]{String.valueOf(colorId), App.CURRENT_UID}, null, null, null);

        cursor.moveToFirst();
        String colorEvent = null;
        if (cursor.getCount() > 0) {
            colorEvent = cursor.getString(cursor.getColumnIndex(SelectedColor.COLOR_EVENT));
        }
        cursor.close();
        db.close();
        return colorEvent;
    }

    /**
     * 根据colorId获得c_time(标记添加的时间)
     *
     * @param colorId
     * @return
     */
    public String getCTime(int colorId) {
        SQLiteDatabase db = this.dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DBInfo.Table.TB_SELECT_COLOR, columns,
                SelectedColor.COLOR_ID + "=? AND " + SelectedColor.UID + "=?", new String[]{String.valueOf(colorId), App.CURRENT_UID}, null, null, null);

        cursor.moveToFirst();
        String cTime = null;
        if (cursor.getCount() > 0) {
            cTime = cursor.getString(cursor.getColumnIndex(SelectedColor.C_TIME));
        }
        cursor.close();
        db.close();
        return cTime;
    }

    /**
     * 得到该用户所有已选择的颜色列表 <br/>
     * 按标记添加的时间由近到远排序 <br/>
     *
     * @return
     */
    public List<SelectedColor> findSelectedColors() {
        SQLiteDatabase db = this.dbHelper.getReadableDatabase();
        List<SelectedColor> lists = null;
        Cursor cursor = db.query(DBInfo.Table.TB_SELECT_COLOR, columns,
                SelectedColor.UID + "=?", new String[]{App.CURRENT_UID}, null, null, SelectedColor.C_TIME + " DESC");
        if (cursor.getCount() > 0) {
            lists = new ArrayList<>(cursor.getCount());
            SelectedColor selectedColor = null;
            while (cursor.moveToNext()) {
                selectedColor = new SelectedColor();
                String cTime = cursor.getString(cursor.getColumnIndex(SelectedColor.C_TIME));
                int colorId = cursor.getInt(cursor.getColumnIndex(SelectedColor.COLOR_ID));
                String colorEvent = cursor.getString(cursor.getColumnIndex(SelectedColor.COLOR_EVENT));

                selectedColor.setC_time(cTime);
                selectedColor.setColor_id(colorId);
                selectedColor.setColor_event(colorEvent);
                lists.add(selectedColor);
            }
            cursor.close();
        }
        db.close();
        return lists;
    }

    /**
     * 删除指定的颜色
     *
     * @param colorId
     */
    public void removeColor(int colorId) {
        SQLiteDatabase db = this.dbHelper.getWritableDatabase();
        String sql = "DELETE FROM " + DBInfo.Table.TB_SELECT_COLOR + " WHERE " + SelectedColor.UID + " = '" + App.CURRENT_UID + "' AND " + SelectedColor.COLOR_ID + " = " + colorId;
        db.execSQL(sql);
//        System.out.println("removeColor:" + sql);
        db.close();
    }

    /**
     * 更改标记名称
     *
     * @param colorId
     * @param newEvent
     */
    public void updateEvent(int colorId, String newEvent) {
        SQLiteDatabase db = this.dbHelper.getReadableDatabase();
        String sql = "UPDATE " + DBInfo.Table.TB_SELECT_COLOR + " SET " + SelectedColor.COLOR_EVENT + "='" + newEvent + "' ,  " + SelectedColor.C_TIME + "= datetime('now','localtime') WHERE " + SelectedColor.UID + " = '" + App.CURRENT_UID + "' AND " + SelectedColor.COLOR_ID + "=" + colorId;
        db.execSQL(sql);
//        System.out.println("updateEvent: " + sql);
        db.close();
    }

    /**
     * 更改标记添加时间
     *
     * @param colorId
     * @param newTime 格式必须是 0000-00-00 00:00:00 <br/>
     */
    public void updateCTime(int colorId, String newTime) {
        SQLiteDatabase db = this.dbHelper.getReadableDatabase();
        String sql = "UPDATE " + DBInfo.Table.TB_SELECT_COLOR + " SET " + SelectedColor.C_TIME + "='" + newTime + "' WHERE " + SelectedColor.UID + " = '" + App.CURRENT_UID + "' AND " + SelectedColor.COLOR_ID + "=" + colorId;
        db.execSQL(sql);
//        System.out.println("updateEvent: " + sql);
        db.close();
    }

    /**
     * 根据colorId得到SelectedColor对象
     *
     * @param colorIds
     * @return
     */
    public List<SelectedColor> getSelectedColorByIds(List<Integer> colorIds) {
        SQLiteDatabase db = this.dbHelper.getReadableDatabase();
        List<SelectedColor> lists = new ArrayList<>(Constants.DAY_MAX_MARK_NUM);
        int colorId;
        for (int i = 0; i < colorIds.size(); i++) {
            colorId = colorIds.get(i);
            String colorEvent = getColorEvent(colorId);
//            System.out.println(colorId + ", " + colorEvent);
            if (colorEvent != null) {
                lists.add(new SelectedColor(colorId, colorEvent));
            }
        }
        db.close();
        return lists;
    }

    /**
     * 更新ColorId
     *
     * @param oldColorId
     * @param newColorId
     */
    public void updateColorId(int oldColorId, int newColorId) {
        SQLiteDatabase db = this.dbHelper.getReadableDatabase();
        String sql = "UPDATE " + DBInfo.Table.TB_SELECT_COLOR + " SET " + SelectedColor.COLOR_ID + "=" + newColorId + " WHERE " + SelectedColor.UID + " = '" + App.CURRENT_UID + "' AND " + SelectedColor.COLOR_ID + "=" + oldColorId;
        db.execSQL(sql);
//        System.out.println("updateEvent: " + sql);
        db.close();
    }

    /**
     * 得到该用户所有的ColorId
     *
     * @return
     */
    public List<Integer> getAllColorIds() {
        SQLiteDatabase db = this.dbHelper.getReadableDatabase();
        List<Integer> colorIds = null;
        Cursor cursor = db.query(DBInfo.Table.TB_SELECT_COLOR, columns,
                SelectedColor.UID + "=?", new String[]{App.CURRENT_UID}, null, null, SelectedColor.C_TIME + " DESC");
        if (cursor.getCount() > 0) {
            colorIds = new ArrayList<>(cursor.getCount());
            while (cursor.moveToNext()) {
                int colorId = cursor.getInt(cursor.getColumnIndex(SelectedColor.COLOR_ID));
                colorIds.add(colorId);
            }
            cursor.close();
        }
        db.close();
        return colorIds;
    }
}
