package lxy.liying.circletodo.ui;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.io.InputStream;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;
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

public class PersonalActivity extends BaseActivity implements View.OnClickListener {

    private CircleImageView ivInfoHead;
    private TextView tvInfoUsername, tvInfoMarksNum;
    private EditText etInfoEmail;
    private Button btnChangeHead, btnChangeEmail, btnEmailVerify;
    /**
     * 更换邮箱的状态 1，2
     */
    private static int CHANGE_EMAIL_STATE = 1;
    /**
     * 更换头像的状态 3，4
     */
    private static int CHANGE_HEAD_STATE = 3;
    /**
     * 头像名称
     */
    private static String PHOTO_FILE_NAME;
    /**
     * 临时头像名称
     */
    private static String TEMP_FILE_NAME;
    /**
     * 用户头像存储的路径
     */
    private String headUrl = "";
    /**
     * 已使用的重发邮件的次数
     */
    private int verifyCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MobclickAgent.onEvent(this, Constants.UmengStatistics.PERSONAL);
        initView();
        PHOTO_FILE_NAME = App.CURRENT_UID;  // 头像文件名为uid
        TEMP_FILE_NAME = "temp";
    }

    /**
     * 初始化视图
     */
    private void initView() {
        setContentView(R.layout.activity_personal);
        actionBarNavigator();

        ivInfoHead = (CircleImageView) findViewById(R.id.ivInfoHead);
        tvInfoUsername = (TextView) findViewById(R.id.tvInfoUsername);
        tvInfoMarksNum = (TextView) findViewById(R.id.tvInfoMarksNum);
        etInfoEmail = (EditText) findViewById(R.id.etInfoEmail);
        btnChangeHead = (Button) findViewById(R.id.btnChangeHead);
        btnChangeEmail = (Button) findViewById(R.id.btnChangeEmail);
        btnEmailVerify = (Button) findViewById(R.id.btnEmailVerify);

        // 获取用户基本信息
        ivInfoHead.setImageBitmap(App.icon);
        tvInfoUsername.setText(App.currentUser.getUsername());
        tvInfoMarksNum.setText(String.valueOf(App.deService.getMarkCount()));
        etInfoEmail.setText(App.currentUser.getEmail());
        etInfoEmail.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                changeEmailTask(etInfoEmail.getText().toString());
                // 更改按钮状态
                etInfoEmail.setEnabled(false);
                btnChangeEmail.setText("更换邮箱地址");
                btnChangeEmail.setBackgroundResource(R.drawable.button_bright_selector);
                CHANGE_EMAIL_STATE = 1;
                return true;
            }
        });

        ivInfoHead.setOnClickListener(this);
        refreshVerifyCount();
    }

    /**
     * 刷新使用次数，更新按钮状态
     */
    private void refreshVerifyCount() {
        BmobQuery<CircleUser> query = new BmobQuery<CircleUser>();
        query.getObject(App.currentUser.getObjectId(), new QueryListener<CircleUser>() {

            @Override
            public void done(CircleUser object, BmobException e) {
                if (e == null) {
                    verifyCount = object.getVerify_count();
                    if (verifyCount >= 4) {
                        btnEmailVerify.setText("重发邮箱验证邮件 (剩余0次)");
                        btnEmailVerify.setEnabled(false);
                        btnEmailVerify.setTextColor(getResources().getColor(R.color.text_color));
                    } else {
                        btnEmailVerify.setEnabled(true);
                        String info = "重发邮箱验证邮件 (剩余" + (4 - verifyCount) + "次)";
                        btnEmailVerify.setText(info);
                        btnEmailVerify.setTextColor(getResources().getColor(R.color.dark_text_selector));
                    }

                } else {
                    btnEmailVerify.setText("重发邮箱验证邮件 (暂不可用)");
                    btnEmailVerify.setEnabled(false);
                    btnEmailVerify.setTextColor(getResources().getColor(R.color.text_color));
                }
            }
        });
    }

    /**
     * 更改头像
     *
     * @param view
     */
    public void changeHead(View view) {
        PermissionManager.grantPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (CHANGE_HEAD_STATE == 3) {
            PermissionManager.grantPermission(PersonalActivity.this, Manifest.permission.CAMERA);
            PicSelectUtils.popupPicSelectWindow(PersonalActivity.this, TEMP_FILE_NAME,
                    App.getInstance().getToast(), R.id.svInfo);
        } else {
            uploadHeadIcon();
            // 更改按钮状态
            btnChangeHead.setBackgroundResource(R.drawable.button_bright_selector);
            btnChangeHead.setText("更换头像");
            CHANGE_HEAD_STATE = 3;
        }
    }

    /**
     * 更换邮箱地址
     *
     * @param view
     */
    public void changeEmail(View view) {
        if (CHANGE_EMAIL_STATE == 1) {
            etInfoEmail.setEnabled(true);
            // 设置光标位于尾部
            etInfoEmail.setSelection(etInfoEmail.getText().length());
            btnChangeEmail.setText("确认更换");
            btnChangeEmail.setBackgroundResource(R.drawable.button_yellow_selector);
            CHANGE_EMAIL_STATE = 2;
        } else {
            changeEmailTask(etInfoEmail.getText().toString());
            // 更改按钮状态
            etInfoEmail.setEnabled(false);
            btnChangeEmail.setText("更换邮箱地址");
            btnChangeEmail.setBackgroundResource(R.drawable.button_bright_selector);
            CHANGE_EMAIL_STATE = 1;
        }
    }

    /**
     * 用户更换邮箱
     */
    private void changeEmailTask(final String newEmail) {
        App.getInstance().showToast("正在发送请求，请稍候……");
        CircleUser newUser = new CircleUser();
        newUser.setEmail(newEmail);
        CircleUser bmobUser = BmobUser.getCurrentUser(CircleUser.class);
        newUser.update(bmobUser.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    // 通知CalendarActivity更新用户信息
                    setResult(Constants.REFRESH_USER_INFO_CODE);
                    App.currentUser.setEmail(newEmail);
                    AlertDialog.Builder builder = App.getAlertDialogBuilder(PersonalActivity.this);
                    builder.setTitle("更换成功");
                    builder.setMessage("您的邮箱信息已经更新，如果更换了邮箱地址，系统会发送一封验证邮件，请去新邮箱接收验证邮件进行身份确认。");
                    builder.setPositiveButton("知道了", null);
                    builder.create().show();
                } else {
                    // 还原原来的邮箱
                    etInfoEmail.setText(App.currentUser.getEmail());
                    showErrorDialog(PersonalActivity.this, "更换邮箱失败：\n" +
                            ErrorCodes.errorMsg.get(e.getErrorCode()), false);
                }
            }
        });
    }

    /**
     * 更换密码，跳转到更改密码界面
     *
     * @param view
     */
    public void changePassword(View view) {
        Intent intent = new Intent(this, UpdatePasswordActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.push_left_in_ac, R.anim.push_left_out_ac);
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
                PicSelectUtils.crop(PersonalActivity.this, uri);
            }

        } else if (requestCode == PicSelectUtils.PHOTO_REQUEST_CAMERA) {
            tempFile = new File(Constants.HEAD_ICON_DIR + File.separator + TEMP_FILE_NAME);
            PicSelectUtils.crop(PersonalActivity.this, Uri.fromFile(tempFile));
        } else if (requestCode == PicSelectUtils.PHOTO_REQUEST_CUT) {
            try {
                Bitmap bitmap = data.getParcelableExtra("data");
                this.ivInfoHead.setImageBitmap(bitmap);
                FileUtils.saveBitmapToSDCard(TEMP_FILE_NAME, bitmap);
                changeHeadState();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void changeHeadState() {
        btnChangeHead.setBackgroundResource(R.drawable.button_yellow_selector);
        btnChangeHead.setText("确认更换");
        CHANGE_HEAD_STATE = 4;
    }

    /**
     * 上传用户头像
     */
    private void uploadHeadIcon() {
        // 上传头像文件
        App.getInstance().showToast("正在发送请求，请稍候……");
        final String headPicPath = Constants.HEAD_ICON_DIR + File.separator + TEMP_FILE_NAME + ".png";
        final BmobFile bmobFile = new BmobFile(new File(headPicPath));
        bmobFile.uploadblock(new UploadFileListener() {

            @Override
            public void done(BmobException e) {
                if (e == null) {
                    //bmobFile.getFileUrl()--返回的上传文件的完整地址
                    headUrl = bmobFile.getFileUrl();
                    // 改文件名
                    String uidPicPath = Constants.HEAD_ICON_DIR + File.separator + PHOTO_FILE_NAME + ".png";
                    FileUtils.rename(headPicPath, uidPicPath);
                    // 先删除原来的头像
                    BmobQuery<CircleUser> query = new BmobQuery<CircleUser>();
                    query.getObject(App.currentUser.getObjectId(), new QueryListener<CircleUser>() {

                        @Override
                        public void done(CircleUser object, BmobException e) {
                            if (e == null) {
                                String oldHeadUrl = object.getHead_icon();
                                delOldHeadUrl(oldHeadUrl);
                                // 再修改数据库个人信息
                                modifyBmobUserTable(headUrl);
                                App.currentUser.setHead_icon(headUrl);
                            } else {
//                                Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                            }
                        }

                    });
                } else {
                    App.getInstance().showToast("头像上传失败：" + ErrorCodes.errorMsg.get(e.getErrorCode()));
                }
            }

            @Override
            public void onProgress(Integer value) {
                // 返回的上传进度（百分比）
//                App.getInstance().showToast(String.valueOf(value));
//                System.out.println("头像上传进度：" + value);
            }
        });
    }

    /**
     * 删除原头像
     */
    private void delOldHeadUrl(String oldHeadUrl) {
        BmobFile file = new BmobFile();
        file.setUrl(oldHeadUrl);//此url是上传文件成功之后通过bmobFile.getUrl()方法获取的。
        file.delete(new UpdateListener() {

            @Override
            public void done(BmobException e) {
                if (e == null) {
                    System.out.println("文件删除成功");
                } else {
                    System.out.println("文件删除失败：" + e.getErrorCode() + "," + e.getMessage());
                }
            }
        });
    }

    /**
     * 更新Bmob _User表中的head_icon字段
     */
    private void modifyBmobUserTable(final String headUrl) {
        CircleUser newUser = new CircleUser();
        newUser.setHead_icon(headUrl);
        BmobUser bmobUser = BmobUser.getCurrentUser(CircleUser.class);
        newUser.update(bmobUser.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    // 通知CalendarActivity更新用户信息
                    setResult(Constants.REFRESH_USER_INFO_CODE);
                    App.currentUser.setHead_icon(headUrl);
                    AlertDialog.Builder builder = App.getAlertDialogBuilder(PersonalActivity.this);
                    builder.setTitle("更换成功");
                    builder.setMessage("头像更新成功。");
                    builder.setPositiveButton("知道了", null);
                    builder.create().show();
                } else {
                    showErrorDialog(PersonalActivity.this, "头像更新失败，" +
                            ErrorCodes.errorMsg.get(e.getErrorCode()), false);
                    App.getInstance().showToast("您可以登录后再进行此操作。", Toast.LENGTH_LONG);
                    startActivityForResult(new Intent(PersonalActivity.this, LoginActivity.class), Constants.REQUEST_CODE);
                    overridePendingTransition(R.anim.push_left_in_ac, R.anim.push_left_out_ac);
                }
            }
        });
    }


    /**
     * 重发邮箱验证邮件 <br/>
     * 每个帐号有4次使用机会 <br/>
     *
     * @param view
     */
    public void verifyEmail(View view) {
        BmobQuery<CircleUser> query = new BmobQuery<CircleUser>();
        query.getObject(App.currentUser.getObjectId(), new QueryListener<CircleUser>() {

            @Override
            public void done(CircleUser object, BmobException e) {
                if (e == null) {
                    verifyCount = object.getVerify_count();
                    if (verifyCount >= 4) {
                        showErrorDialog(PersonalActivity.this, "您已经使用了4次，无法再次使用。", false);
                        refreshVerifyCount();   // 刷新按钮状态
                        return;
                    }
                    sendVerifyEmail(verifyCount);
                } else {
                    btnEmailVerify.setText("重发邮箱验证邮件 (暂不可用)");
                    btnEmailVerify.setEnabled(false);
                    btnEmailVerify.setTextColor(getResources().getColor(R.color.text_color));
                }
            }
        });
    }

    /***
     * 重发邮箱验证邮件
     *
     * @param count
     */
    private void sendVerifyEmail(final int count) {
        AlertDialog.Builder builder = App.getAlertDialogBuilder(PersonalActivity.this);
        builder.setTitle("提醒");
        builder.setMessage("每个用户仅有4次重发邮箱验证邮件的机会，您已使用了" + count + "次。是否继续？");
        builder.setPositiveButton("继续", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                App.getInstance().showToast("正在发送请求，请稍候……");
                final String email = etInfoEmail.getText().toString();
                BmobUser.requestEmailVerify(email, new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            AlertDialog.Builder builder = App.getAlertDialogBuilder(PersonalActivity.this);
                            builder.setTitle("发送成功");
                            builder.setMessage("请求验证邮件成功，请到" + email + "邮箱中进行激活。");
                            builder.setPositiveButton("知道了", null);
                            builder.create().show();
                            // 使用次数+1
                            CircleUser cUser = new CircleUser();
                            cUser.setVerify_count(count + 1);
                            cUser.update(App.currentUser.getObjectId(), new UpdateListener() {

                                @Override
                                public void done(BmobException e) {
                                    if (e == null) {
//                                        Log.i("bmob","更新成功");
                                        // 刷新按钮状态
                                        refreshVerifyCount();
                                    } else {
//                                        Log.i("bmob","更新失败："+e.getMessage()+","+e.getErrorCode());
                                    }
                                }
                            });
                        } else {
                            showErrorDialog(PersonalActivity.this, "邮件发送失败，\n" +
                                    ErrorCodes.errorMsg.get(e.getErrorCode()), false);
                        }
                    }
                });
            }
        });
        builder.setNegativeButton("取消", null);
        builder.create().show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (CHANGE_EMAIL_STATE == 2) {
                etInfoEmail.setText(App.currentUser.getEmail());
                etInfoEmail.setEnabled(false);
                btnChangeEmail.setText("更换邮箱地址");
                btnChangeEmail.setBackgroundResource(R.drawable.button_bright_selector);
                CHANGE_EMAIL_STATE = 1;
                return true;
            } else if (CHANGE_HEAD_STATE == 4) {
                ivInfoHead.setImageBitmap(App.icon);    // 还原头像
                btnChangeHead.setBackgroundResource(R.drawable.button_bright_selector);
                btnChangeHead.setText("更换头像");
                CHANGE_HEAD_STATE = 3;
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 点击头像进行刷新
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        App.getInstance().showToast("正在获取最新的头像……");
        BmobQuery<CircleUser> query = new BmobQuery<CircleUser>();
        query.getObject(App.currentUser.getObjectId(), new QueryListener<CircleUser>() {

            @Override
            public void done(CircleUser object, BmobException e) {
                if (e == null) {
                    String headIconUrl = object.getHead_icon();
                    if ("".equals(headIconUrl)) {
                        // 用户没有上传过头像，则使用默认头像
                        ivInfoHead.setImageResource(R.drawable.icon_no_head);
                        App.icon = BitmapFactory.decodeResource(getResources(), R.drawable.icon_no_head);
                        App.getInstance().showToast("您没有上传过头像，已使用默认头像。");
                    } else {
                        // 开始下载头像
                        String path = App.CURRENT_UID + ".png";
                        BmobFile bmobfile = new BmobFile(path, "", headIconUrl);
                        downloadFile(bmobfile);
                    }
                } else {
//                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                    showErrorDialog(PersonalActivity.this, "获取最新头像失败，\n" +
                            ErrorCodes.errorMsg.get(e.getErrorCode()), false);
                }
            }
        });
    }

    /**
     * 下载头像
     *
     * @param file
     */
    private void downloadFile(BmobFile file) {
        File saveFile = new File(Constants.HEAD_ICON_DIR, file.getFilename());
        file.download(saveFile, new DownloadFileListener() {

            @Override
            public void onStart() {
//                toast("开始下载...");
                App.getInstance().showToast("开始下载头像……");
            }

            @Override
            public void done(String savePath, BmobException e) {
                if (e == null) {
//                    toast("下载成功,保存路径:"+savePath);
                    InputStream inputStream = FileUtils.getBitmapInputStreamFromSDCard(savePath);
                    Bitmap icon = BitmapFactory.decodeStream(inputStream);
                    ivInfoHead.setImageBitmap(icon);
                    setResult(Constants.REFRESH_USER_INFO_CODE);    // 刷新用户信息
                    App.icon = icon;
                    App.getInstance().showToast("头像已更新。");
                } else {
                    showErrorDialog(PersonalActivity.this, "获取最新头像失败，\n" +
                            ErrorCodes.errorMsg.get(e.getErrorCode()), false);
                }
            }

            @Override
            public void onProgress(Integer value, long newworkSpeed) {
//                Log.i("bmob","下载进度："+value+","+newworkSpeed);
            }
        });
    }
}
