package lxy.liying.circletodo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * =============================================
 * 版权：李颖个人版权所有©2015
 * 作者：李颖
 * 日期：2015/9/2 19:32
 * 版本：1.0
 * 描述：自定义适应ScrollView的ListView
 * 备注：
 * =============================================
 */
public class ListViewForScrollView extends ListView {
    public ListViewForScrollView(Context context) {
        super(context);
    }

    public ListViewForScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ListViewForScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 重写该方法，达到使ListView适应ScrollView的效果
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
