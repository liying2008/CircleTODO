package lxy.liying.circletodo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import lxy.liying.circletodo.R;
import lxy.liying.circletodo.domain.SelectedColor;
import lxy.liying.circletodo.ui.MarkActivity;

/**
 * =======================================================
 * 版权：©Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying
 * 日期：2016/7/24 13:55
 * 版本：1.0
 * 描述：已选择的颜色的列表适配器
 * 备注：
 * =======================================================
 */
public class SelectedColorsListAdapter extends BaseAdapter {
    private Context mContext;
    private List<SelectedColor> mLists;

    // 构造方法
    public SelectedColorsListAdapter(Context context, List<SelectedColor> lists) {
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
            convertView = listContainer.inflate(R.layout.item_selected, null);
            listItemView.ivSelected = (CircleImageView) convertView.findViewById(R.id.ivSelected);
            listItemView.tvSlEvent = (TextView) convertView.findViewById(R.id.tvSlEvent);
            listItemView.tvSlTime = (TextView) convertView.findViewById(R.id.tvSlTime);
            listItemView.ivDelete = (ImageView) convertView.findViewById(R.id.ivDelete);

            //设置控件集到convertView
            convertView.setTag(listItemView);
        } else {
            listItemView = (ListItemView)convertView.getTag();
        }

        final SelectedColor selectedColor = mLists.get(position);
        listItemView.ivSelected.setImageResource(selectedColor.getColor_id());
        listItemView.tvSlEvent.setText(selectedColor.getColor_event());
        listItemView.tvSlTime.setText(selectedColor.getC_time());
        listItemView.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 删除颜色
                MarkActivity.getInstance().delColor(selectedColor.getColor_id());
            }
        });
        return convertView;
    }

    /** 自定义控件集合 */
    public final class ListItemView{
        public CircleImageView ivSelected;
        public TextView tvSlEvent, tvSlTime;
        public ImageView ivDelete;
    }
}
