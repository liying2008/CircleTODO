package lxy.liying.circletodo.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.UpdateListener;
import lxy.liying.circletodo.R;
import lxy.liying.circletodo.app.App;
import lxy.liying.circletodo.domain.CircleUser;
import lxy.liying.circletodo.utils.Constants;
import lxy.liying.circletodo.utils.ErrorCodes;
import lxy.liying.circletodo.utils.StringUtils;

/**
 * =======================================================
 * 版权：©Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying
 * 日期：2016/8/1 21:10
 * 版本：1.0
 * 描述：重置密码后的首次登录的Activity
 * 备注：
 * =======================================================
 */
public class ResetPasswordLoginActivity extends BaseActivity {
    private EditText etUsername, etResetPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password_login);
        actionBarNavigator();
        etUsername = (EditText) findViewById(R.id.etUsername);
        etResetPassword = (EditText) findViewById(R.id.etResetPassword);

        Button btnUsernameClear = (Button) findViewById(R.id.btnUsernameClear);
        Button btnPasswordClear = (Button) findViewById(R.id.btnPasswordClear);

        btnUsernameClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etUsername.setText("");
            }
        });

        btnPasswordClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etResetPassword.setText("");
            }
        });
    }

    /**
     * 初始化密码并登录
     *
     * @param view
     */
    public void initPasswordAndLogin(View view) {
        App.getInstance().showToast("正在发送请求，请稍候……");
        final String username = etUsername.getText().toString();
        final String password = etResetPassword.getText().toString();
        final String passwordEncrypted = StringUtils.encryptToSHA1(password);

        BmobUser.loginByAccount(username, password, new LogInListener<CircleUser>() {

            @Override
            public void done(CircleUser user, BmobException e) {
                if (user != null) {
                    // 登录成功
                    // 刷新用户信息
                    App.CURRENT_UID = user.getUid();
                    // 设置当前注册用户为当前用户
                    App.currentUser = user;
                    setResult(Constants.REFRESH_USER_INFO_CODE);
                    // 修改用户密码（将用户密码加密）
                    encryptPassword(password, passwordEncrypted);
                } else {
                    showErrorDialog(ResetPasswordLoginActivity.this, "登录失败：\n" +
                            ErrorCodes.errorMsg.get(e.getErrorCode()), false);
                }
            }
        });
    }

    /**
     * 修改用户密码（将用户密码加密）
     */
    private void encryptPassword(String oldPassword, String newPassword) {
        BmobUser.updateCurrentUserPassword(oldPassword, newPassword, new UpdateListener() {

            @Override
            public void done(BmobException e) {
                if (e == null) {
                    showDialog("初始化成功", "新密码已经重置成功，以后可直接在登录页面使用新密码登录。");
                } else {
                    showDialog("初始化失败", "您已经使用新密码登录，但新密码初始化失败，请下次登录时依然选择该方式登录。");
                }
            }
        });
    }

    /**
     * 显示初始化结果对话框
     *
     * @param title
     * @param msg
     */
    private void showDialog(String title, String msg) {
        AlertDialog.Builder builder = App.getAlertDialogBuilder(ResetPasswordLoginActivity.this);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton("知道了", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ResetPasswordLoginActivity.this.finish();
                overridePendingTransition(R.anim.push_right_in_ac, R.anim.push_right_out_ac);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }
}
