package com.aprilz.tiny.service;

import com.aprilz.tiny.mbg.entity.ApBrand;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 品牌商表 服务类
 * </p>
 *
 * @author aprilz
 * @since 2022-07-19
 */
public interface IApBrandService extends IService<ApBrand> {

    List<ApBrand> query(Integer offset, Integer limit);
}
