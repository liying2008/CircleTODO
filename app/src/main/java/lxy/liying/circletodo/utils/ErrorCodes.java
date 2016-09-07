package lxy.liying.circletodo.utils;

import java.util.HashMap;

/**
 * =======================================================
 * 版权：©Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying
 * 日期：2016/8/11 14:57
 * 版本：1.0
 * 描述：错误码与错误信息映射
 * 备注：
 * =======================================================
 */
public class ErrorCodes {
    public static HashMap<Integer, String> errorMsg = new HashMap<>(110);
    static {
        // AndroidSDK错误码列表
        errorMsg.put(9001, "Application Id为空");
        errorMsg.put(9002, "解析返回数据出错");
        errorMsg.put(9003, "上传文件出错");
        errorMsg.put(9004, "文件上传失败");
        errorMsg.put(9005, "批量操作只支持最多50条");
        errorMsg.put(9006, "objectId为空");
        errorMsg.put(9007, "文件大小超过10M");
        errorMsg.put(9008, "上传文件不存在");
        errorMsg.put(9009, "没有缓存数据");
        errorMsg.put(9010, "网络超时");
        errorMsg.put(9011, "不支持批量操作");
        errorMsg.put(9012, "上下文为空");
        errorMsg.put(9013, "数据表名称格式不正确");
        errorMsg.put(9014, "第三方账号授权失败");
        errorMsg.put(9015, "其他错误");
        errorMsg.put(9016, "无网络连接，请检查您的手机网络");
        errorMsg.put(9017, "与第三方登录有关的错误");
        errorMsg.put(9018, "参数不能为空");
        errorMsg.put(9019, "格式不正确");

        // HttpResponseCode为404时返回内容的详细说明
        errorMsg.put(101, "用户名(或邮箱)或密码不正确");
        errorMsg.put(102, "查询对应的字段值不匹配");
        errorMsg.put(103, " 非法的 class 名称");
        errorMsg.put(104, "关联的class名称不存在");
        errorMsg.put(105, "字段名是大小写敏感的");
        errorMsg.put(106, "不是一个正确的指针类型");
        errorMsg.put(107, "格式不正确");
        errorMsg.put(108, "用户名和密码是必需的");
        errorMsg.put(109, "邮箱和密码是必需的");

        errorMsg.put(111, "传入的字段值与字段类型不匹配");
        errorMsg.put(112, "requests的值必须是数组");
        errorMsg.put(113, "json对象格式不正确");
        errorMsg.put(114, "requests数组大于50");

        errorMsg.put(117, "纬度范围在[-90, 90] 或 经度范围在[-180, 180]");

        errorMsg.put(120, "邮箱认证功能开关未打开");

        errorMsg.put(131, "不正确的deviceToken");
        errorMsg.put(132, "不正确的installationId");
        errorMsg.put(133, "不正确的deviceType");
        errorMsg.put(134, "deviceToken已经存在");
        errorMsg.put(135, "installationId已经存在");
        errorMsg.put(136, "只读属性不能修改 或 android设备不需要设置deviceToken");

        errorMsg.put(138, "表是只读的");
        errorMsg.put(139, "角色名称格式不正确");

        errorMsg.put(141, "缺失推送需要的data参数");
        errorMsg.put(142, "时间格式应该如下： 2013-12-04 00:51:13");
        errorMsg.put(143, "必须是一个数字");
        errorMsg.put(144, "不能是之前的时间");
        errorMsg.put(145, "文件大小错误");
        errorMsg.put(146, "文件名错误");
        errorMsg.put(147, "文件分页上传偏移量错误");
        errorMsg.put(148, "文件上下文错误");
        errorMsg.put(149, "空文件");
        errorMsg.put(150, "文件上传错误");
        errorMsg.put(151, "文件删除错误");

        errorMsg.put(160, "图片错误");
        errorMsg.put(161, "图片模式错误");
        errorMsg.put(162, "图片宽度错误");
        errorMsg.put(163, "图片高度错误");
        errorMsg.put(164, "图片长边错误");
        errorMsg.put(165, "图片短边错误");

        errorMsg.put(201, "缺失数据");
        errorMsg.put(202, "用户名已经存在");
        errorMsg.put(203, "邮箱已经存在");
        errorMsg.put(204, "必须提供一个邮箱地址");
        errorMsg.put(205, "没有找到此邮件或用户名的用户");
        errorMsg.put(206, "登录用户才能修改自己的信息");
        errorMsg.put(207, "验证码错误");
        errorMsg.put(208, "authData不正确");
        errorMsg.put(209, "该手机号码已经存在");
        errorMsg.put(210, "旧密码不正确");

        errorMsg.put(301, "邮箱格式不正确");
        errorMsg.put(302, "后台设置了应用设置值");

        errorMsg.put(310, "云端逻辑运行错误的详细信息");
        errorMsg.put(311, "云端逻辑名称格式不正确");

        errorMsg.put(401, "唯一键不能存在重复的值");
        errorMsg.put(402, "查询的wher语句长度大于具体多少个字节");

        errorMsg.put(601, "不正确的BQL查询语句");
    }
}
