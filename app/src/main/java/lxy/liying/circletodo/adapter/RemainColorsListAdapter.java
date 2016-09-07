package lxy.liying.circletodo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import lxy.liying.circletodo.R;
import lxy.liying.circletodo.domain.SelectedColor;

/**
 * =======================================================
 * 版权：©Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying
 * 日期：2016/7/29 15:37
 * 版本：1.0
 * 描述：给相应日期选择颜色对话框的列表适配器（已选择的颜色不显示）
 * 备注：
 * =======================================================
 */
public class RemainColorsListAdapter extends BaseAdapter {
    private Context mContext;
    private List<SelectedColor> mLists;

    // 构造方法
    public RemainColorsListAdapter(Context context, List<SelectedColor> lists) {
        this.mContext = context;
        if (null == lists) {
            mLists = new ArrayList<>(1);
        } else {
            this.mLists = lists;
        }
    }

    /**
     * 设置数据
     * @param lists
     */
    public void setData(List<SelectedColor> lists) {
        this.mLists = lists;
    }

    @Override
    public int getCount() {
        return mLists.size();
    }

    @Override
    public Object getItem(int position) {
        return mLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater listContainer = LayoutInflater.from(mContext);
        ListItemView listItemView = null;
        if (convertView == null){
            listItemView = new ListItemView();
            //获取list_item布局文件的视图
            convertView = listContainer.inflate(R.layout.item_dialog_color_list, null);
            listItemView.ivDialogColor = (CircleImageView) convertView.findViewById(R.id.ivDialogColor);
            listItemView.tvDialogSlEvent = (TextView) convertView.findViewById(R.id.tvDialogSlEvent);

            //设置控件集到convertView
            convertView.setTag(listItemView);
        } else {
            listItemView = (ListItemView)convertView.getTag();
        }

        SelectedColor selectedColor = mLists.get(position);
        listItemView.ivDialogColor.setImageResource(selectedColor.getColor_id());
        listItemView.tvDialogSlEvent.setText(selectedColor.getColor_event());
        return convertView;
    }

    /** 自定义控件集合 */
    public final class ListItemView{
        public CircleImageView ivDialogColor;
        public TextView tvDialogSlEvent;
    }

}
