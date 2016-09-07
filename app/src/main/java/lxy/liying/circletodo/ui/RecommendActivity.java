package lxy.liying.circletodo.ui;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ListView;

import lxy.liying.circletodo.R;
import lxy.liying.circletodo.adapter.RecommendListAdapter;
import lxy.liying.circletodo.app.App;

/**
 * 应用推荐界面
 */
public class RecommendActivity extends BaseActivity {

    private static RecommendActivity recommendActivity;

    // 图标数组
    private static int[] icons = {R.drawable.xdp_512x512, R.drawable.ipgw_512x512, R.drawable.sec_512x512};
    // 软件名称数组
    private static int[] name = {R.string.recommend_name_xdp, R.string.recommend_name_ipgw, R.string.recommend_name_sec};
    // 软件描述数组
    private static int[] desc = {R.string.recommend_desc_xdp, R.string.recommend_desc_ipgw, R.string.recommend_desc_sec};
    // 软件包名
    public static String[] packageName = {"lxy.liying.hdtvneu", "com.liying.ipgw", "lxy.liying.secbook"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);

        actionBarNavigator();
        ListView lvRecommend = (ListView) findViewById(R.id.lvRecommend);
        RecommendListAdapter adapter = new RecommendListAdapter(this, icons, name, desc);
        lvRecommend.setAdapter(adapter);

        recommendActivity = this;
    }

    /**
     * 获取本Activity的实例
     * @return
     */
    public static RecommendActivity getInstance() {
        return recommendActivity;
    }

    /**
     * 跳到应用商店的下载页面
     * @param packageName 应用的包名
     */
    public void goToDownload(String packageName) {
        try {
            Uri uri = Uri.parse("market://details?id=" + packageName);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            App.getInstance().showToast("您没有安装应用市场类软件，无法打开应用下载页面。");
        }
    }
}
