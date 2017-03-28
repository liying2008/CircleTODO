package lxy.liying.circletodo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import lxy.liying.circletodo.R;
import lxy.liying.circletodo.adapter.MySpinnerAdapter;
import lxy.liying.circletodo.adapter.StatisticsAdapter;
import lxy.liying.circletodo.app.App;
import lxy.liying.circletodo.domain.SelectedColor;
import lxy.liying.circletodo.task.OnStatisticsListener;
import lxy.liying.circletodo.task.StatisticsMarksTask;
import lxy.liying.circletodo.utils.Constants;

public class StatisticsActivity extends BaseActivity implements OnStatisticsListener, AdapterView.OnItemSelectedListener {

    private ExpandableListView elvStatisticsMarks;
    private StatisticsAdapter adapter;
    private LinearLayout llProgress;
    private TextView tvStatisticsProgress, tvNotFound;
    private ProgressBar pbStatistics;
    private String date;
    private Spinner spinner;
    private TextView tvSelectColorMsg;
    private MySpinnerAdapter spinnerAdapter;
    private List<String> dateList;
    private List<List<SelectedColor>> selectColorList;
    private List<SelectedColor> selectedColors;

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
        tvSelectColorMsg = (TextView) findViewById(R.id.tvSelectColorMsg);
        spinner = (Spinner) findViewById(R.id.spinner);
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

        spinnerAdapter = new MySpinnerAdapter(this);
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
    public void onComplete(List<String> dateList, List<List<SelectedColor>> selectColorList, Set<SelectedColor> colorSet) {
        // 设置进度布局不可见
        llProgress.setVisibility(View.GONE);
        if (dateList.size() == 0) {
            // 没有当前日期后的标记
            tvNotFound.setText("没有找到 " + date + " 后的标记");
            tvNotFound.setVisibility(View.VISIBLE);
        } else {
            this.dateList = dateList;
            this.selectColorList = selectColorList;

            // 更新MySpinnerAdapter
            selectedColors = new ArrayList<>();
            selectedColors.add(new SelectedColor(-1, "全部"));
            selectedColors.addAll(colorSet);
            spinnerAdapter.setData(selectedColors);
            spinner.setAdapter(spinnerAdapter);
            spinner.setOnItemSelectedListener(this);
            tvSelectColorMsg.setText("选择标记：全部");

            // 给Adapter设置数据
            adapter.setData(dateList, selectColorList);
            adapter.notifyDataSetChanged();
            elvStatisticsMarks.setAdapter(adapter);
            // 展开所有group
            expandGroup();
        }
    }

    private void expandGroup() {
        for (int i = 0; i < adapter.getGroupCount(); i++) {
            elvStatisticsMarks.expandGroup(i);
        }
    }

    /**
     * 点击Spinner条目
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        SelectedColor selectedColor = selectedColors.get(position);
        int totle = 0;  // 选中的颜色总数
        List<String> dateList = new ArrayList<>();
        List<List<SelectedColor>> selectColorList = new ArrayList<>();
        if (selectedColor.getColor_id() == -1) {
            // 显示全部
            adapter.setData(this.dateList, this.selectColorList);
            adapter.notifyDataSetChanged();
            expandGroup();
            tvSelectColorMsg.setText("选择标记：全部");
        } else {
            // 显示单独分类
            for (int i = 0; i < this.selectColorList.size(); i++) {
                boolean exist = checkExist(selectedColor, this.selectColorList.get(i));
                if (exist) {
                    totle++;
                    dateList.add(this.dateList.get(i));
                    selectColorList.add(this.selectColorList.get(i));
                }
            }
            App.getInstance().showToast("共有 " + totle + " 组数据。");
            adapter.setData(dateList, selectColorList);
            adapter.notifyDataSetChanged();
            expandGroup();
            tvSelectColorMsg.setText("选择标记：" + selectedColor.getColor_event() + " [" + totle + "组数据]");
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /**
     * 检查该组下是否有选中的color
     */
    private boolean checkExist(SelectedColor selectedColor, List<SelectedColor> scList) {
        for (SelectedColor sc : scList) {
            if (sc.equals(selectedColor)) {
                return true;
            }
        }
        return false;
    }
}
