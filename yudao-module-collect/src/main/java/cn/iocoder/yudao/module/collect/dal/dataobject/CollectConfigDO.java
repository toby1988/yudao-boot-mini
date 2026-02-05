package cn.iocoder.yudao.module.collect.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 数据采集配置 DO
 *
 * @author 芋道源码
 */
@TableName("collect_config")
@KeySequence("collect_config_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CollectConfigDO extends BaseDO {

    /**
     * 配置ID
     */
    @TableId
    private Long id;

    /**
     * 配置键
     */
    private String configKey;

    /**
     * 配置值
     */
    private String configValue;

    /**
     * 配置类型
     *
     * 枚举 {@link cn.iocoder.yudao.module.collect.enums.ConfigTypeEnum}
     */
    private String configType;

    /**
     * 备注
     */
    private String remark;

}