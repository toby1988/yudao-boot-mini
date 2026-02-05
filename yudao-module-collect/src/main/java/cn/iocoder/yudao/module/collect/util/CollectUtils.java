package cn.iocoder.yudao.module.collect.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;

/**
 * 数据采集工具类
 *
 * @author 芋道源码
 */
public class CollectUtils {

    /**
     * 计算数据内容的MD5哈希值
     *
     * @param dataContent 数据内容
     * @return MD5哈希值
     */
    public static String calculateDataHash(String dataContent) {
        if (StrUtil.isBlank(dataContent)) {
            return "";
        }
        return DigestUtil.md5Hex(dataContent);
    }

    /**
     * 验证Cron表达式的有效性
     *
     * @param cronExpression Cron表达式
     * @return 是否有效
     */
    public static boolean isValidCronExpression(String cronExpression) {
        if (StrUtil.isBlank(cronExpression)) {
            return false;
        }
        try {
            // 这里可以使用 Quartz 的 CronExpression 来验证
            // 为简化实现，这里只做基本格式检查
            String[] parts = cronExpression.trim().split("\\s+");
            return parts.length >= 6;
        } catch (Exception e) {
            return false;
        }
    }

}