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
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import lxy.liying.circletodo.R;
import lxy.liying.circletodo.app.App;
import lxy.liying.circletodo.db.DBInfo;
import lxy.liying.circletodo.domain.CircleUser;
import lxy.liying.circletodo.utils.Constants;
import lxy.liying.circletodo.utils.ErrorCodes;

public class BackupActivity extends BaseActivity {

    private ProgressBar pbBackup;
    private Button btnBackup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup);
        btnBackup = (Button) findViewById(R.id.btnBackup);

        MobclickAgent.onEvent(this, Constants.UmengStatistics.BACKUP);
        pbBackup = (ProgressBar) findViewById(R.id.pbBackup);
    }

    /**
     * 开始备份
     *
     * @param view
     */
    public void backup(View view) {
//        System.out.println(this.getDatabasePath(DBInfo.DB_NAME));   // 数据库文件路径
        pbBackup.setVisibility(View.VISIBLE);   // 显示进度条
        btnBackup.setText("正在备份……");
        btnBackup.setEnabled(false);
        uploadDBFile(this.getDatabasePath(DBInfo.DB_NAME));
    }

    /**
     * 上传数据库文件
     */
    private void uploadDBFile(File dbFile) {
        final BmobFile bmobFile = new BmobFile(dbFile);
        bmobFile.uploadblock(new UploadFileListener() {

            @Override
            public void done(BmobException e) {
                if (e == null) {
                    //bmobFile.getFileUrl()--返回的上传文件的完整地址
                    final String dbUrl = bmobFile.getFileUrl(); // getFileUrl()和getUrl()得到的结果是一样的
                    // 上传成功
                    // 如果已存在之前的数据库备份，则删除
                    // 获取之前备份的数据库的url
                    BmobQuery<CircleUser> query = new BmobQuery<CircleUser>();
                    query.getObject(App.currentUser.getObjectId(), new QueryListener<CircleUser>() {

                        @Override
                        public void done(CircleUser object, BmobException e) {
                            if (e == null) {
                                String oldBackupUrl = object.getDbBackupUrl();
                                if (!TextUtils.isEmpty(oldBackupUrl)) {
                                    // oldBackupUrl不为空
                                    BmobFile file = new BmobFile();
                                    file.setUrl(oldBackupUrl);//此url是上传文件成功之后通过bmobFile.getUrl()方法获取的。
                                    file.delete(new UpdateListener() {

                                        @Override
                                        public void done(BmobException e) {
                                            if (e == null) {
                                                System.out.println("文件删除成功");
                                            } else {
                                                System.out.println("文件删除失败：" + e.getErrorCode() + "," + e.getMessage());
                                            }

                                            // 更新Bmob _User表
                                            updateUserTable(dbUrl);
                                        }
                                    });
                                } else {
                                    // oldBackupUrl为空
                                    // 更新Bmob _User表
                                    updateUserTable(dbUrl);
                                }

                            } else {
                                showErrorDialog(BackupActivity.this, "很抱歉，备份失败，\n" +
                                        ErrorCodes.errorMsg.get(e.getErrorCode()), true);
                            }
                        }

                    });
                } else {
                    showErrorDialog(BackupActivity.this, "很抱歉，备份失败，\n" +
                            ErrorCodes.errorMsg.get(e.getErrorCode()), true);
                }
            }

            @Override
            public void onProgress(Integer value) {
                // 返回的上传进度（百分比）
//                System.out.println("数据库上传进度：" + value);
                pbBackup.setProgress(value);
            }
        });
    }

    /**
     * 更新Bmob _User表
     *
     * @param dbUrl
     */
    private void updateUserTable(final String dbUrl) {
        final CircleUser cUser = new CircleUser();
        CircleUser bmobUser = BmobUser.getCurrentUser(CircleUser.class);

        cUser.setDbBackupUrl(dbUrl);
        addSubscription(cUser.update(bmobUser.getObjectId(), new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            // 数据库更新成功
                            App.getInstance().showToast("备份完成。");
                            pbBackup.setVisibility(View.GONE);
                            BackupActivity.this.finish();   // 关闭页面
                            App.currentUser.setDbBackupUrl(dbUrl);
                        } else {
                            App.getInstance().showToast("您可以登录后再进行此操作。", Toast.LENGTH_LONG);
                            showErrorDialog(BackupActivity.this, "很抱歉，备份失败。\n" +
                                    ErrorCodes.errorMsg.get(e.getErrorCode()), true);
                            startActivityForResult(new Intent(BackupActivity.this, LoginActivity.class), Constants.REQUEST_CODE);
                            overridePendingTransition(R.anim.push_left_in_ac, R.anim.push_left_out_ac);
                        }
                    }
                })
        );
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
