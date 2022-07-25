package com.aprilz.tiny.service;

import com.aprilz.tiny.mbg.entity.ApAddress;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 收货地址表 服务类
 * </p>
 *
 * @author aprilz
 * @since 2022-07-18
 */
public interface IApAddressService extends IService<ApAddress> {

    void resetDefault(Long userId);
}
