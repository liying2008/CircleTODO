package lxy.liying.circletodo.operation;

import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import lxy.liying.circletodo.R;
import lxy.liying.circletodo.adapter.CalendarAdapter;
import lxy.liying.circletodo.adapter.YearAdapter;
import lxy.liying.circletodo.app.App;
import lxy.liying.circletodo.ui.CalendarActivity;

/**
 * =======================================================
 * 版权：©Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying
 * 日期：2016/8/15 19:46
 * 版本：1.0
 * 描述：年份选择器
 * 备注：
 * =======================================================
 */
public class YearSelector implements View.OnClickListener {

    private CalendarActivity activity;
    private CalendarAdapter calendarAdapter;
    private ViewFlipper flipper;
    private GridView gridView;
    private TextView currentMonth;

    public YearSelector(CalendarActivity activity, CalendarAdapter calendarAdapter, ViewFlipper flipper,
                        GridView gridView, TextView currentMonth) {
        this.activity = activity;
        this.calendarAdapter = calendarAdapter;
        this.flipper = flipper;
        this.gridView = gridView;
        this.currentMonth = currentMonth;
    }
    @Override
    public void onClick(View v) {
        AlertDialog.Builder builder = App.getAlertDialogBuilder(activity);
        View view = activity.getLayoutInflater().inflate(R.layout.dialog_year_list, null);
        ListView lvYearList = (ListView) view.findViewById(R.id.lvYearList);
        TextView tvGoCurrentMonth = (TextView) view.findViewById(R.id.tvGoCurrentMonth);
        YearAdapter adapter = new YearAdapter(activity);
        lvYearList.setAdapter(adapter);
        lvYearList.setSelection(activity.year_c - 1970);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.show();
        lvYearList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                flipper.removeAllViews();
                activity.year_c = position + 1970;
                activity.month_c = 1;
                CalendarActivity.jumpMonth = 0;
                calendarAdapter.setData(0, activity.year_c, activity.month_c);
                activity.addGridView();
                gridView.setAdapter(calendarAdapter);
                flipper.addView(gridView, 0);
                activity.addTextToTopTextView(currentMonth);
                dialog.dismiss();
            }
        });
        // 转到本月
        tvGoCurrentMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flipper.removeAllViews();
                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d", Locale.getDefault());
                CalendarActivity.currentDate = sdf.format(date); // 当期日期
                activity.year_c = Integer.parseInt(CalendarActivity.currentDate.split("-")[0]);
                activity.month_c = Integer.parseInt(CalendarActivity.currentDate.split("-")[1]);
                CalendarActivity.jumpMonth = 0;
                calendarAdapter.setData(0, activity.year_c, activity.month_c);
                activity.addGridView();
                gridView.setAdapter(calendarAdapter);
                flipper.addView(gridView, 0);
                activity.addTextToTopTextView(currentMonth);
                dialog.dismiss();
            }
        });
    }
}
