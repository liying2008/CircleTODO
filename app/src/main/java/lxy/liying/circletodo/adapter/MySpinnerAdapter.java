package lxy.liying.circletodo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import lxy.liying.circletodo.R;
import lxy.liying.circletodo.domain.SelectedColor;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/3/28 9:45
 * 版本：1.0
 * 描述：统计标记Spinner适配器
 * 备注：
 * =======================================================
 */
public class MySpinnerAdapter extends BaseAdapter {
    private Context context;
    private List<SelectedColor> selectedColors;

    /**
     * 构造方法
     */
    public MySpinnerAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<SelectedColor> selectedColors) {
        this.selectedColors = selectedColors;
    }

    @Override
    public int getCount() {
        return selectedColors.size();
    }

    @Override
    public Object getItem(int position) {
        return selectedColors.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SelectedColor color = selectedColors.get(position);
        LayoutInflater listContainer = LayoutInflater.from(context);
        ExListItemView listItemView = null;
        if (convertView == null) {
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
        if (color.getColor_id() != -1) {
            listItemView.ivStatisticsColor.setVisibility(View.VISIBLE);
            listItemView.ivStatisticsColor.setImageResource(color.getColor_id());
        } else {
            listItemView.ivStatisticsColor.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }

    /**
     * 自定义控件集合
     */
    public final class ExListItemView {
        public TextView tvStatisticsMark;
        public CircleImageView ivStatisticsColor;
    }
}
