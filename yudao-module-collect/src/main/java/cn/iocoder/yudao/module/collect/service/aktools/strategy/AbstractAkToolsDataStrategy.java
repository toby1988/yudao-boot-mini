package cn.iocoder.yudao.module.collect.service.aktools.strategy;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.module.collect.framework.aktools.enums.AkApiEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 抽象股票数据采集策略基类
 * 提供通用的数据处理逻辑
 *
 * @param <T> 返回的数据类型
 * @author ToBy.Qoder
 */
@Slf4j
public abstract class AbstractAkToolsDataStrategy<T> implements AkToolsDataStrategy<T> {

    @Override
    public Map<String, Object> buildRequestParams(Object param) {
        // 默认实现：将参数转换为Map
        if (param instanceof Map) {
            return (Map<String, Object>) param;
        }
        return Map.of();
    }

    /**
     * 批量转换数据列表
     *
     * @param rawList 原始数据列表
     * @param context 转换上下文
     * @return 转换后的数据列表
     */
    protected List<T> convertDataList(List<?> rawList, ConversionContext context) {
        if (CollUtil.isEmpty(rawList)) {
            log.warn("[convertDataList][原始数据为空] api: {}", getApiEnum().getApiPath());
            return List.of();
        }

        return rawList.stream()
            .map(item -> {
                try {
                    return convertData(item, context);
                } catch (Exception e) {
                    log.error("[convertDataList][数据转换失败] api: {}, item: {}, error: {}", 
                        getApiEnum().getApiPath(), item, e.getMessage(), e);
                    return null;
                }
            })
            .filter(item -> item != null)
            .collect(Collectors.toList());
    }

    /**
     * 安全转换单个数据项
     *
     * @param rawData 原始数据
     * @param context 转换上下文
     * @return 转换后的数据，失败返回null
     */
    protected T safeConvert(Object rawData, ConversionContext context) {
        try {
            return convertData(rawData, context);
        } catch (Exception e) {
            log.error("[safeConvert][数据转换失败] api: {}, data: {}, error: {}", 
                getApiEnum().getApiPath(), rawData, e.getMessage(), e);
            return null;
        }
    }

    /**
     * 验证API枚举是否匹配
     *
     * @param apiEnum API枚举
     * @throws IllegalArgumentException 当API枚举不匹配时抛出
     */
    protected void validateApiEnum(AkApiEnum apiEnum) {
        if (!getApiEnum().equals(apiEnum)) {
            throw new IllegalArgumentException(
                String.format("API枚举不匹配，期望: %s, 实际: %s", 
                    getApiEnum().getApiPath(), apiEnum.getApiPath()));
        }
    }

}