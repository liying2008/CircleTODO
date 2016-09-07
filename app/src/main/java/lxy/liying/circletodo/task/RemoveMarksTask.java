package lxy.liying.circletodo.task;

import java.util.List;

import lxy.liying.circletodo.app.App;
import lxy.liying.circletodo.db.DateEventService;
import lxy.liying.circletodo.domain.DateEvent;
import lxy.liying.circletodo.utils.StringUtils;


/**
 * =======================================================
 * 版权：©Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying
 * 日期：2016/7/26 20:06
 * 版本：1.0
 * 描述：删除标记的异步任务类
 * 备注：
 * =======================================================
 */
public class RemoveMarksTask extends MyAsyncTask {

    private OnRemoveMarksListener listener;

    public RemoveMarksTask(OnRemoveMarksListener listener) {
        this.listener = listener;
    }

    /**
     * 删除某一日期之前或之后的所有标记 <br/>
     * 或者删除所有的标记 <br/>
     *
     * @param params 日期和前后（如：params[0] = "2016-7-26"  params[1] = "前"） <br/>
     *               或者传入空串（即：params[0] = ""  params[1] = ""） <br/>
     * @return null
     */
    @Override
    protected Void doInBackground(String... params) {
        // 先判断是否是空串
        if ("".equals(params[0]) && "".equals(params[1])) {
            // 清理所有的标记
//            App.deService.removeMarks();  // 此方法无法获取进度
            List<DateEvent> lists = App.deService.findAllDateEvent(DateEventService.ORDER_ASC);
            if (lists != null) {
                int size = lists.size();
                for (int i = 0; i < size; i++) {
                    App.deService.removeMark(lists.get(i).getD_id());
                    // 更新进度
                    publishProgress(100 * i / size);
                }
            }
            return null;
        }
        // 得到日期和前后
        String oldDate = params[0];
        String item = params[1];

        String thisDate = StringUtils.formatDate(oldDate);
        List<DateEvent> allDateEvent = App.deService.findAllDateEvent(DateEventService.ORDER_ASC);
        DateEvent dateEvent;
        if (allDateEvent != null) {
            int size = allDateEvent.size();
            if ("前".equals(item)) {
                for (int i = 0; i < size; i++) {
                    dateEvent = allDateEvent.get(i);
                    String date = dateEvent.getE_date();
                    if (date.compareTo(thisDate) < 0) {
                        // 比thisDate小，删除该标记
                        App.deService.removeMark(dateEvent.getD_id());
                    }
                    // 更新进度
                    publishProgress(100 * i / size);
                }
            } else {
                for (int i = 0; i < size; i++) {
                    dateEvent = allDateEvent.get(i);
                    String date = dateEvent.getE_date();
                    if (date.compareTo(thisDate) > 0) {
                        // 比thisDate大，删除该标记
                        App.deService.removeMark(dateEvent.getD_id());
                    }
                    // 更新进度
                    publishProgress(100 * i / size);
                }
            }
        } else {
//            System.out.println("没有任何标记可被删除。");
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        listener.onProgress(values[0]);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        listener.onComplete();
    }
}
