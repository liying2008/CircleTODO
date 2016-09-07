package lxy.liying.circletodo.utils;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import lxy.liying.circletodo.R;
import lxy.liying.circletodo.adapter.DateMarkSelectAdapter;
import lxy.liying.circletodo.adapter.RemainColorsListAdapter;
import lxy.liying.circletodo.app.App;
import lxy.liying.circletodo.domain.DateEvent;
import lxy.liying.circletodo.domain.SelectedColor;
import lxy.liying.circletodo.ui.BaseActivity;
import lxy.liying.circletodo.ui.CalendarActivity;
import lxy.liying.circletodo.ui.DateMarkActivity;
import lxy.liying.circletodo.ui.RemainListActivity;

/**
 * =======================================================
 * 版权：©Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying
 * 日期：2016/8/12 13:14
 * 版本：1.0
 * 描述：每日标记工具类
 * 备注：
 * =======================================================
 */
public class DateMarkUtils implements AdapterView.OnItemClickListener {
    private ListView lvDateMarkSelected, lvDateMarkRemain;
    private DateMarkSelectAdapter selectAdapter;
    private RemainColorsListAdapter remainAdapter;
    private String date;
    private BaseActivity activity;
    private TextView tvSelectMsg, tvRemainMsg;

    /**
     * “可选择列表数据”——差集列表
     */
    private List<SelectedColor> remainLists;
    /**
     * “已选择列表数据”
     */
    private List<SelectedColor> selectLists;

    /**
     * 构造方法
     *
     * @param lvDateMarkSelected
     * @param lvDateMarkRemain
     * @param date
     * @param activity
     * @param tvSelectMsg
     * @param tvRemainMsg
     */
    public DateMarkUtils(ListView lvDateMarkSelected, ListView lvDateMarkRemain, String date, BaseActivity activity,
                         TextView tvSelectMsg, TextView tvRemainMsg) {
        this.lvDateMarkSelected = lvDateMarkSelected;
        this.lvDateMarkRemain = lvDateMarkRemain;
        this.date = date;
        this.activity = activity;
        this.tvSelectMsg = tvSelectMsg;
        this.tvRemainMsg = tvRemainMsg;

        List<Integer> colorIds = App.deService.getColorId(date);
        selectLists = App.scService.getSelectedColorByIds(colorIds);
        selectAdapter = new DateMarkSelectAdapter(activity, selectLists, this);
        // 求两个List的差集
        remainLists = ListUtils.subtract(App.mColorLists, selectLists);
        remainAdapter = new RemainColorsListAdapter(activity, remainLists);
    }

    /**
     * 初始化可选择标记列表 <br/>
     */
    public void initRemainListView() {
        lvDateMarkRemain.setVisibility(View.VISIBLE);
        tvRemainMsg.setVisibility(View.GONE);
        lvDateMarkRemain.setAdapter(remainAdapter);
        lvDateMarkRemain.setOnItemClickListener(this);
        if (remainLists.size() == 0) {
            // 无可选择标记
            lvDateMarkRemain.setVisibility(View.GONE);
            tvRemainMsg.setText("没有可选择的标记");
            tvRemainMsg.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 初始化已选择标记列表
     */
    public void initSelectedListView() {
        lvDateMarkSelected.setVisibility(View.VISIBLE);
        tvSelectMsg.setVisibility(View.GONE);
        lvDateMarkSelected.setAdapter(selectAdapter);

        if (selectLists.size() == 0) {
            // 无已选择列表
            lvDateMarkSelected.setVisibility(View.GONE);
            tvSelectMsg.setText("没有已选择的标记");
            tvSelectMsg.setVisibility(View.VISIBLE);
        } else if (selectLists.size() >= Constants.DAY_MAX_MARK_NUM) {
            // 已选择列表已达最大值，隐藏可选择列表
            lvDateMarkRemain.setVisibility(View.GONE);
            tvRemainMsg.setText("日标记数已满");
            tvRemainMsg.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 删除一个标记
     *
     * @param colorId
     */
    public void delMark(int colorId) {
        // 可选择列表可见
        if (tvRemainMsg != null && lvDateMarkRemain != null) {
            tvRemainMsg.setVisibility(View.GONE);
            lvDateMarkRemain.setVisibility(View.VISIBLE);
        }

        App.deService.removeMark(date, colorId);
        List<Integer> colorIds = App.deService.getColorId(date);
        selectLists = App.scService.getSelectedColorByIds(colorIds);

        selectAdapter.setData(selectLists);
        selectAdapter.notifyDataSetChanged();
        if (selectLists.size() == 0) {
            lvDateMarkSelected.setVisibility(View.GONE);
            tvSelectMsg.setText("没有已选择的标记");
            tvSelectMsg.setVisibility(View.VISIBLE);
        }

        // 更新差集列表
        remainLists = ListUtils.subtract(App.mColorLists, selectLists);
        // 更新“可选择列表”
        remainAdapter.setData(remainLists);
        remainAdapter.notifyDataSetChanged();
        // 刷新界面
        if (activity instanceof CalendarActivity) {
            ((CalendarActivity) activity).refreshGridView();
        } else if (activity instanceof DateMarkActivity) {
            activity.setResult(Constants.REFRESH_VIEW_CODE);   // 刷新GridView
        }
    }

    /**
     * 点击ListView
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.lvDateMarkRemain) {
            // 点击当日可选择标记
            if (tvSelectMsg != null && lvDateMarkSelected != null) {
                tvSelectMsg.setVisibility(View.GONE);
                lvDateMarkSelected.setVisibility(View.VISIBLE);
            }
            // 添加到数据库中
            SelectedColor selectedColor = remainLists.get(position);
            App.deService.addDateEvent(new DateEvent(date, selectedColor.getColor_id()));
            remainAdapter.setData(App.mColorLists);

            // 更新“已选择列表”
            selectLists.add(selectedColor);
            selectAdapter.setData(selectLists);
            selectAdapter.notifyDataSetChanged();

            if (selectLists.size() >= Constants.DAY_MAX_MARK_NUM) {
                // 已选择列表已达最大值，隐藏可选择列表
                lvDateMarkRemain.setVisibility(View.GONE);
                tvRemainMsg.setText("日标记数已满");
                tvRemainMsg.setVisibility(View.VISIBLE);
            } else {
                // 更新差集列表
                remainLists = ListUtils.subtract(remainLists, selectedColor);
                if (remainLists.size() == 0) {
                    // 无可选择标记
                    lvDateMarkRemain.setVisibility(View.GONE);
                    tvRemainMsg.setText("没有可选择的标记");
                    tvRemainMsg.setVisibility(View.VISIBLE);
                } else {
                    // 更新“可选择列表”
                    remainAdapter.setData(remainLists);
                    remainAdapter.notifyDataSetChanged();
                }
            }
            // 更新界面
            if (activity instanceof DateMarkActivity) {
                activity.setResult(Constants.REFRESH_VIEW_CODE);   // 刷新GridView
            } else if (activity instanceof RemainListActivity) {
                activity.setResult(Constants.REFRESH_GRID_AND_MARK_MODE);   // 刷新GridView
            }
        }
    }
}
