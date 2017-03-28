package lxy.liying.circletodo.task;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lxy.liying.circletodo.app.App;
import lxy.liying.circletodo.db.DateEventService;
import lxy.liying.circletodo.domain.DateEvent;
import lxy.liying.circletodo.domain.SelectedColor;
import lxy.liying.circletodo.utils.Constants;
import lxy.liying.circletodo.utils.StringUtils;

/**
 * =======================================================
 * 版权：©Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying
 * 日期：2016/7/31 14:32
 * 版本：1.0
 * 描述：统计[今日及以后][今日及以前][所有]的所有标记任务类
 * 备注：
 * =======================================================
 */
public class StatisticsMarksTask extends MyAsyncTask {

    private OnStatisticsListener listener;
    private List<String> dateList = new ArrayList<>();
    private List<List<SelectedColor>> selectColorList = new ArrayList<>();
    private Set<SelectedColor> colorSet = new HashSet<>();

    public StatisticsMarksTask(OnStatisticsListener listener) {
        this.listener = listener;
    }

    /**
     * 统计标记 <br/>
     *
     * @param params 今日的日期（比如：params[0] = "2016-7-31"）
     * @return null
     */
    @Override
    protected Void doInBackground(String... params) {
        String thisDate = StringUtils.formatDate(params[0]);
        if (App.STATISTICS_SCOPE == 0) {
            // 本日后
            statisticsAfter(thisDate);
        } else if (App.STATISTICS_SCOPE == 1) {
            // 本日前
            statisticsBefore(thisDate);
        } else if (App.STATISTICS_SCOPE == 2) {
            // 所有日期
            statisticsAll();
        }
        return null;
    }

    /**
     * 统计所有标记
     */
    private void statisticsAll() {
        List<DateEvent> allDateEvent = App.deService.findAllDateEvent(DateEventService.ORDER_DESC);
        DateEvent dateEvent;
        if (allDateEvent != null) {
            int size = allDateEvent.size();
            String currDate = "";
            List<SelectedColor> colorList = null;
            for (int i = 0; i < size; i++) {
                dateEvent = allDateEvent.get(i);
                String date = dateEvent.getE_date();
                if (!dateList.contains(date)) {
                    dateList.add(date);
                    currDate = date;
                    if (colorList != null) {
                        selectColorList.add(colorList);
                    }
                    colorList = new ArrayList<>(Constants.DAY_MAX_MARK_NUM);
                }
                if (date.equals(currDate)) {
                    SelectedColor selectedColor = new SelectedColor(dateEvent.getColor_id(),
                            App.scService.getColorEvent(dateEvent.getColor_id()));
                    colorList.add(selectedColor);
                    colorSet.add(selectedColor);
                }

                // 更新进度
                publishProgress(100 * i / size);
            }
            selectColorList.add(colorList);
        }
    }

    /**
     * 统计本日及以前的标记
     *
     * @param thisDate
     */
    private void statisticsBefore(String thisDate) {
        List<DateEvent> allDateEvent = App.deService.findAllDateEvent(DateEventService.ORDER_DESC);
        DateEvent dateEvent;
        if (allDateEvent != null) {
            int size = allDateEvent.size();
            String currDate = "";
            List<SelectedColor> colorList = null;
            for (int i = 0; i < size; i++) {
                dateEvent = allDateEvent.get(i);
                String date = dateEvent.getE_date();
                if (date.compareTo(thisDate) <= 0) {
                    // 今天及之前的标记
                    if (!dateList.contains(date)) {
                        dateList.add(date);
                        currDate = date;
                        if (colorList != null) {
                            selectColorList.add(colorList);
                        }
                        colorList = new ArrayList<>(Constants.DAY_MAX_MARK_NUM);
                    }
                    if (date.equals(currDate)) {
                        SelectedColor selectedColor = new SelectedColor(dateEvent.getColor_id(),
                                App.scService.getColorEvent(dateEvent.getColor_id()));
                        colorList.add(selectedColor);
                        colorSet.add(selectedColor);
                    }
                }
                // 更新进度
                publishProgress(100 * i / size);
            }
            selectColorList.add(colorList);
        }
    }

    /**
     * 统计本日及以后的标记
     *
     * @param thisDate
     */
    private void statisticsAfter(String thisDate) {
        List<DateEvent> allDateEvent = App.deService.findAllDateEvent(DateEventService.ORDER_ASC);
        DateEvent dateEvent;
        if (allDateEvent != null) {
            int size = allDateEvent.size();
            String currDate = "";
            List<SelectedColor> colorList = null;
            for (int i = 0; i < size; i++) {
                dateEvent = allDateEvent.get(i);
                String date = dateEvent.getE_date();
                if (date.compareTo(thisDate) >= 0) {
                    // 今天及之后的标记
                    if (!dateList.contains(date)) {
                        dateList.add(date);
                        currDate = date;
                        if (colorList != null) {
                            selectColorList.add(colorList);
                        }
                        colorList = new ArrayList<>(Constants.DAY_MAX_MARK_NUM);
                    }
                    if (date.equals(currDate)) {
                        SelectedColor selectedColor = new SelectedColor(dateEvent.getColor_id(),
                                App.scService.getColorEvent(dateEvent.getColor_id()));
                        colorList.add(selectedColor);
                        colorSet.add(selectedColor);
                    }
                }
                // 更新进度
                publishProgress(100 * i / size);
            }
            selectColorList.add(colorList);
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        listener.onProgress(values[0]);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        listener.onComplete(dateList, selectColorList, colorSet);
    }
}
