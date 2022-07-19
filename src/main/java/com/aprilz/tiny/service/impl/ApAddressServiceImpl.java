package com.aprilz.tiny.service.impl;

import com.aprilz.tiny.mapper.ApAddressMapper;
import com.aprilz.tiny.mbg.entity.ApAddress;
import com.aprilz.tiny.service.IApAddressService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 收货地址表 服务实现类
 * </p>
 *
 * @author aprilz
 * @since 2022-07-18
 */
@Service
public class ApAddressServiceImpl extends ServiceImpl<ApAddressMapper, ApAddress> implements IApAddressService {

}
