package lxy.liying.circletodo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import lxy.liying.circletodo.R;
import lxy.liying.circletodo.utils.Constants;
import lxy.liying.circletodo.utils.DateMarkUtils;

/**
 * =======================================================
 * 版权：©Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying
 * 日期：2016/8/15 15:47
 * 版本：1.0
 * 描述：每日可选择标记列表对话框
 * 备注：
 * =======================================================
 */
public class RemainListActivity extends BaseActivity implements View.OnClickListener {

    private ListView lvDateMarkRemain;
    private TextView tvRemainMsg;
    private LinearLayout llAddColor;
    private String date;
    private DateMarkUtils dateMarkUtils;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remain_list);

        lvDateMarkRemain = (ListView) findViewById(R.id.lvDateMarkRemain);
        tvRemainMsg = (TextView) findViewById(R.id.tvRemainMsg);
        llAddColor = (LinearLayout) findViewById(R.id.llAddColor);

        llAddColor.setOnClickListener(this);

        Intent intent = getIntent();
        date = intent.getStringExtra("date");

        dateMarkUtils = new DateMarkUtils(null, lvDateMarkRemain, date, this, null, tvRemainMsg);
        dateMarkUtils.initRemainListView();
    }

    /**
     * 增加新颜色
     * @param v
     */
    @Override
    public void onClick(View v) {
        startActivityForResult(new Intent(this, MarkActivity.class), Constants.REQUEST_CODE);
        overridePendingTransition(R.anim.push_left_in_ac, R.anim.push_left_out_ac);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Constants.REFRESH_REMAIN_LIST || resultCode == Constants.REFRESH_GRID_AND_MARK_MODE) {
            if (dateMarkUtils != null) {
                dateMarkUtils = null;
            }
            dateMarkUtils = new DateMarkUtils(null, lvDateMarkRemain, date, this, null, tvRemainMsg);
            dateMarkUtils.initRemainListView();
        }
    }
}
