package lxy.liying.circletodo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import lxy.liying.circletodo.R;

/**
 * =======================================================
 * 版权：©Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying
 * 日期：2016/8/4 21:42
 * 版本：1.0
 * 描述：年份列表适配器
 * 备注：
 * =======================================================
 */
public class YearAdapter extends BaseAdapter {
    private Context context;
    private int[] years = new int[2050 - 1970];

    public YearAdapter(Context context) {
        this.context = context;
        for (int i = 1970; i < 2050; i++) {
            years[i - 1970] = i;
        }
    }

    @Override
    public int getCount() {
        return years.length;
    }

    @Override
    public Integer getItem(int position) {
        return years[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater listContainer = LayoutInflater.from(context);
        ListItemView listItemView = null;
        if (convertView == null){
            listItemView = new ListItemView();
            //获取list_item布局文件的视图
            convertView = listContainer.inflate(R.layout.item_year, null);
            listItemView.tvYear = (TextView) convertView.findViewById(R.id.tvYear);

            //设置控件集到convertView
            convertView.setTag(listItemView);
        } else {
            listItemView = (ListItemView)convertView.getTag();
        }

        listItemView.tvYear.setText(String.valueOf(getItem(position)));
        return convertView;
    }

    /**
     * 自定义控件集合
     */
    public final class ListItemView {
        public TextView tvYear;
    }
}
