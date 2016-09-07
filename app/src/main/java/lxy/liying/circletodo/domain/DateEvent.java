package lxy.liying.circletodo.domain;

/**
 * =======================================================
 * 版权：©Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying
 * 日期：2016/7/24 12:58
 * 版本：1.0
 * 描述：日期事件实体类
 * 备注：
 * =======================================================
 */
public class DateEvent {
    private long d_id;
//    private String uid;
    private String e_date;
    private int color_id;

    // 数据库表字段名
    public static final String D_ID = "d_id";
    public static final String UID = "uid";
    public static final String E_DATE = "e_date";
    public static final String COLOR_ID = "color_id";

    public DateEvent() {
    }

    public DateEvent(String e_date, int color_id) {
        this.e_date = e_date;
        this.color_id = color_id;
    }

    public long getD_id() {
        return d_id;
    }

    public void setD_id(long d_id) {
        this.d_id = d_id;
    }

    public String getE_date() {
        return e_date;
    }

    public void setE_date(String e_date) {
        this.e_date = e_date;
    }

    public int getColor_id() {
        return color_id;
    }

    public void setColor_id(int color_id) {
        this.color_id = color_id;
    }

    @Override
    public String toString() {
        return "DateEvent{" +
                "d_id=" + d_id +
//                ", uid='" + uid + '\'' +
                ", e_date='" + e_date + '\'' +
                ", color_id=" + color_id +
                '}';
    }
}
