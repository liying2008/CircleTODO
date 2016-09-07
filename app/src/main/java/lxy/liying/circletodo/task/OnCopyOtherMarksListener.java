package lxy.liying.circletodo.task;

/**
 * =======================================================
 * 版权：©Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying
 * 日期：2016/8/15 13:34
 * 版本：1.0
 * 描述：复制其他人的标记监听回调
 * 备注：
 * =======================================================
 */
public interface OnCopyOtherMarksListener {
    /**
     * 清除进度回调
     */
    void onProgress(int progress);

    /**
     * 清除完成回调
     *
     * @param colorOverrideCount 颜色覆盖数目
     * @param colorIgnoreCount   颜色忽略数目
     * @param markOverrideCount  标记覆盖数目
     * @param markIgnoreCount    标记忽略数目
     */
    void onComplete(int colorOverrideCount, int colorIgnoreCount, int markOverrideCount, int markIgnoreCount);
}
