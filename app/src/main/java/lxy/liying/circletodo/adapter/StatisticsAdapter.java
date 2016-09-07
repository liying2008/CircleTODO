package lxy.liying.circletodo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import lxy.liying.circletodo.R;
import lxy.liying.circletodo.domain.SelectedColor;

/**
 * =======================================================
 * 版权：©Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying
 * 日期：2016/7/31 15:40
 * 版本：1.0
 * 描述：标记统计ExpandableList适配器
 * 备注：
 * =======================================================
 */
public class StatisticsAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> groupDates;
    private List<List<SelectedColor>> childMarks;

    /**
     * 构造方法
     */
    public StatisticsAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<String> groupDates, List<List<SelectedColor>> childMarks) {
        this.groupDates = groupDates;
        this.childMarks = childMarks;
    }
    @Override
    public int getGroupCount() {
        return groupDates.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return childMarks.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupDates.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return childMarks.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        LayoutInflater listContainer = LayoutInflater.from(context);
        ExListItemView listItemView = null;
        if (convertView == null){
            listItemView = new ExListItemView();
            convertView = listContainer.inflate(R.layout.item_statistics_date, null);
            listItemView.tvStatisticsDate = (TextView) convertView.findViewById(R.id.tvStatisticsDate);

            //设置控件集到convertView
            convertView.setTag(listItemView);
        } else {
            listItemView = (ExListItemView) convertView.getTag();
        }

        listItemView.tvStatisticsDate.setText(groupDates.get(groupPosition));
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        SelectedColor color = childMarks.get(groupPosition).get(childPosition);
        LayoutInflater listContainer = LayoutInflater.from(context);
        ExListItemView listItemView = null;
        if (convertView == null){
            listItemView = new ExListItemView();
            //获取list_item布局文件的视图
            convertView = listContainer.inflate(R.layout.item_statistics_mark, null);
            listItemView.tvStatisticsMark = (TextView) convertView.findViewById(R.id.tvStatisticsMark);
            listItemView.ivStatisticsColor = (CircleImageView) convertView.findViewById(R.id.ivStatisticsColor);
            //设置控件集到convertView
            convertView.setTag(listItemView);
        } else {
            listItemView = (ExListItemView) convertView.getTag();
        }

        listItemView.tvStatisticsMark.setText(color.getColor_event());
        listItemView.ivStatisticsColor.setImageResource(color.getColor_id());
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    /** 自定义控件集合 */
    public final class ExListItemView{
        public TextView tvStatisticsDate;
        public TextView tvStatisticsMark;
        public CircleImageView ivStatisticsColor;
    }

}
