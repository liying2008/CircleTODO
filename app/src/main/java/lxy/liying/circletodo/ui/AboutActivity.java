package lxy.liying.circletodo.ui;

import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.ActionSheetDialog;
import com.umeng.analytics.MobclickAgent;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.update.AppVersion;
import cn.bmob.v3.update.BmobUpdateAgent;
import lxy.liying.circletodo.R;
import lxy.liying.circletodo.app.App;
import lxy.liying.circletodo.utils.Constants;
import lxy.liying.circletodo.utils.ErrorCodes;

/**
 * =======================================================
 * 版权：©Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying
 * 日期：2016/7/25 15:27
 * 版本：1.0
 * 描述：关于界面
 * 备注：
 * =======================================================
 */
public class AboutActivity extends BaseActivity {

    /**
     * 作者微博地址
     */
    private static final String WEIBO_URL = "http://m.weibo.cn/d/neuliying";
    /**
     * 官方网站
     */
    private static final String OFFICIAL_WEBSITE = "http://duduhuo.cc/";
    /**
     * 作者邮箱地址
     */
    private static final String EMAIL = "liruoer2008@yeah.net";
    public static final String APP_VERSION_OBJECT_ID = "GMxw8885";
    private ImageView ivNewVersion; // "有更新"的图标
    /**
     * 当前是否是最新版本
     */
    private boolean isLatest = true;
    /**
     * 是否已检查过版本
     */
    private boolean isChecked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ivNewVersion = (ImageView) findViewById(R.id.ivNewVersion);

        actionBarNavigator();

         /* 版本显示文本 */
        TextView tvVersion = (TextView) findViewById(R.id.tvVersion);
        /** 显示版本 */
        String version = "v" + App.getInstance().getVersionName();
        tvVersion.setText(version);
        BmobQuery<AppVersion> query = new BmobQuery<AppVersion>();
        query.getObject(APP_VERSION_OBJECT_ID, new QueryListener<AppVersion>() {

            @Override
            public void done(AppVersion object, BmobException e) {
                if (e == null) {
                    int versionI = object.getVersion_i();
                    int versionCode = App.getInstance().getVersionCode();
                    if (versionI > versionCode) {
                        // 服务器上的版本更新
                        isLatest = false;
                        ivNewVersion.setVisibility(View.VISIBLE);
                    }
                } else {
//                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                }
                isChecked = true;
            }

        });
    }

    /**
     * 访问作者微博
     *
     * @param view
     */
    public void onWeiboClick(View view) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(WEIBO_URL));
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            App.getInstance().showToast(R.string.about_source_open_failed);
        }
    }

    /**
     * 检查更新
     *
     * @param view
     */
    public void onUpdateClick(View view) {
        // 检查更新
        BmobUpdateAgent.forceUpdate(this);
        MobclickAgent.onEvent(this, Constants.UmengStatistics.CHECK_UPDATE);
        if (isChecked && isLatest) {
            App.getInstance().showToast("当前版本已是最新版本。");
        } else if (!isChecked) {
            BmobQuery<AppVersion> query = new BmobQuery<AppVersion>();
            query.getObject(APP_VERSION_OBJECT_ID, new QueryListener<AppVersion>() {

                @Override
                public void done(AppVersion object, BmobException e) {
                    if (e == null) {
                        int versionI = object.getVersion_i();
                        int versionCode = App.getInstance().getVersionCode();
                        if (versionI <= versionCode) {
                            App.getInstance().showToast("当前版本已是最新版本。");
                        }
                    } else {
                        App.getInstance().showToast("检查更新失败，\n" + ErrorCodes.errorMsg.get(e.getErrorCode()));
                    }
                    isChecked = true;
                }
            });
        }
    }

    /**
     * 应用推荐
     *
     * @param view
     */
    public void onRecommendClick(View view) {
        MobclickAgent.onEvent(this, Constants.UmengStatistics.RECOMMEND);
        Intent intent = new Intent(this, RecommendActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.push_left_in_ac, R.anim.push_left_out_ac);
    }

    /**
     * 给作者发送邮件
     *
     * @param view
     */
    public void sendMail(View view) {
        final String[] stringItems = {"复制邮箱地址", "给作者发邮件"};
        final ActionSheetDialog dialog = new ActionSheetDialog(this, stringItems, null);
        dialog.title("选择操作")//
                .titleTextSize_SP(14.5f)//
                .show();

        dialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    // 复制邮箱地址
                    ClipboardManager cmbName = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clipDataName = ClipData.newPlainText(null, EMAIL);
                    cmbName.setPrimaryClip(clipDataName);
                    App.getInstance().showToast("邮箱地址已复制到剪贴板。");
                } else if (position == 1) {
                    // 发送邮件
                    try {
                        Intent data = new Intent(Intent.ACTION_SENDTO);
                        data.setData(Uri.parse("mailto:" + EMAIL));
                        data.putExtra(Intent.EXTRA_SUBJECT, "ABOUT CircleTODO");
                        data.putExtra(Intent.EXTRA_TEXT, "");
                        startActivity(data);
                    } catch (ActivityNotFoundException e) {
                        App.getInstance().showToast("您没有安装邮件类应用。");
                    }
                }
                dialog.dismiss();
            }
        });
    }

    /**
     * 访问官方下载地址
     * @param view
     */
    public void onOfficialWebsite(View view) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(OFFICIAL_WEBSITE));
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            App.getInstance().showToast(R.string.about_source_open_failed);
        }
    }
}
