package lxy.liying.circletodo.task;

import java.util.List;

import lxy.liying.circletodo.domain.SelectedColor;

/**
 * =======================================================
 * 版权：©Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying
 * 日期：2016/8/15 12:54
 * 版本：1.0
 * 描述：统计标记监听回调
 * 备注：
 * =======================================================
 */
public interface OnStatisticsListener {
    /** 统计进度回调 */
    void onProgress(int progress);
    /** 统计完成回调 */
    void onComplete(List<String> dateList, List<List<SelectedColor>> selectColorList);
}
