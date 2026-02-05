package cn.iocoder.yudao.module.collect.dal.mysql.collect;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.collect.dal.dataobject.CollectConfigDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 数据采集配置 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface CollectConfigMapper extends BaseMapperX<CollectConfigDO> {

    default CollectConfigDO selectByKey(String configKey) {
        return selectOne(new LambdaQueryWrapperX<CollectConfigDO>()
                .eq(CollectConfigDO::getConfigKey, configKey));
    }

}