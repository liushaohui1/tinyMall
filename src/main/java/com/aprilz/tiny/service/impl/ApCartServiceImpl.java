package com.aprilz.tiny.service.impl;

import com.aprilz.tiny.mapper.ApCartMapper;
import com.aprilz.tiny.mbg.entity.ApCart;
import com.aprilz.tiny.service.IApCartService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 购物车商品表 服务实现类
 * </p>
 *
 * @author aprilz
 * @since 2022-07-20
 */
@Service
public class ApCartServiceImpl extends ServiceImpl<ApCartMapper, ApCart> implements IApCartService {

}
