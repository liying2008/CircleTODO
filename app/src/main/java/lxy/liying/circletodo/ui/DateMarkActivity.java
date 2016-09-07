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

public class DateMarkActivity extends BaseActivity implements View.OnClickListener {

    private DateMarkUtils dateMarkUtils;
    private ListView lvDateMarkSelected;
    private ListView lvDateMarkRemain;
    private TextView tvSelectMsg;
    private TextView tvRemainMsg;
    private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_mark);

        lvDateMarkSelected = (ListView) findViewById(R.id.lvDateMarkSelected);
        lvDateMarkRemain = (ListView) findViewById(R.id.lvDateMarkRemain);
        tvSelectMsg = (TextView) findViewById(R.id.tvSelectMsg);
        tvRemainMsg = (TextView) findViewById(R.id.tvRemainMsg);
        LinearLayout llAddColor = (LinearLayout) findViewById(R.id.llAddColor);
        llAddColor.setOnClickListener(this);

        Intent intent = getIntent();
        date = intent.getStringExtra("date");
        this.setTitle("当日标记 (" + date + ")");   // 设置Activity标题
        // 实例化一个“每日标记”工具类
        dateMarkUtils = new DateMarkUtils(lvDateMarkSelected, lvDateMarkRemain, date, this, tvSelectMsg, tvRemainMsg);
        dateMarkUtils.initSelectedListView();
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
            dateMarkUtils = new DateMarkUtils(lvDateMarkSelected, lvDateMarkRemain, date, this, tvSelectMsg, tvRemainMsg);
            dateMarkUtils.initSelectedListView();
            dateMarkUtils.initRemainListView();
        }
    }
}
