package lxy.liying.circletodo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ViewFlipper;

/**
 * =======================================================
 * 版权：©Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying
 * 日期：2016/8/12 12:27
 * 版本：1.0
 * 描述：自定义适应ScrollView的ViewFlipper
 * 备注：
 * =======================================================
 */
public class FlipperForScrollView extends ViewFlipper {
    public FlipperForScrollView(Context context) {
        super(context);
    }

    public FlipperForScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 重写该方法，达到使ViewFlipper适应ScrollView的效果
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
