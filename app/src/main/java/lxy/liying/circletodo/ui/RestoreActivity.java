package lxy.liying.circletodo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import java.io.File;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.QueryListener;
import lxy.liying.circletodo.R;
import lxy.liying.circletodo.app.App;
import lxy.liying.circletodo.db.DBInfo;
import lxy.liying.circletodo.domain.CircleUser;
import lxy.liying.circletodo.utils.Constants;
import lxy.liying.circletodo.utils.ErrorCodes;

public class RestoreActivity extends BaseActivity {
    private ProgressBar pbRestore;
    private Button btnRestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restore);
        btnRestore = (Button) findViewById(R.id.btnRestore);

        MobclickAgent.onEvent(this, Constants.UmengStatistics.RESTORE);
        pbRestore = (ProgressBar) findViewById(R.id.pbRestore);
    }

    /**
     * 从云端恢复
     *
     * @param view
     */
    public void restore(View view) {
        pbRestore.setVisibility(View.VISIBLE);   // 显示进度条
        btnRestore.setText("正在恢复……");
        btnRestore.setEnabled(false);
        downloadDBFile();
    }

    /**
     * 下载数据库文件
     *
     */
    private void downloadDBFile() {
        // 首先查询数据库备份的地址
        BmobQuery<CircleUser> query = new BmobQuery<CircleUser>();
        query.getObject(App.currentUser.getObjectId(), new QueryListener<CircleUser>() {

            @Override
            public void done(CircleUser object, BmobException e) {
                if(e == null){
                    // 获取数据库备份地址并更新当前CircleUser对象
                    String newDBBackupUrl = object.getDbBackupUrl();
                    App.currentUser.setDbBackupUrl(newDBBackupUrl);
                    if (TextUtils.isEmpty(newDBBackupUrl)) {
                        // 数据库备份地址为空，表示无备份文件
                        App.getInstance().showToast("没有查找到备份数据。");
                        pbRestore.setVisibility(View.GONE);
                        RestoreActivity.this.finish();   // 关闭页面
                        return;
                    }
                    // 构造BmobFile对象
                    BmobFile bmobfile = new BmobFile(RestoreActivity.this.getDatabasePath(DBInfo.DB_NAME).toString(), "", object.getDbBackupUrl());
                    download(bmobfile); // 开始下载
                }else{
//                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                    showErrorDialog(RestoreActivity.this, "很抱歉，从云端恢复失败。\n" +
                            ErrorCodes.errorMsg.get(e.getErrorCode()), true);
                    App.getInstance().showToast("您可以登录后再进行此操作。", Toast.LENGTH_LONG);
                    startActivityForResult(new Intent(RestoreActivity.this, LoginActivity.class), Constants.REQUEST_CODE);
                    overridePendingTransition(R.anim.push_left_in_ac, R.anim.push_left_out_ac);
                }
            }

        });
    }

    /**
     * 下载数据库文件
     * @param bmobfile
     */
    private void download(BmobFile bmobfile) {
        File saveFile = new File(bmobfile.getFilename());
        bmobfile.download(saveFile, new DownloadFileListener() {
            @Override
            public void onStart() {
//                toast("开始下载...");
                App.getInstance().showToast("正在从云端获取数据……");
            }

            @Override
            public void done(String savePath, BmobException e) {
                if (e == null) {
//                    System.out.println("下载成功,保存路径:" + savePath);
                    App.getInstance().showToast("恢复完成。");
                    pbRestore.setVisibility(View.GONE);
                    setResult(Constants.REFRESH_GRID_AND_MARK_MODE); // 通知GridView、每日标记显示刷新
                    RestoreActivity.this.finish();   // 关闭页面
                } else {
//                    toast("下载失败："+e.getErrorCode()+","+e.getMessage());
                    showErrorDialog(RestoreActivity.this, "很抱歉，从云端恢复失败。\n" +
                            ErrorCodes.errorMsg.get(e.getErrorCode()), true);
                }
            }

            @Override
            public void onProgress(Integer value, long newworkSpeed) {
//                Log.i("bmob","下载进度："+value+","+newworkSpeed);
                pbRestore.setProgress(value);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Constants.REFRESH_USER_INFO_CODE) {
            setResult(Constants.REFRESH_USER_INFO_CODE);
            finish();
        }
    }
}
