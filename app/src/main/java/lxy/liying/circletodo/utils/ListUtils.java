package lxy.liying.circletodo.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * =======================================================
 * 版权：©Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying
 * 日期：2016/7/29 16:01
 * 版本：1.0
 * 描述：List工具类
 * 备注：
 * =======================================================
 */
public class ListUtils {

    /**
     * 求两个List的差集
     *
     * @param bigList   较大的List
     * @param smallList 较小的List
     * @param <T>
     * @return
     */
    public static <T> List<T> subtract(List<T> bigList, List<T> smallList) {
        List<T> rltList = new ArrayList<>();
        if (bigList != null && bigList.size() > 0) {
            for (T t : bigList) {
                boolean isExists = smallList.contains(t);
                if (!isExists) {
                    rltList.add(t);
                }
            }
        }
        return rltList;
    }

    /**
     * 求一个List删除一个元素的结果
     *
     * @param list 原list
     * @param element 单个元素
     * @param <T>
     * @return
     */
    public static <T> List<T> subtract(List<T> list, T element) {
        list.remove(element);
        return list;
    }
}
