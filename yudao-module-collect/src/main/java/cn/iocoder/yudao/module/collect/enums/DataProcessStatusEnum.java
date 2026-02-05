package cn.iocoder.yudao.module.collect.enums;

import cn.hutool.core.util.ArrayUtil;
import cn.iocoder.yudao.framework.common.core.IntArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 数据处理状态枚举
 *
 * @author 芋道源码
 */
@AllArgsConstructor
@Getter
public enum DataProcessStatusEnum implements IntArrayValuable {

    NOT_PROCESSED(0, "未处理"),
    PROCESSING(1, "处理中"),
    PROCESSED(2, "已处理"),
    PROCESS_FAILED(3, "处理失败");

    public static final int[] ARRAYS = Arrays.stream(values()).mapToInt(DataProcessStatusEnum::getStatus).toArray();

    /**
     * 状态
     */
    private final Integer status;
    /**
     * 名称
     */
    private final String name;

    @Override
    public int[] array() {
        return ARRAYS;
    }

    public static DataProcessStatusEnum valueOfStatus(Integer status) {
        return ArrayUtil.firstMatch(o -> o.getStatus().equals(status), values());
    }

}