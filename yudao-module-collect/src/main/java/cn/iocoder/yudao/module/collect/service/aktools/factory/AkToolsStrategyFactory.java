package cn.iocoder.yudao.module.collect.service.aktools.factory;

import cn.iocoder.yudao.module.collect.framework.aktools.enums.AkApiEnum;
import cn.iocoder.yudao.module.collect.service.aktools.strategy.AkToolsDataStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * AkTools 数据策略工厂
 * 负责管理和获取不同类型的数据采集策略
 *
 * @author ToBy.Qoder
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AkToolsStrategyFactory {

    private final List<AkToolsDataStrategy<?>> strategies;
    private final Map<AkApiEnum, AkToolsDataStrategy<?>> strategyMap = new EnumMap<>(AkApiEnum.class);

    @PostConstruct
    public void init() {
        // 初始化策略映射
        for (AkToolsDataStrategy<?> strategy : strategies) {
            AkApiEnum apiEnum = strategy.getApiEnum();
            if (strategyMap.containsKey(apiEnum)) {
                log.warn("[init][重复的策略定义] api: {}", apiEnum.getApiPath());
            }
            strategyMap.put(apiEnum, strategy);
            log.info("[init][注册策略] api: {}, strategy: {}", 
                apiEnum.getApiPath(), strategy.getClass().getSimpleName());
        }
    }

    /**
     * 根据API枚举获取对应的策略
     *
     * @param apiEnum API枚举
     * @param <T> 返回的数据类型
     * @return 数据采集策略
     * @throws IllegalArgumentException 当找不到对应策略时抛出
     */
    @SuppressWarnings("unchecked")
    public <T> AkToolsDataStrategy<T> getStrategy(AkApiEnum apiEnum) {
        AkToolsDataStrategy<?> strategy = strategyMap.get(apiEnum);
        if (strategy == null) {
            throw new IllegalArgumentException(
                String.format("未找到API对应的策略: %s", apiEnum.getApiPath()));
        }
        return (AkToolsDataStrategy<T>) strategy;
    }

    /**
     * 检查是否存在指定API的策略
     *
     * @param apiEnum API枚举
     * @return 是否存在对应策略
     */
    public boolean hasStrategy(AkApiEnum apiEnum) {
        return strategyMap.containsKey(apiEnum);
    }

    /**
     * 获取所有已注册的API枚举
     *
     * @return API枚举列表
     */
    public List<AkApiEnum> getRegisteredApis() {
        return List.copyOf(strategyMap.keySet());
    }

    /**
     * 获取策略数量
     *
     * @return 策略数量
     */
    public int getStrategyCount() {
        return strategyMap.size();
    }

}