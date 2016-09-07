package lxy.liying.circletodo.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lxy.liying.circletodo.R;
import lxy.liying.circletodo.app.App;
import lxy.liying.circletodo.utils.LunarCalendar;
import lxy.liying.circletodo.utils.SpecialCalendar;

/**
 * 日历gridView中的每一个item显示的textView
 *
 * @author Vincent Lee、LiYing
 */
public class CalendarAdapter extends BaseAdapter {
    private Toast toast;
    private boolean isLeapyear = false; // 是否为闰年
    private int daysOfMonth = 0; // 某月的天数
    private int dayOfWeek = 0; // 具体某一天是星期几
    private int lastDaysOfMonth = 0; // 上一个月的总天数
    private Context context;
    private String[] dayNumber = new String[42]; // 一个gridview中的日期存入此数组中
    // private static String week[] = {"周日","周一","周二","周三","周四","周五","周六"};
    private SpecialCalendar sc = null;
    private LunarCalendar lc = null;

    private String currentYear = "";
    private String currentMonth = "";

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d", Locale.getDefault());
    private int currentFlag = -1; // 用于标记当天
    private int[] schDateTagFlag = null; // 存储当月所有的日程日期

    private String showYear = ""; // 用于在头部显示的年份
    private String showMonth = ""; // 用于在头部显示的月份
    private String animalsYear = "";
    private String leapMonth = ""; // 闰哪一个月
    private String cyclical = ""; // 天干地支
    // 系统当前时间
    private String sysDate = "";
    private String sys_year = "";
    private String sys_month = "";
    private String sys_day = "";

    public CalendarAdapter() {
        Date date = new Date();
        sysDate = sdf.format(date); // 当期日期
        sys_year = sysDate.split("-")[0];
        sys_month = sysDate.split("-")[1];
        sys_day = sysDate.split("-")[2];
    }

    /**
     * 构造方法
     * @param context
     * @param jumpMonth
     * @param year_c
     * @param month_c
     */
    public CalendarAdapter(Context context, Toast toast, int jumpMonth, int year_c, int month_c) {
        this();
        this.context = context;
        this.toast = toast;
        sc = new SpecialCalendar();
        lc = new LunarCalendar();
        // 设置数据
        setData(jumpMonth, year_c, month_c);
    }

    /**
     * 设置数据
     * @param jumpMonth
     * @param year_c
     * @param month_c
     */
    public void setData(int jumpMonth, int year_c, int month_c) {
        currentFlag = -1;   // 用于标记当天
        int stepYear;
        int stepMonth = month_c + jumpMonth;
        if (stepMonth > 0) {
            // 往下一个月滑动
            if (stepMonth % 12 == 0) {
                stepYear = year_c + stepMonth / 12 - 1;
                stepMonth = 12;
            } else {
                stepYear = year_c + stepMonth / 12;
                stepMonth = stepMonth % 12;
            }
        } else {
            // 往上一个月滑动
            stepYear = year_c - 1 + stepMonth / 12;
            stepMonth = stepMonth % 12 + 12;
            if (stepMonth % 12 == 0) {

            }
        }

        /**
         * 年份不允许大于等于2050，小于1900
         */
        if (stepYear >= 2050) {
            stepYear = 2049;
            stepMonth = 12;
            toast.setText("年份已到上界。");
            toast.show();
        } else if (stepYear < 1970) {
            stepYear = 1970;
            stepMonth = 1;
            toast.setText("年份已到下界。");
            toast.show();
        }

        currentYear = String.valueOf(stepYear); // 得到当前的年份
        currentMonth = String.valueOf(stepMonth); // 得到本月
        getCalendar(Integer.parseInt(currentYear), Integer.parseInt(currentMonth));
    }

    @Override
    public int getCount() {
        return dayNumber.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_calendar, null);
        }
        TextView textView = (TextView) convertView.findViewById(R.id.tvDate);

        String d = dayNumber[position].split("\\.")[0];
        String dv = dayNumber[position].split("\\.")[1];
