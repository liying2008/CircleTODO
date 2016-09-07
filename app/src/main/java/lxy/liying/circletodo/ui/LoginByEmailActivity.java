package lxy.liying.circletodo.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.QueryListener;
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
 * 日期：2016/8/1 12:41
 * 版本：1.0
 * 描述：使用邮箱+密码登录方式
 * 备注：
 * =======================================================
 */
public class LoginByEmailActivity extends BaseActivity implements View.OnClickListener {

    private EditText etLoginEmail, etLoginPassword;
    private TextView tvToLoginByUsername;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_by_email);

        actionBarNavigator();
        etLoginEmail = (EditText) findViewById(R.id.etLoginEmail);
        etLoginPassword = (EditText) findViewById(R.id.etLoginPassword);
        tvToLoginByUsername = (TextView) findViewById(R.id.tvToLoginByUsername);
        Button btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);
    }

    /**
     * 使用邮箱+密码的方式进行登录
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        final String email = etLoginEmail.getText().toString();
        String password = etLoginPassword.getText().toString();
        password = StringUtils.encryptToSHA1(password);

        // 查看是否已经验证邮箱
        BmobQuery<CircleUser> query = new BmobQuery<CircleUser>();
        final String finalPassword = password;
        query.getObject(App.currentUser.getObjectId(), new QueryListener<CircleUser>() {

            @Override
            public void done(CircleUser object, BmobException e) {
                if (e == null) {
                    boolean verified = object.getEmailVerified();
                    if (verified) {
                        // 邮箱已验证
                        loginViaEmail(email, finalPassword);
                    } else {
                        App.getInstance().showToast("邮箱未验证，无法通过邮箱登录。");
                    }
                } else {
//                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                    App.getInstance().showToast(ErrorCodes.errorMsg.get(e.getErrorCode()));
                }
            }

        });
    }

    private void loginViaEmail(String email, String password) {
        BmobUser.loginByAccount(email, password, new LogInListener<CircleUser>() {

            @Override
            public void done(CircleUser user, BmobException e) {
                if (user != null) {
                    App.getInstance().showToast("登录成功");
                    // 设置登录成功用户为当前用户
                    App.currentUser = BmobUser.getCurrentUser(CircleUser.class);
                    // 设置当前用户ID
                    App.CURRENT_UID = App.currentUser.getUid();
                    // 通知CalendarActivity更新用户信息
                    setResult(Constants.REFRESH_USER_INFO_CODE);
                    LoginByEmailActivity.this.finish();    // 关闭本页面
                    LoginByEmailActivity.this.overridePendingTransition(R.anim.push_right_in_ac, R.anim.push_right_out_ac);
                } else {
                    // 提示错误信息
                    showErrorDialog(LoginByEmailActivity.this, "失败:" +
                            ErrorCodes.errorMsg.get(e.getErrorCode()), false);
                }
            }
        });
    }

    /**
     * 返回用户名+密码的登录方式
     *
     * @param view
     */
    public void toLoginByUsername(View view) {
        finish();
        overridePendingTransition(R.anim.push_right_in_ac, R.anim.push_right_out_ac);
    }
}
