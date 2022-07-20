package com.aprilz.tiny.service.impl;

import com.aprilz.tiny.mapper.ApLogMapper;
import com.aprilz.tiny.mbg.entity.ApLog;
import com.aprilz.tiny.service.IApLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 操作日志表 服务实现类
 * </p>
 *
 * @author aprilz
 * @since 2022-07-20
 */
@Service
public class ApLogServiceImpl extends ServiceImpl<ApLogMapper, ApLog> implements IApLogService {

}
