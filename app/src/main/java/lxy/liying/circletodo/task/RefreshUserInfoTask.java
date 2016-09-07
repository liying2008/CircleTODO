package lxy.liying.circletodo.task;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import java.io.File;
import java.io.InputStream;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.QueryListener;
import de.hdodenhof.circleimageview.CircleImageView;
import lxy.liying.circletodo.R;
import lxy.liying.circletodo.app.App;
import lxy.liying.circletodo.domain.CircleUser;
import lxy.liying.circletodo.ui.CalendarActivity;
import lxy.liying.circletodo.utils.Constants;
import lxy.liying.circletodo.utils.ErrorCodes;
import lxy.liying.circletodo.utils.FileUtils;

/**
 * =======================================================
 * 版权：©Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying
 * 日期：2016/8/15 20:07
 * 版本：1.0
 * 描述：刷新用户信息任务类
 * 备注：
 * =======================================================
 */
public class RefreshUserInfoTask extends MyAsyncTask {
    private Bitmap icon;
    private CircleImageView ivHeadIcon;
    private TextView tvCurrEmail;
    private CalendarActivity activity;
    private static final int REFRESH_HEAD_ICON = 0x0000;

    public RefreshUserInfoTask(CircleImageView ivHeadIcon, TextView tvCurrEmail, CalendarActivity activity) {
        this.ivHeadIcon = ivHeadIcon;
        this.tvCurrEmail = tvCurrEmail;
        this.activity = activity;
    }
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == REFRESH_HEAD_ICON){
                ivHeadIcon.setImageBitmap(icon);
            }
            return false;
        }
    });

    @Override
    protected Void doInBackground(String... params) {
        // 1、更新用户头像
        final String fileName = Constants.HEAD_ICON_DIR + File.separator + App.CURRENT_UID + ".png";
        File file = new File(fileName);
        if (file.exists()) {
            // SD卡上头像文件已经存在
            InputStream inputStream = FileUtils.getBitmapInputStreamFromSDCard(fileName);
            icon = BitmapFactory.decodeStream(inputStream);
            handler.sendEmptyMessage(REFRESH_HEAD_ICON);
            App.icon = icon;
        } else {
            // SD卡上无头像文件，需要从Bmob服务器下载头像文件
            BmobQuery<CircleUser> query = new BmobQuery<CircleUser>();
            if (App.currentUser.getObjectId() == null) {
                // 缓存的用户登录信息失效，需要用户再次登录
                activity.jumpToLogin();
                return null;
            }
            query.getObject(App.currentUser.getObjectId(), new QueryListener<CircleUser>() {

                @Override
                public void done(CircleUser object, BmobException e) {
                    if (e == null) {
                        String head_icon = object.getHead_icon();
                        if (head_icon.equals("")) {
                            // 说明用户没有上传头像，则使用默认头像
                            ivHeadIcon.setImageResource(R.drawable.icon_no_head);
                            App.icon = BitmapFactory.decodeResource(activity.getResources(), R.drawable.icon_no_head);
                        } else {
                            // 用户上传了头像，下载头像
                            BmobFile bmobfile = new BmobFile(App.CURRENT_UID + ".png", "", App.currentUser.getHead_icon());
                            downloadFile(bmobfile, fileName);
                        }
                    } else {
//                        Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                        App.getInstance().showToast("头像下载失败：" + ErrorCodes.errorMsg.get(e.getErrorCode()));
                    }
                }

            });
        }
        // 3、更新邮箱
        BmobQuery<CircleUser> query = new BmobQuery<CircleUser>();
        query.getObject(App.currentUser.getObjectId(), new QueryListener<CircleUser>() {

            @Override
            public void done(CircleUser object, BmobException e) {
                if (e == null) {
                    String email = object.getEmail();
                    tvCurrEmail.setText(email);
                    App.currentUser.setEmail(email);
                } else {
//                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                    tvCurrEmail.setText(App.currentUser.getEmail());
                }
            }
        });
        return null;
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
                App.getInstance().showToast("正在更新头像……");
            }

            @Override
            public void done(String savePath, BmobException e) {
                if (e == null) {
                    // 下载成功，显示头像
                    InputStream inputStream = FileUtils.getBitmapInputStreamFromSDCard(savePath);
                    Bitmap icon = BitmapFactory.decodeStream(inputStream);
                    ivHeadIcon.setImageBitmap(icon);
                    App.icon = icon;
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
