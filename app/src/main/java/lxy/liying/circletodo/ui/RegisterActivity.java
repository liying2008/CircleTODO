package lxy.liying.circletodo.ui;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import java.io.File;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;
import de.hdodenhof.circleimageview.CircleImageView;
import lxy.liying.circletodo.R;
import lxy.liying.circletodo.app.App;
import lxy.liying.circletodo.domain.CircleUser;
import lxy.liying.circletodo.permission.PermissionManager;
import lxy.liying.circletodo.utils.Constants;
import lxy.liying.circletodo.utils.ErrorCodes;
import lxy.liying.circletodo.utils.FileUtils;
import lxy.liying.circletodo.utils.PicSelectUtils;
import lxy.liying.circletodo.utils.StringUtils;

public class RegisterActivity extends BaseActivity implements View.OnClickListener {

    private CircleImageView ivRegisIcon;
    private EditText etRegisUsername, etRegisPassword, etRegisEmail;
    /**
     * 用户头像存储的路径
     */
    private String headUrl = "";
    /**
     * 数据库备份的url
     */
    private String dbBackupUrl = "";
    private boolean hasHeadIcon = false;    // 用户是否选择了头像
    /**
     * 头像名称
     */
    private static String PHOTO_FILE_NAME;
    /**
     * 临时头像名称
     */
    private static String TEMP_FILE_NAME;
    private String uid;
    /** 用户注册用户名 */
    private String username;
    /** 用户注册密码 */
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        MobclickAgent.onEvent(this, Constants.UmengStatistics.REGISTER);
        actionBarNavigator();
        uid = StringUtils.uuid();
        PHOTO_FILE_NAME = uid;  // 头像文件名为uid
        TEMP_FILE_NAME = "temp";
        ivRegisIcon = (CircleImageView) findViewById(R.id.ivRegisIcon);
        etRegisUsername = (EditText) findViewById(R.id.etRegisUsername);
        etRegisPassword = (EditText) findViewById(R.id.etRegisPassword);
        etRegisEmail = (EditText) findViewById(R.id.etRegisEmail);

        // 设置头像
        ivRegisIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 获取权限
                PermissionManager.grantPermission(RegisterActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                PermissionManager.grantPermission(RegisterActivity.this, Manifest.permission.CAMERA);
                PicSelectUtils.popupPicSelectWindow(RegisterActivity.this, TEMP_FILE_NAME,
                        App.getInstance().getToast(), R.id.slRegister);
            }
        });

        Button btnRegist = (Button) findViewById(R.id.btnRegist);
        btnRegist.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        File tempFile = null;
        // 点击取消按钮
        if (resultCode == RESULT_CANCELED) {
            return;
        }

        if (requestCode == PicSelectUtils.PHOTO_REQUEST_GALLERY) {
            if (data != null) {
                // 得到图片的全路径
                Uri uri = data.getData();
                PicSelectUtils.crop(RegisterActivity.this, uri);
            }

        } else if (requestCode == PicSelectUtils.PHOTO_REQUEST_CAMERA) {
            tempFile = new File(Constants.HEAD_ICON_DIR + File.separator + TEMP_FILE_NAME);
            PicSelectUtils.crop(RegisterActivity.this, Uri.fromFile(tempFile));

        } else if (requestCode == PicSelectUtils.PHOTO_REQUEST_CUT) {
            try {
                Bitmap bitmap = data.getParcelableExtra("data");
                this.ivRegisIcon.setImageBitmap(bitmap);
                FileUtils.saveBitmapToSDCard(TEMP_FILE_NAME, bitmap);
                hasHeadIcon = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 用户注册
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        username = etRegisUsername.getText().toString();
        password = etRegisPassword.getText().toString();

        // 用户名不得小于3位
        if (username.length() < 3) {
            App.getInstance().showToast("用户名长度必须大于或等于3。");
            return;
        }
        if (password.length() < 3) {
            App.getInstance().showToast("密码长度必须大于或等于3。");
            return;
        }

        App.getInstance().showToast("正在发送请求，请稍候……");
        if (hasHeadIcon) {
            // 用户选择了头像，则先上传头像得到headUrl
            uploadHeadIcon();
        } else {
            register();
        }
    }

    /**
     * 用户注册
     */
    private void register() {
        String email = etRegisEmail.getText().toString();
        String passwordEncrypted = StringUtils.encryptToSHA1(password);
        final CircleUser cUser = new CircleUser();
        cUser.setUsername(username);
        cUser.setPassword(passwordEncrypted);
        cUser.setEmail(email);
        cUser.setUid(uid);
        cUser.setHead_icon(headUrl);
        cUser.setDbBackupUrl(dbBackupUrl);

        //注意：不能用save方法进行注册
        cUser.signUp(new SaveListener<CircleUser>() {
            @Override
            public void done(CircleUser s, BmobException e) {
                if (e == null) {
                    App.getInstance().showToast("恭喜，您已注册成功，请去邮箱接收验证邮件进行身份确认。", Toast.LENGTH_LONG);
                    // 刷新用户信息
                    App.CURRENT_UID = uid;
                    // 设置当前用户
                    App.currentUser = BmobUser.getCurrentUser(CircleUser.class);
                    // 注册完毕之后，需要登录（由于ObjectId为空，故应用会自动跳转到登录页面）
                    setResult(Constants.REFRESH_USER_INFO_CODE);
                    finish();
                    overridePendingTransition(R.anim.push_right_in_ac, R.anim.push_right_out_ac);
                } else {
                    showErrorDialog(RegisterActivity.this, "很抱歉，注册失败，" + ErrorCodes.errorMsg.get(e.getErrorCode()), false);
                }
            }
        });
    }

    /**
     * 上传用户头像
     */
    private void uploadHeadIcon() {
        // 上传头像文件
        final String headPicPath = Constants.HEAD_ICON_DIR + File.separator + TEMP_FILE_NAME + ".png";
        final BmobFile bmobFile = new BmobFile(new File(headPicPath));
        bmobFile.uploadblock(new UploadFileListener() {

            @Override
            public void done(BmobException e) {
                if (e == null) {
                    //bmobFile.getFileUrl()--返回的上传文件的完整地址
                    headUrl = bmobFile.getFileUrl();
//                    App.getInstance().showToast("头像上传成功:" + headUrl);
                    // 改文件名
                    String uidPicPath = Constants.HEAD_ICON_DIR + File.separator + PHOTO_FILE_NAME + ".png";
                    FileUtils.rename(headPicPath, uidPicPath);
                    register(); // 注册
                } else {
                    App.getInstance().showToast("头像上传失败：" + ErrorCodes.errorMsg.get(e.getErrorCode()));
                    register();
                }
            }

            @Override
            public void onProgress(Integer value) {
                // 返回的上传进度（百分比）
//                App.getInstance().showToast(String.valueOf(value));
            }
        });
    }
}
