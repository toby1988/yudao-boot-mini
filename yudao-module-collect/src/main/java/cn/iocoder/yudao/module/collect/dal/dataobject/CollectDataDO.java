package cn.iocoder.yudao.module.collect.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 数据采集记录 DO
 *
 * @author 芋道源码
 */
@TableName("collect_data")
@KeySequence("collect_data_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CollectDataDO extends BaseDO {

    /**
     * 数据ID
     */
    @TableId
    private Long id;

    /**
     * 任务ID
     */
    private Long taskId;

    /**
     * 采集的数据内容(JSON格式)
     */
    private String dataContent;

    /**
     * 数据哈希值(用于去重)
     */
    private String dataHash;

    /**
     * 采集时间
     */
    private java.time.LocalDateTime collectTime;

    /**
     * 处理状态
     *
     * 枚举 {@link cn.iocoder.yudao.module.collect.enums.DataProcessStatusEnum}
     */
    private Integer processStatus;

    /**
     * 处理结果
     */
    private String processResult;

}