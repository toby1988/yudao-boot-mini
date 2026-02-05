package cn.iocoder.yudao.module.collect.enums;

import cn.hutool.core.util.ArrayUtil;
import cn.iocoder.yudao.framework.common.core.IntArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 日志类型枚举
 *
 * @author 芋道源码
 */
@AllArgsConstructor
@Getter
public enum LogTypeEnum implements IntArrayValuable {

    EXECUTE_START(1, "执行开始"),
    EXECUTE_SUCCESS(2, "执行成功"),
    EXECUTE_FAILED(3, "执行失败"),
    DATA_PROCESS(4, "数据处理");

    public static final int[] ARRAYS = Arrays.stream(values()).mapToInt(LogTypeEnum::getType).toArray();

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

    public static LogTypeEnum valueOfType(Integer type) {
        return ArrayUtil.firstMatch(o -> o.getType().equals(type), values());
    }

}