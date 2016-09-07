package lxy.liying.circletodo.task;

/**
 * =======================================================
 * 版权：©Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying
 * 日期：2016/8/15 13:25
 * 版本：1.0
 * 描述：清除标记回调监听
 * 备注：
 * =======================================================
 */
public interface OnRemoveMarksListener {
    /** 清除进度回调 */
    void onProgress(int progress);
    /** 清除完成回调 */
    void onComplete();
}
