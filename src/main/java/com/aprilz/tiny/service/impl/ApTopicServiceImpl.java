package com.aprilz.tiny.service.impl;

import com.aprilz.tiny.mapper.ApTopicMapper;
import com.aprilz.tiny.mbg.entity.ApTopic;
import com.aprilz.tiny.service.IApTopicService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 专题表 服务实现类
 * </p>
 *
 * @author aprilz
 * @since 2022-07-19
 */
@Service
public class ApTopicServiceImpl extends ServiceImpl<ApTopicMapper, ApTopic> implements IApTopicService {

    @Override
    public List<ApTopic> query(Integer offset, Integer limit) {
        LambdaQueryWrapper<ApTopic> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApTopic::getDeleteFlag, true)
                .orderByDesc(ApTopic::getCreateTime).last("limit " + offset + "," + limit);
        return this.list(queryWrapper);
    }
}
