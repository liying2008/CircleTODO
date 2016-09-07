package lxy.liying.circletodo.utils;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.ActionSheetDialog;

import java.io.File;

import lxy.liying.circletodo.ui.BaseActivity;

/**
 * =======================================================
 * 版权：©Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying
 * 日期：2016/7/30 16:18
 * 版本：1.0
 * 描述：popupWindow功能工具类
 * 备注：
 * =======================================================
 */
public class PicSelectUtils {

    public static final int PHOTO_REQUEST_CAMERA = 1;// 拍照
    public static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
    public static final int PHOTO_REQUEST_CUT = 3;// 结果

    /**
     * 弹出选择头像Window
     *
     * @param activity
     * @param photoFileName
     * @param toast
     */
    public static void popupPicSelectWindow(final BaseActivity activity, final String photoFileName,
                                            final Toast toast, int layoutId) {
        final ActionSheetDialog dialog = new ActionSheetDialog(activity, new String[]{"拍照", "从相册选择"}, null);
        dialog.title("上传头像")//
                .titleTextSize_SP(14.5f)//
                .show();
        dialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int which, long id) {
                if (which == 0) {
                    // 从相机拍照获取
                    takePhoto(activity, photoFileName, toast);
                } else if (which == 1) {
                    // 从相册（图库）获取
                    pickPhoto(activity);
                }
                dialog.dismiss();
            }
        });
    }

    /**
     * 拍照获取图片
     *
     * @param activity
     * @param photoFileName
     * @param toast
     */
    public static void takePhoto(BaseActivity activity, String photoFileName, Toast toast) {
        // 执行拍照前，应该先判断SD卡是否存在
        String SDState = Environment.getExternalStorageState();
        if (SDState.equals(Environment.MEDIA_MOUNTED)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Uri photoUri = Uri.fromFile(new File(Constants.HEAD_ICON_DIR + File.separator + photoFileName));
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            activity.startActivityForResult(intent, PHOTO_REQUEST_CAMERA);
        } else {
            toast.setText("内存卡不存在。");
            toast.show();
        }
    }

    /***
     * 从相册中取图片
     *
     * @param activity
     */
    public static void pickPhoto(BaseActivity activity) {
        Intent intent = new Intent();
        // 如果要限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型"
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        activity.startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
    }

    /**
     * 裁剪图片
     *
     * @param activity
     * @param uri
     */
    public static void crop(BaseActivity activity, Uri uri) {
        // 裁剪图片意图
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // 裁剪框的比例，1：1
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // 裁剪后输出图片的尺寸大小
        intent.putExtra("outputX", Constants.HEAD_WIDTH);
        intent.putExtra("outputY", Constants.HEAD_HEIGHT);
        // 图片格式
        intent.putExtra("outputFormat", "PNG");
        intent.putExtra("noFaceDetection", true);// 取消人脸识别
        intent.putExtra("return-data", true);// true:不返回uri，false：返回uri
        activity.startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }
}
