package lxy.liying.circletodo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import java.util.List;

import lxy.liying.circletodo.R;
import lxy.liying.circletodo.adapter.StatisticsAdapter;
import lxy.liying.circletodo.app.App;
import lxy.liying.circletodo.domain.SelectedColor;
import lxy.liying.circletodo.task.OnStatisticsListener;
import lxy.liying.circletodo.task.StatisticsMarksTask;
import lxy.liying.circletodo.utils.Constants;

public class StatisticsActivity extends BaseActivity implements OnStatisticsListener {

    private ExpandableListView elvStatisticsMarks;
    private StatisticsAdapter adapter;
    private LinearLayout llProgress;
    private TextView tvStatisticsProgress, tvNotFound;
    private ProgressBar pbStatistics;
    private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        MobclickAgent.onEvent(this, Constants.UmengStatistics.STATISTICS_MARK);

        elvStatisticsMarks = (ExpandableListView) findViewById(R.id.elvStatisticsMarks);
        llProgress = (LinearLayout) findViewById(R.id.llProgress);
        tvStatisticsProgress = (TextView) findViewById(R.id.tvStatisticsProgress);
        tvNotFound = (TextView) findViewById(R.id.tvNotFound);
        pbStatistics = (ProgressBar) findViewById(R.id.pbStatistics);
        adapter = new StatisticsAdapter(this);

        initData();     // 初始化数据
    }

    /**
     * 初始化数据
     */
    private void initData() {
        Intent intent = getIntent();
        date = intent.getStringExtra("date");
        int scope = Integer.parseInt(App.getInstance().getSetting(Constants.STATISTICS_SCOPE, "0"));
        App.STATISTICS_SCOPE = scope;
        if (scope == 0) {
            // 本日后
            setTitle("统计标记 (" + date + "后)");
        } else if (scope == 1) {
            // 本日前
            setTitle("统计标记 (" + date + "前)");
        } else if (scope == 2) {
            // 所有
            setTitle("统计标记 (所有)");
        }

        StatisticsMarksTask task = new StatisticsMarksTask(this);
        task.execute(date);
    }

    @Override
    public void onProgress(int progress) {
        String msg = "正在统计标记  " + progress + "%";
        tvStatisticsProgress.setText(msg);
        pbStatistics.setProgress(progress);
    }

    @Override
    public void onComplete(List<String> dateList, List<List<SelectedColor>> selectColorList) {
        // 设置进度布局不可见
        llProgress.setVisibility(View.GONE);
        if (dateList.size() == 0) {
            // 没有当前日期后的标记
            tvNotFound.setText("没有找到 " + date + " 后的标记");
            tvNotFound.setVisibility(View.VISIBLE);
        } else {
            // 给Adapter设置数据
            adapter.setData(dateList, selectColorList);
            adapter.notifyDataSetChanged();
            elvStatisticsMarks.setAdapter(adapter);
            // 展开所有group
            for (int i = 0; i < adapter.getGroupCount(); i++) {
                elvStatisticsMarks.expandGroup(i);
            }
        }
    }
}
