package lxy.liying.circletodo.ui;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import lxy.liying.circletodo.R;
import lxy.liying.circletodo.app.App;
import lxy.liying.circletodo.task.OnRemoveMarksListener;
import lxy.liying.circletodo.task.RemoveMarksTask;
import lxy.liying.circletodo.utils.Constants;

/**
 * =======================================================
 * 版权：©Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying
 * 日期：2016/7/25 15:27
 * 版本：1.0
 * 描述：清理标记界面
 * 备注：
 * =======================================================
 */
public class CleanupActivity extends BaseActivity implements OnRemoveMarksListener {
    private Spinner spinner;
    private LinearLayout llProgress;
    private TextView tvCleanupProgress;
    private ProgressBar pbCleanup;
    private Button btnDatePicker;
    private int year, month, day;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cleanup);

        actionBarNavigator();
        btnDatePicker = (Button) findViewById(R.id.btnDatePicker);
        spinner = (Spinner) findViewById(R.id.spinner);
        llProgress = (LinearLayout) findViewById(R.id.llProgress);
        tvCleanupProgress = (TextView) findViewById(R.id.tvCleanupProgress);
        pbCleanup = (ProgressBar) findViewById(R.id.pbCleanup);
        //初始化Calendar日历对象
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        Date date = new Date(); //获取当前日期Date对象
        calendar.setTime(date);////为Calendar对象设置时间为当前日期

        year = calendar.get(Calendar.YEAR); //获取Calendar对象中的年
        month = calendar.get(Calendar.MONTH);//获取Calendar对象中的月
        day = calendar.get(Calendar.DAY_OF_MONTH);//获取这个月的第几天
        btnDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dpd = new DatePickerDialog(CleanupActivity.this, dateSetListener, year, month, day);
                dpd.show();//显示DatePickerDialog组件
            }
        });
    }

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        /**
         * params：view：该事件关联的组件
         * params：year：当前选择的年
         * params：monthOfYear：当前选择的月
         * params：dayOfMonth：当前选择的日
         */
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            //修改year、month、day的变量值，以便以后单击按钮时，DatePickerDialog上显示上一次修改后的值
            CleanupActivity.this.year = year;
            CleanupActivity.this.month = monthOfYear;
            CleanupActivity.this.day = dayOfMonth;
            //更新日期
            updateDate();

        }

        //当DatePickerDialog关闭时，更新日期显示
        private void updateDate() {
            // 更改Button上显示的日期
            String selectDate = year + "-" + (month + 1) + "-" + day;
            btnDatePicker.setText(selectDate);
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cleanup, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_cleanup_all_mark:
                cleanAllMarks();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * 清理指定日期内的标记
     *
     * @param view
     */
    public void cleanMarks(View view) {
        MobclickAgent.onEvent(this, Constants.UmengStatistics.CLEANUP_MARK);
        final String item = (String) spinner.getSelectedItem();
        final String date = year + "-" + (month + 1) + "-" + day;
        AlertDialog.Builder builder = App.getAlertDialogBuilder(this);
        builder.setTitle("删除提醒");
        builder.setMessage("确定要删除 " + date + " （不含当日）以" + item + "的所有标记？");
        builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 显示清理进度布局
                llProgress.setVisibility(View.VISIBLE);
                RemoveMarksTask task = new RemoveMarksTask(CleanupActivity.this);
                task.execute(date, item);
            }
        });
        builder.setNegativeButton("取消", null);
        builder.create().show();
    }

    /**
     * 清除该用户的所有标记
     */
    public void cleanAllMarks() {
        MobclickAgent.onEvent(this, Constants.UmengStatistics.CLEANUP_MARK);
        AlertDialog.Builder builder = App.getAlertDialogBuilder(this);
        builder.setTitle("删除提醒");
        builder.setMessage("确定要删除所有标记？");
        builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 显示清理进度布局
                llProgress.setVisibility(View.VISIBLE);
                RemoveMarksTask task = new RemoveMarksTask(CleanupActivity.this);
                task.execute("", "");   // 传入两个空字符串代表清理所有标记
            }
        });
        builder.setNegativeButton("取消", null);
        builder.create().show();
    }

    @Override
    public void onProgress(int progress) {
        String msg = "正在清理  " + progress + "%";
        tvCleanupProgress.setText(msg);
        pbCleanup.setProgress(progress);
    }

    @Override
    public void onComplete() {
        setResult(Constants.REFRESH_GRID_AND_MARK_MODE);
        // 设置进度布局不可见
        llProgress.setVisibility(View.GONE);
        App.getInstance().showToast("清理完成。");
    }
}
