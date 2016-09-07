package lxy.liying.circletodo.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import de.hdodenhof.circleimageview.CircleImageView;
import lxy.liying.circletodo.R;
import lxy.liying.circletodo.app.App;
import lxy.liying.circletodo.domain.CircleUser;
import lxy.liying.circletodo.utils.Constants;
import lxy.liying.circletodo.utils.ErrorCodes;
import lxy.liying.circletodo.utils.FileUtils;
import lxy.liying.circletodo.utils.StringUtils;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private CircleImageView ivLoginIcon;
    private EditText etLoginUsername, etLoginPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        actionBarNavigator();

        ivLoginIcon = (CircleImageView) findViewById(R.id.ivLoginIcon);
        etLoginUsername = (EditText) findViewById(R.id.etLoginUsername);
        etLoginPassword = (EditText) findViewById(R.id.etLoginPassword);
        Button btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);
        // 实时刷新用户头像
        UsernameWatcher watcher = new UsernameWatcher();
        etLoginUsername.addTextChangedListener(watcher);
    }

    /**
     * 跳转到注册页面
     *
     * @param view
     */
    public void toRegister(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivityForResult(intent, Constants.REQUEST_CODE);
        overridePendingTransition(R.anim.push_left_in_ac, R.anim.push_left_out_ac);
    }

    /**
     * 重置密码后的首次登录
     * @param view
     */
    public void toResetPasswordLogin(View view) {
        Intent intent = new Intent(this, ResetPasswordLoginActivity.class);
        startActivityForResult(intent, Constants.REQUEST_CODE);
        overridePendingTransition(R.anim.push_left_in_ac, R.anim.push_left_out_ac);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Constants.REFRESH_USER_INFO_CODE) {
            // 通知CalendarActivity刷新用户信息
            setResult(Constants.REFRESH_USER_INFO_CODE);
            LoginActivity.this.finish();  // 关闭本页面
        }
    }

    /**
     * 用户登录
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        String username = etLoginUsername.getText().toString();
        String password = etLoginPassword.getText().toString();
        String passwordEncrypted = StringUtils.encryptToSHA1(password);

        CircleUser cUser = new CircleUser();
        cUser.setUsername(username);
        cUser.setPassword(passwordEncrypted);
        cUser.login(new SaveListener<CircleUser>() {

            @Override
            public void done(CircleUser bmobUser, BmobException e) {
                if (e == null) {
                    App.getInstance().showToast("登录成功");
                    //通过BmobUser user = BmobUser.getCurrentUser()获取登录成功后的本地用户信息
                    //如果是自定义用户对象MyUser，可通过MyUser user = BmobUser.getCurrentUser(MyUser.class)获取自定义用户信息
                    // 设置登录成功用户为当前用户
                    App.currentUser = BmobUser.getCurrentUser(CircleUser.class);
                    // 设置当前用户ID
                    App.CURRENT_UID = App.currentUser.getUid();
                    // 通知CalendarActivity更新用户信息
                    setResult(Constants.REFRESH_USER_INFO_CODE);
                    LoginActivity.this.finish();    // 关闭本页面
                    LoginActivity.this.overridePendingTransition(R.anim.push_right_in_ac, R.anim.push_right_out_ac);
                } else {
                    // 提示错误信息
                    showErrorDialog(LoginActivity.this, "登录失败，\n" +
                            ErrorCodes.errorMsg.get(e.getErrorCode()), false);
                }
            }
        });
    }

    /**
     * 通过邮箱重置密码
     *
     * @param view
     */
    public void toResetPassword(View view) {
        AlertDialog.Builder builder = App.getAlertDialogBuilder(this);
        builder.setTitle("提醒");
        builder.setMessage("您正在进行重置密码的操作，我们将会向您注册时填写的电子邮件地址发送一封包含特殊的密码重置链接的电子邮件，您可以根据向导点击重置密码连接，依据提示输入一个新的密码。");
        builder.setPositiveButton("重置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                resetPassword();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                App.getInstance().showToast("您已取消了操作。");
            }
        });
        builder.create().show();
    }

    /**
     * 重置密码
     */
    private void resetPassword() {
        AlertDialog.Builder builder = App.getAlertDialogBuilder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_put_email, null);
        final EditText etEmail = (EditText) view.findViewById(R.id.etEmail);
        Button btnClear = (Button) view.findViewById(R.id.btnClear);
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etEmail.setText("");
            }
        });
        builder.setTitle("填写电子邮件地址");
        builder.setView(view);
        builder.setPositiveButton("重置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String email = etEmail.getText().toString();
                BmobUser.resetPasswordByEmail(email, new UpdateListener() {

                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            AlertDialog.Builder infoBuilder = App.getAlertDialogBuilder(LoginActivity.this);
                            infoBuilder.setTitle("邮件已发送");
                            infoBuilder.setMessage("重置密码请求成功，请到 " + email + " 邮箱进行密码重置操作。");
                            infoBuilder.setPositiveButton("知道了", null);
                            AlertDialog infoDialog = infoBuilder.create();
                            // 不允许通过点击Dialog外部关闭Dialog
                            infoDialog.setCanceledOnTouchOutside(false);
                            // 解决不自动弹出输入法面板的问题
                            infoDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                            infoDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                            infoDialog.show();
                        } else {
                            showErrorDialog(LoginActivity.this, "失败:" +
                                    ErrorCodes.errorMsg.get(e.getErrorCode()), false);
                        }
                    }
                });
            }
        });
        builder.setNegativeButton("取消", null);
        builder.create().show();
    }

    /**
     * 使用邮箱登录
     * @param view
     */
    public void toLoginByEmail(View view) {
        Intent intent = new Intent(this, LoginByEmailActivity.class);
        startActivityForResult(intent, Constants.REQUEST_CODE);
        overridePendingTransition(R.anim.push_left_in_ac, R.anim.push_left_out_ac);
    }


    class UsernameWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String username = s.toString();
            BmobQuery<CircleUser> query = new BmobQuery<CircleUser>();

            //查询username为已经输入的用户名的数据
            query.addWhereEqualTo("username", username);
            //返回1条数据
            query.setLimit(1);
            //执行查询方法
            query.findObjects(new FindListener<CircleUser>() {
                @Override
                public void done(List<CircleUser> object, BmobException e) {
                    if (e == null) {
                        if (object.size() > 0) {
                            CircleUser cUser = object.get(0);
                            String head_icon = cUser.getHead_icon();
                            String uid = cUser.getUid();
                            refreshUserHead(uid, head_icon);
                        } else {
                            ivLoginIcon.setImageResource(R.drawable.unlogin);
                        }
                    } else {
                        Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                        ivLoginIcon.setImageResource(R.drawable.unlogin);
                    }
                }
            });
        }
    }

    /**
     * 刷新用户信息
     */
    private void refreshUserHead(String uid, String headUrl) {
        // 刷新用户头像
        String fileName = Constants.HEAD_ICON_DIR + File.separator + uid + ".png";
        File file = new File(fileName);
        if (file.exists()) {
            // SD卡上头像文件已经存在
            InputStream inputStream = FileUtils.getBitmapInputStreamFromSDCard(fileName);
            Bitmap icon = BitmapFactory.decodeStream(inputStream);
            ivLoginIcon.setImageBitmap(icon);
        } else {
            // SD卡上无头像文件，需要从Bmob服务器下载头像文件
            if (headUrl.equals("")) {
                // 说明用户没有上传头像
                ivLoginIcon.setImageResource(R.drawable.icon_no_head);
            } else {
                // 用户上传了头像，下载头像
                BmobFile bmobfile = new BmobFile(uid + ".png", "", headUrl);
                downloadFile(bmobfile, fileName);
            }
        }
    }

    /**
     * 下载头像文件
     *
     * @param file
     */
    private void downloadFile(BmobFile file, String fileName) {
        //允许设置下载文件的存储路径，默认下载文件的目录为：context.getApplicationContext().getCacheDir()+"/bmob/"
        File saveFile = new File(fileName);
        file.download(saveFile, new DownloadFileListener() {

            @Override
            public void onStart() {
//                toast("开始下载...");
//                App.getInstance().showToast("正在更新头像……");
            }

            @Override
            public void done(String savePath, BmobException e) {
                if (e == null) {
                    // 下载成功，显示头像
                    InputStream inputStream = FileUtils.getBitmapInputStreamFromSDCard(savePath);
                    Bitmap icon = BitmapFactory.decodeStream(inputStream);
                    ivLoginIcon.setImageBitmap(icon);
                } else {
                    App.getInstance().showToast("头像下载失败：" + ErrorCodes.errorMsg.get(e.getErrorCode()));
                }
            }

            @Override
            public void onProgress(Integer value, long newworkSpeed) {
//                Log.i("bmob","下载进度："+value+","+newworkSpeed);
            }

        });
    }
}