//        System.out.println("d = " + d + ", dv = " + dv);
        String currentDate = currentYear + "-" + currentMonth + "-" + d;
        List<Integer> colorIds = App.deService.getColorId(currentDate);

        if (position < daysOfMonth + dayOfWeek && position >= dayOfWeek) {
            if (colorIds.size() > 0) {
                // 本月
                dv = "";
                for (int i = 0; i < colorIds.size(); i++) {
                    dv += "●";
                }
            }
        }

        SpannableString sp = new SpannableString(d + "\n" + dv);
        sp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, d.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        sp.setSpan(new RelativeSizeSpan(1.2f), 0, d.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        if (position < daysOfMonth + dayOfWeek && position >= dayOfWeek) {
            if (colorIds.size() > 0) {
                for (int i = 0; i < colorIds.size(); i++) {
                    sp.setSpan(new TextAppearanceSpan(context, R.style.dotStyle), d.length() + 1 + i, d.length() + 2 + i, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    sp.setSpan(new RelativeSizeSpan(1.0f), d.length() + 1 + i, d.length() + 2 + i, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    sp.setSpan(new ForegroundColorSpan(context.getResources().getColor(colorIds.get(i))), d.length() + 1 + i, d.length() + 2 + i, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            } else {
                sp.setSpan(new RelativeSizeSpan(0.75f), d.length() + 1, dayNumber[position].length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        } else {
            sp.setSpan(new RelativeSizeSpan(0.75f), d.length() + 1, dayNumber[position].length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
//        ===============================================================
        textView.setText(sp);
        textView.setTextColor(Color.GRAY);

        if (position < daysOfMonth + dayOfWeek && position >= dayOfWeek) {
            // 当前月信息显示
            textView.setTextColor(Color.BLACK);// 当月字体设黑
            if (position % 7 == 0 || position % 7 == 6) {
                // 当前月信息显示
                textView.setTextColor(Color.rgb(23, 126, 214));// 当月字体设黑
            }
        }

        if (currentFlag == position) {
            // 设置当天的背景
            textView.setBackgroundColor(Color.rgb(23, 126, 214));
            textView.setTextColor(Color.WHITE);
        }
        return convertView;
    }

    // 得到某年的某月的天数且这月的第一天是星期几

    public void getCalendar(int year, int month) {
        isLeapyear = sc.isLeapYear(year); // 是否为闰年
        daysOfMonth = sc.getDaysOfMonth(isLeapyear, month); // 某月的总天数
        dayOfWeek = sc.getWeekdayOfMonth(year, month); // 某月第一天为星期几
        lastDaysOfMonth = sc.getDaysOfMonth(isLeapyear, month - 1); // 上一个月的总天数
        Log.d("DAY", isLeapyear + " ======  " + daysOfMonth + "  ============  " + dayOfWeek + "  =========   " + lastDaysOfMonth);
        getWeek(year, month);
    }

    // 将一个月中的每一天的值添加入数组dayNuber中
    private void getWeek(int year, int month) {
        int j = 1;
        int flag = 0;
        String lunarDay = "";

        // 得到当前月的所有日程日期(这些日期需要标记)
        for (int i = 0; i < dayNumber.length; i++) {
            // 周一
            // if(i<7){
            // dayNumber[i]=week[i]+"."+" ";
            // }
            if (i < dayOfWeek) { // 前一个月
                int temp = lastDaysOfMonth - dayOfWeek + 1;
                lunarDay = lc.getLunarDate(year, month - 1, temp + i, false);
                dayNumber[i] = (temp + i) + "." + lunarDay;
//                System.out.println("前一：" + dayNumber[i]);

            } else if (i < daysOfMonth + dayOfWeek) { // 本月
                String day = String.valueOf(i - dayOfWeek + 1); // 得到的日期
                lunarDay = lc.getLunarDate(year, month, i - dayOfWeek + 1, false);
                dayNumber[i] = i - dayOfWeek + 1 + "." + lunarDay;
                // 对于当前月才去标记当前日期
                if (sys_year.equals(String.valueOf(year)) && sys_month.equals(String.valueOf(month)) && sys_day.equals(day)) {
                    // 标记当前日期
                    currentFlag = i;
                }
//                System.out.println("当前：" + dayNumber[i]);
                setShowYear(String.valueOf(year));
                setShowMonth(String.valueOf(month));
                setAnimalsYear(lc.animalsYear(year));
                setLeapMonth(lc.leapMonth == 0 ? "" : String.valueOf(lc.leapMonth));
                setCyclical(lc.cyclical(year));
            } else { // 下一个月
                lunarDay = lc.getLunarDate(year, month + 1, j, false);
                dayNumber[i] = j + "." + lunarDay;
                j++;
//                System.out.println("下一：" + dayNumber[i]);
            }
        }

//		String abc = "";
//		for (int i = 0; i < dayNumber.length; i++) {
//			abc = abc + dayNumber[i] + ":";
//		}
//		Log.d("DAYNUMBER", abc);
    }


    /**
     * 点击每一个item时返回item中的日期
     *
     * @param position
     * @return
     */
    public String getDateByClickItem(int position) {
        return dayNumber[position];
    }

    /**
     * 在点击gridView时，得到这个月中第一天的位置
     *
     * @return
     */
    public int getStartPositon() {
        return dayOfWeek + 7;
    }

    /**
     * 在点击gridView时，得到这个月中最后一天的位置
     *
     * @return
     */
    public int getEndPosition() {
        return (dayOfWeek + daysOfMonth + 7) - 1;
    }

    public String getShowYear() {
        return showYear;
    }

    public void setShowYear(String showYear) {
        this.showYear = showYear;
    }

    public String getShowMonth() {
        return showMonth;
    }

    public void setShowMonth(String showMonth) {
        this.showMonth = showMonth;
    }

    public String getAnimalsYear() {
        return animalsYear;
    }

    public void setAnimalsYear(String animalsYear) {
        this.animalsYear = animalsYear;
    }

    public String getLeapMonth() {
        return leapMonth;
    }

    public void setLeapMonth(String leapMonth) {
        this.leapMonth = leapMonth;
    }

    public String getCyclical() {
        return cyclical;
    }

    public void setCyclical(String cyclical) {
        this.cyclical = cyclical;
    }
}
