package cn.iocoder.yudao.module.collect.framework.aktools.enums;

/**
 * 枚举工具类
 * 提供通用的枚举操作方法
 *
 * @author ToBy.Qoder
 */
public class EnumUtils {

    /**
     * 根据编码获取枚举值
     *
     * @param enumClass 枚举类
     * @param code 编码值
     * @param <T> 枚举类型
     * @return 对应的枚举值，未找到返回 null
     */
    public static <T extends Enum<T> & CodeEnum> T getEnumByCode(Class<T> enumClass, Integer code) {
        if (code == null) {
            return null;
        }
        for (T enumConstant : enumClass.getEnumConstants()) {
            if (enumConstant.getCode().equals(code)) {
                return enumConstant;
            }
        }
        return null;
    }

    /**
     * 判断编码是否存在
     *
     * @param enumClass 枚举类
     * @param code 编码值
     * @param <T> 枚举类型
     * @return 是否存在
     */
    public static <T extends Enum<T> & CodeEnum> boolean exists(Class<T> enumClass, Integer code) {
        return getEnumByCode(enumClass, code) != null;
    }

    /**
     * 带编码的枚举接口
     */
    public interface CodeEnum {
        Integer getCode();
    }

}