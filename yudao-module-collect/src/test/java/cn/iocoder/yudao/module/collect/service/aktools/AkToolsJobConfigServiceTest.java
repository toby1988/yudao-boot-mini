package cn.iocoder.yudao.module.collect.service.aktools;

import cn.hutool.core.bean.BeanUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.test.core.ut.BaseDbUnitTest;
import cn.iocoder.yudao.module.collect.controller.admin.aktools.vo.jobconfig.AkToolsJobConfigCreateReqVO;
import cn.iocoder.yudao.module.collect.controller.admin.aktools.vo.jobconfig.AkToolsJobConfigPageReqVO;
import cn.iocoder.yudao.module.collect.controller.admin.aktools.vo.jobconfig.AkToolsJobConfigRespVO;
import cn.iocoder.yudao.module.collect.controller.admin.aktools.vo.jobconfig.AkToolsJobConfigUpdateReqVO;
import cn.iocoder.yudao.module.collect.dal.dataobject.aktools.AkToolsJobConfigDO;
import cn.iocoder.yudao.module.collect.dal.mysql.aktools.AkToolsJobConfigMapper;
import cn.iocoder.yudao.module.collect.service.aktools.impl.AkToolsJobConfigServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

import static cn.iocoder.yudao.framework.common.util.date.LocalDateTimeUtils.buildTime;
import static cn.iocoder.yudao.framework.test.core.util.AssertUtils.assertPojoEquals;
import static cn.iocoder.yudao.framework.test.core.util.RandomUtils.randomLongId;
import static cn.iocoder.yudao.framework.test.core.util.RandomUtils.randomPojo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * {@link AkToolsJobConfigService} 的单元测试类
 *
 * @author ToBy.Qoder
 */
@Import(AkToolsJobConfigServiceImpl.class)
public class AkToolsJobConfigServiceTest extends BaseDbUnitTest {

    @Resource
    private AkToolsJobConfigService jobConfigService;

    @MockBean
    private AkToolsJobConfigMapper jobConfigMapper;

    @MockBean
    private AkToolsTaskExecutorFactory executorFactory;

    @MockBean
    private AkToolsBatchCollectionService batchCollectionService;

    @Test
    public void testCreateJobConfig_success() {
        // 准备参数
        AkToolsJobConfigCreateReqVO reqVO = randomPojo(AkToolsJobConfigCreateReqVO.class, o -> {
            o.setApiEnum("STOCK_SPOT");
            o.setCronExpression("0 15 15 ? * MON-FRI");
            o.setStatus(1);
        });

        // 准备返回值
        AkToolsJobConfigDO jobConfig = BeanUtil.toBean(reqVO, AkToolsJobConfigDO.class);
        jobConfig.setId(randomLongId());
        when(jobConfigMapper.insert(any())).thenReturn(1);

        // 调用
        Long jobId = jobConfigService.createJobConfig(reqVO);

        // 断言
        assertNotNull(jobId);
        assertTrue(jobId > 0);
    }

    @Test
    public void testUpdateJobConfig_success() {
        // 准备参数
        AkToolsJobConfigUpdateReqVO reqVO = randomPojo(AkToolsJobConfigUpdateReqVO.class, o -> {
            o.setId(1L);
            o.setApiEnum("STOCK_VALUATION");
            o.setCronExpression("0 0 18 ? * MON-FRI");
        });

        // 准备返回值
        when(jobConfigMapper.selectById(1L)).thenReturn(randomPojo(AkToolsJobConfigDO.class));
        when(jobConfigMapper.updateById(any())).thenReturn(1);

        // 调用
        jobConfigService.updateJobConfig(reqVO);

        // 断言 - 不抛出异常即为成功
        assertDoesNotThrow(() -> jobConfigService.updateJobConfig(reqVO));
    }

    @Test
    public void testDeleteJobConfig_success() {
        // 准备参数
        Long id = 1L;

        // 准备返回值
        when(jobConfigMapper.selectById(id)).thenReturn(randomPojo(AkToolsJobConfigDO.class));
        when(jobConfigMapper.deleteById(id)).thenReturn(1);

        // 调用
        jobConfigService.deleteJobConfig(id);

        // 断言 - 不抛出异常即为成功
        assertDoesNotThrow(() -> jobConfigService.deleteJobConfig(id));
    }

    @Test
    public void testGetJobConfig_success() {
        // 准备参数
        Long id = 1L;

        // 准备返回值
        AkToolsJobConfigDO jobConfig = randomPojo(AkToolsJobConfigDO.class, o -> {
            o.setId(id);
            o.setCreateTime(buildTime(2023, 1, 15));
            o.setUpdateTime(buildTime(2023, 1, 15));
        });
        when(jobConfigMapper.selectById(id)).thenReturn(jobConfig);

        // 调用
        AkToolsJobConfigRespVO respVO = jobConfigService.getJobConfig(id);

        // 断言
        assertPojoEquals(jobConfig, respVO);
    }

    @Test
    public void testGetJobConfigPage_success() {
        // 准备参数
        AkToolsJobConfigPageReqVO reqVO = randomPojo(AkToolsJobConfigPageReqVO.class);

        // 准备返回值
        AkToolsJobConfigDO jobConfig = randomPojo(AkToolsJobConfigDO.class);
        PageResult<AkToolsJobConfigDO> pageResult = new PageResult<>(Arrays.asList(jobConfig), 1L);
        when(jobConfigMapper.selectPage(reqVO)).thenReturn(pageResult);

        // 调用
        PageResult<AkToolsJobConfigRespVO> result = jobConfigService.getJobConfigPage(reqVO);

        // 断言
        assertEquals(1, result.getTotal());
        assertEquals(1, result.getList().size());
        assertPojoEquals(jobConfig, result.getList().get(0));
    }

    @Test
    public void testGetAllEnabledConfigs_success() {
        // 准备返回值
        List<AkToolsJobConfigDO> configs = Arrays.asList(
            randomPojo(AkToolsJobConfigDO.class, o -> o.setStatus(1)),
            randomPojo(AkToolsJobConfigDO.class, o -> o.setStatus(1))
        );
        when(jobConfigMapper.selectAllEnabled()).thenReturn(configs);

        // 调用
        List<AkToolsJobConfigDO> result = jobConfigService.getAllEnabledConfigs();

        // 断言
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(config -> config.getStatus() == 1));
    }

    @Test
    public void testValidateCronExpression_success() {
        // 测试有效的Cron表达式
        assertTrue(jobConfigService.validateCronExpression("0 15 15 ? * MON-FRI"));
        assertTrue(jobConfigService.validateCronExpression("0 0 2 1 * ?"));
        
        // 测试无效的Cron表达式
        assertFalse(jobConfigService.validateCronExpression("invalid cron"));
        assertFalse(jobConfigService.validateCronExpression(""));
        assertFalse(jobConfigService.validateCronExpression(null));
    }

    @Test
    public void testGetNextExecuteTime_success() {
        // 测试有效的Cron表达式
        String result = jobConfigService.getNextExecuteTime("0 15 15 ? * MON-FRI");
        assertNotNull(result);
        assertNotEquals("无效的Cron表达式", result);
        
        // 测试无效的Cron表达式
        String invalidResult = jobConfigService.getNextExecuteTime("invalid cron");
        assertEquals("无效的Cron表达式", invalidResult);
    }

}