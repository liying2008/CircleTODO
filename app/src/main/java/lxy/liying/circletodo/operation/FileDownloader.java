package lxy.liying.circletodo.operation;

import android.app.ProgressDialog;
import android.support.v7.app.AlertDialog;
import android.view.Window;

import java.io.File;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import lxy.liying.circletodo.R;
import lxy.liying.circletodo.adapter.CalendarAdapter;
import lxy.liying.circletodo.app.App;
import lxy.liying.circletodo.task.CopyOtherMarksTask;
import lxy.liying.circletodo.task.OnCopyOtherMarksListener;
import lxy.liying.circletodo.ui.CalendarActivity;
import lxy.liying.circletodo.utils.ErrorCodes;
import lxy.liying.circletodo.utils.FileUtils;

/**
 * =======================================================
 * 版权：©Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying
 * 日期：2016/8/15 21:09
 * 版本：1.0
 * 描述：下载文件
 * 备注：
 * =======================================================
 */
public class FileDownloader {
    private CalendarActivity activity;
    private CalendarAdapter calendarAdapter;
    private ProgressDialog progressDialog;

    public FileDownloader(CalendarActivity activity, CalendarAdapter calendarAdapter) {
        this.activity = activity;
        this.calendarAdapter = calendarAdapter;
    }
    /**
     * 下载数据库文件
     *
     * @param file
     */
    public void downloadDBFile(final String uid, final String username, BmobFile file) {
        //允许设置下载文件的存储路径，默认下载文件的目录为：context.getApplicationContext().getCacheDir()+"/bmob/"
        File saveFile = new File(activity.getCacheDir(), file.getFilename());
        file.download(saveFile, new DownloadFileListener() {

            @Override
            public void onStart() {
                progressDialog = new ProgressDialog(activity);
                progressDialog.setMessage("正在下载 " + username + " 的标记...");
                progressDialog.show();
            }

            @Override
            public void done(String savePath, BmobException e) {
                if (e == null) {
//                    toast("下载成功,保存路径:"+savePath);
                    progressDialog.setMessage("正在复制 " + username + " 的标记...");
                    CopyOtherMarksTask task = new CopyOtherMarksTask(new OnCopyOtherMarksListener() {
                        @Override
                        public void onProgress(int progress) {

                        }

                        @Override
                        public void onComplete(int colorOverrideCount, int colorIgnoreCount, int markOverrideCount, int markIgnoreCount) {
                            // 删除temp.db
                            FileUtils.delete(activity.getCacheDir() + File.separator + "temp.db");
                            // 取消对话框
                            progressDialog.dismiss();
                            progressDialog = null;
                            AlertDialog.Builder builder = App.getAlertDialogBuilder(activity);
                            builder.setTitle("复制完成");
                            String msg = "";
                            if (App.SHARE_CONFLICT == 0) {
                                // 覆盖原来的
                                msg = "新标记复制完成。\n\n覆盖原有颜色数：" + colorOverrideCount + "\n覆盖原有标记数：" + markOverrideCount + "\n忽略新标记数：" + markIgnoreCount;
                            } else if (App.SHARE_CONFLICT == 1) {
                                // 忽略新的
                                msg = "新标记复制完成。\n\n忽略新颜色数：" + colorIgnoreCount + "\n忽略新标记数：" + markIgnoreCount;
                            }
                            builder.setMessage(msg);
                            builder.setPositiveButton("确定", null);
                            builder.create().show();
                            App.getSelectedColors();    // 更新已选颜色
                            calendarAdapter.notifyDataSetChanged(); // 更新GridView
                        }
                    });
                    task.execute(uid, activity.getCacheDir() + File.separator + "temp.db");
                } else {
//                    toast("下载失败："+e.getErrorCode()+","+e.getMessage());
                    progressDialog.dismiss();
                    progressDialog = null;
                    AlertDialog.Builder builder = App.getAlertDialogBuilder(activity);
                    builder.setTitle("出现了错误");
                    builder.setMessage("下载失败：\n" + ErrorCodes.errorMsg.get(e.getErrorCode()));
                    builder.setPositiveButton("确定", null);
                    AlertDialog dialog = builder.create();
                    // 设置Dialog动画效果
                    Window window = dialog.getWindow();
                    window.setWindowAnimations(R.style.myDialogAnim);
                    dialog.show();
                }
            }

            @Override
            public void onProgress(Integer value, long newworkSpeed) {
//                Log.i("bmob","下载进度："+value+","+newworkSpeed);
            }
        });
    }
}
