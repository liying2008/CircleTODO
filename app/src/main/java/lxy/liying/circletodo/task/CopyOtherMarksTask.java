package lxy.liying.circletodo.task;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.util.List;

import lxy.liying.circletodo.app.App;
import lxy.liying.circletodo.db.DBInfo;
import lxy.liying.circletodo.domain.DateEvent;
import lxy.liying.circletodo.domain.SelectedColor;

/**
 * =======================================================
 * 版权：©Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying
 * 日期：2016/8/2 15:12
 * 版本：1.0
 * 描述：拷贝其他人的标记
 * 备注：
 * =======================================================
 */
public class CopyOtherMarksTask extends MyAsyncTask {
    private OnCopyOtherMarksListener listener;
    /** 颜色覆盖数目 */
    private int colorOverrideCount = 0;
    /** 颜色忽略数目 */
    private int colorIgnoreCount = 0;
    /** 标记覆盖数目 */
    private int markOverrideCount = 0;
    /** 标记忽略数目 */
    private int markIgnoreCount = 0;
    /**
     * 当前用户所有的colorId
     */
    private List<Integer> colorIds;

    public CopyOtherMarksTask(OnCopyOtherMarksListener listener) {
        this.listener = listener;

        // 覆盖、标记数目清零
        colorOverrideCount = 0;
        colorIgnoreCount = 0;
        markOverrideCount = 0;
        markIgnoreCount = 0;
    }

    /**
     * 复制对方的标记 <br/>
     *
     * @param params 对方的信息（params[0]:uid，params[1]:下载的数据库文件存储路径）
     * @return null
     */
    @Override
    protected Void doInBackground(String... params) {
        String uid = params[0];
        colorIds = App.scService.getAllColorIds();
        File tempDBFile = new File(params[1]);
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(tempDBFile, null);
        // 更新selected_color表
        updateSelectedColorTable(db, uid);
        // 更新date_event表
        updateDateEventTable(db, uid);
        db.close();
        return null;
    }

    /**
     * 更新selected_color表
     * @param db
     * @param uid
     */
    private void updateSelectedColorTable(SQLiteDatabase db, String uid) {
        String sql = "SELECT * FROM " + DBInfo.Table.TB_SELECT_COLOR + " WHERE " + SelectedColor.UID + "='" + uid + "'";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String cTime = cursor.getString(cursor.getColumnIndex(SelectedColor.C_TIME));
                int colorId = cursor.getInt(cursor.getColumnIndex(SelectedColor.COLOR_ID));
                String colorEvent = cursor.getString(cursor.getColumnIndex(SelectedColor.COLOR_EVENT));
                if (colorIds != null && colorIds.size() > 0) {
                    if (colorIds.contains(colorId)) {
                        // 重复的颜色
                        if (App.SHARE_CONFLICT == 0) {
                            // 覆盖
                            App.scService.updateEvent(colorId, colorEvent);
                            App.scService.updateCTime(colorId, cTime);
                            colorOverrideCount++;
                            int count = App.deService.marksCount(colorId);
                            markOverrideCount += count;
                        } else if (App.SHARE_CONFLICT == 1) {
                            // 忽略
                            colorIgnoreCount++;
                        }
                    } else {
                        App.scService.addColor(new SelectedColor(cTime, colorId, colorEvent), true);
                    }
                } else {
                    App.scService.addColor(new SelectedColor(cTime, colorId, colorEvent), true);
                }
            }
        }
        cursor.close();
    }

    /**
     * 更新date_event表
     */
    private void updateDateEventTable(SQLiteDatabase db, String uid) {
        String sql = "SELECT * FROM " + DBInfo.Table.TB_DATE_EVENT + " WHERE " + SelectedColor.UID + "='" + uid + "'";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
//                long d_id = cursor.getLong(cursor.getColumnIndex(DateEvent.D_ID));
                String e_date = cursor.getString(cursor.getColumnIndex(DateEvent.E_DATE));
                int color_id = cursor.getInt(cursor.getColumnIndex(DateEvent.COLOR_ID));
                List<Integer> thisDateColorIds = App.deService.getColorId(e_date);
                if (thisDateColorIds.size() >= 10) {
                    // 日标记数已达最大
                    markIgnoreCount++;
                    continue;   // 执行下一次循环
                }
                if (thisDateColorIds.contains(color_id)) {
                    // 该日期含有此颜色标记
                    if (App.SHARE_CONFLICT == 0) {
                        // 覆盖原来的
                        markOverrideCount++;
                    } else if (App.SHARE_CONFLICT == 1) {
                        // 忽略新的
                        markIgnoreCount++;
                    }
                } else {
                    // 该日期不含有此颜色标记
                    App.deService.addDateEvent(new DateEvent(e_date, color_id));
                }
            }
        }
        cursor.close();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        listener.onProgress(values[0]);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        listener.onComplete(colorOverrideCount, colorIgnoreCount, markOverrideCount, markIgnoreCount);
    }
}