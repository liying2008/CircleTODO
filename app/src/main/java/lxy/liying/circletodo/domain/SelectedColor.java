package lxy.liying.circletodo.domain;

/**
 * =======================================================
 * 版权：©Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying
 * 日期：2016/7/24 12:58
 * 版本：1.0
 * 描述：已选择的颜色信息类
 * 备注：
 * =======================================================
 */
public class SelectedColor {
    private String c_time;
    private int color_id;
    private String color_event;

    // 数据库表字段名
    public static final String UID = "uid";
    public static final String C_TIME = "c_time";
    public static final String COLOR_ID = "color_id";
    public static final String COLOR_EVENT = "color_event";

    public SelectedColor() {
    }

    public SelectedColor(int color_id, String color_event) {
        this.color_id = color_id;
        this.color_event = color_event;
    }

    public SelectedColor(String c_time, int color_id, String color_event) {
        this.c_time = c_time;
        this.color_id = color_id;
        this.color_event = color_event;
    }

    public String getC_time() {
        return c_time;
    }

    public void setC_time(String c_time) {
        this.c_time = c_time;
    }

    public int getColor_id() {
        return color_id;
    }

    public void setColor_id(int color_id) {
        this.color_id = color_id;
    }

    public String getColor_event() {
        return color_event;
    }

    public void setColor_event(String color_event) {
        this.color_event = color_event;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SelectedColor)) {
            return false;
        }
        SelectedColor sc = (SelectedColor) o;
        if (sc.getColor_id() == this.color_id && sc.getColor_event().equals(this.color_event)) {
            return true;
        }
        return false;
    }

    /**
     * 利用HashSet去重，务必重写hashCode()方法
     * @return
     */
    @Override
    public int hashCode() {
        int result;
        result = (color_event == null ? 0 : color_event.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "SelectedColor{" +
                "c_time='" + c_time + '\'' +
                ", color_id=" + color_id +
                ", color_event='" + color_event + '\'' +
                '}';
    }
}
