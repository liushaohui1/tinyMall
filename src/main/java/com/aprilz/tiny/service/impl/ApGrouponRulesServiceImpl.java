package com.aprilz.tiny.service.impl;

import com.aprilz.tiny.mapper.ApGrouponRulesMapper;
import com.aprilz.tiny.mbg.entity.ApGrouponRules;
import com.aprilz.tiny.service.IApGrouponRulesService;
import com.aprilz.tiny.vo.GrouponRuleVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * <p>
 * 团购规则表 服务实现类
 * </p>
 *
 * @author aprilz
 * @since 2022-07-19
 */
@Service
public class ApGrouponRulesServiceImpl extends ServiceImpl<ApGrouponRulesMapper, ApGrouponRules> implements IApGrouponRulesService {

    @Override
    public IPage<GrouponRuleVo> queryPage(Integer page, Integer size) {
        Page<GrouponRuleVo> pages = new Page(page, size);
        QueryWrapper<GrouponRuleVo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("u.delete_flag", true);
        queryWrapper.eq("g.delete_flag", true);
        queryWrapper.eq("u.status", 0);
        queryWrapper.gt("u.expire_time", new Date());
        queryWrapper.orderByDesc("u.create_time").orderByDesc("g.create_time");
        return this.baseMapper.queryPage(pages, queryWrapper);
    }
}
