package cn.iocoder.yudao.module.collect.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 配置类型枚举
 *
 * @author 芋道源码
 */
@AllArgsConstructor
@Getter
public enum ConfigTypeEnum {

    STRING("STRING", "字符串"),
    JSON("JSON", "JSON"),
    NUMBER("NUMBER", "数字"),
    BOOLEAN("BOOLEAN", "布尔值");

    /**
     * 类型
     */
    private final String type;
    /**
     * 名称
     */
    private final String name;

}