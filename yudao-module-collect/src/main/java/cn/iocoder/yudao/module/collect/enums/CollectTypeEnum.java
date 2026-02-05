package cn.iocoder.yudao.module.collect.enums;

import cn.hutool.core.util.ArrayUtil;
import cn.iocoder.yudao.framework.common.core.IntArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 数据采集类型枚举
 *
 * @author 芋道源码
 */
@AllArgsConstructor
@Getter
public enum CollectTypeEnum implements IntArrayValuable {

    HTTP_INTERFACE(1, "HTTP接口"),
    DATABASE_QUERY(2, "数据库查询"),
    FILE_READ(3, "文件读取"),
    SYSTEM_METRICS(4, "系统指标");

    public static final int[] ARRAYS = Arrays.stream(values()).mapToInt(CollectTypeEnum::getType).toArray();

    /**
     * 类型
     */
    private final Integer type;
    /**
     * 名称
     */
    private final String name;

    @Override
    public int[] array() {
        return ARRAYS;
    }

    public static CollectTypeEnum valueOfType(Integer type) {
        return ArrayUtil.firstMatch(o -> o.getType().equals(type), values());
    }

}