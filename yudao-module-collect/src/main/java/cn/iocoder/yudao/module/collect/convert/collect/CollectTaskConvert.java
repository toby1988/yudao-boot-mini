package cn.iocoder.yudao.module.collect.convert.collect;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.collect.controller.admin.collect.vo.task.CollectTaskCreateReqVO;
import cn.iocoder.yudao.module.collect.controller.admin.collect.vo.task.CollectTaskRespVO;
import cn.iocoder.yudao.module.collect.controller.admin.collect.vo.task.CollectTaskUpdateReqVO;
import cn.iocoder.yudao.module.collect.dal.dataobject.CollectTaskDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface CollectTaskConvert {

    CollectTaskConvert INSTANCE = Mappers.getMapper(CollectTaskConvert.class);

    CollectTaskDO convert(CollectTaskCreateReqVO bean);

    CollectTaskDO convert(CollectTaskUpdateReqVO bean);

    CollectTaskRespVO convert(CollectTaskDO bean);

    List<CollectTaskRespVO> convertList(List<CollectTaskDO> list);

    PageResult<CollectTaskRespVO> convertPage(PageResult<CollectTaskDO> page);

}