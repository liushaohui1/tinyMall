package com.aprilz.tiny.controller;

import cn.hutool.core.util.StrUtil;
import com.aprilz.tiny.common.api.CommonResult;
import com.aprilz.tiny.common.utils.RegexUtil;
import com.aprilz.tiny.common.utils.UserUtil;
import com.aprilz.tiny.mbg.entity.ApAddress;
import com.aprilz.tiny.mbg.entity.ApUser;
import com.aprilz.tiny.service.IApAddressService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * <p>
 * 收货地址表 前端控制器
 * </p>
 *
 * @author aprilz
 * @since 2022-07-18
 */
@RestController
@RequestMapping("/address")
public class ApAddressController {

    @Resource
    private IApAddressService apAddressService;


    /**
     * 用户收货地址列表
     *
     * @return 收货地址列表
     */
    @GetMapping("/list")
    public CommonResult list() {
        ApUser user = UserUtil.getUser();
        LambdaQueryWrapper<ApAddress> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApAddress::getUserId, user.getId()).eq(ApAddress::getDeleteFlag, true);
        List<ApAddress> addressList = apAddressService.list(queryWrapper);
        return CommonResult.success(addressList);
    }

    /**
     * 收货地址详情
     *
     * @param id 收货地址ID
     * @return 收货地址详情
     */
    @GetMapping("/detail")
    public CommonResult detail(@NotNull Integer id) {
        ApUser user = UserUtil.getUser();
        LambdaQueryWrapper<ApAddress> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApAddress::getId, id).eq(ApAddress::getUserId, user.getId()).eq(ApAddress::getDeleteFlag, true);
        ApAddress address = apAddressService.getOne(queryWrapper);
        return CommonResult.success(address);
    }

    private CommonResult validate(ApAddress address) {
        String name = address.getName();
        if (StrUtil.isEmpty(name)) {
            return CommonResult.validateFailed();
        }

        // 测试收货手机号码是否正确
        String mobile = address.getTel();
        if (StrUtil.isEmpty(mobile)) {
            return CommonResult.validateFailed();
        }
        if (!RegexUtil.isMobileSimple(mobile)) {
            return CommonResult.validateFailed();
        }

        String province = address.getProvince();
        if (StrUtil.isEmpty(province)) {
            return CommonResult.validateFailed();
        }

        String city = address.getCity();
        if (StrUtil.isEmpty(city)) {
            return CommonResult.validateFailed();
        }

        String county = address.getCounty();
        if (StrUtil.isEmpty(county)) {
            return CommonResult.validateFailed();
        }


        String areaCode = address.getAreaCode();
        if (StrUtil.isEmpty(areaCode)) {
            return CommonResult.validateFailed();
        }

        String detailedAddress = address.getAddressDetail();
        if (StrUtil.isEmpty(detailedAddress)) {
            return CommonResult.validateFailed();
        }

        Boolean isDefault = address.getIsDefault();
        if (isDefault == null) {
            return CommonResult.validateFailed();
        }
        return null;
    }

    /**
     * 添加或更新收货地址
     *
     * @param address 用户收货地址
     * @return 添加或更新操作结果
     */
    @PostMapping("/save")
    public CommonResult save(@RequestBody ApAddress address) {
        ApUser user = UserUtil.getUser();
        CommonResult error = validate(address);
        if (error != null) {
            return error;
        }

        if (address.getId() == null || new Long("0").equals(address.getId())) {
            if (address.getIsDefault()) {
                // 重置其他收货地址的默认选项
                apAddressService.resetDefault(user.getId());
            }

            address.setId(null);
            address.setUserId(user.getId());
            apAddressService.save(address);
        } else {
            LambdaQueryWrapper<ApAddress> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ApAddress::getId, address.getId()).eq(ApAddress::getUserId, user.getId()).eq(ApAddress::getDeleteFlag, true);
            ApAddress apAddress = apAddressService.getOne(queryWrapper);
            if (apAddress == null) {
                return CommonResult.error();
            }

            if (address.getIsDefault()) {
                // 重置其他收货地址的默认选项
                apAddressService.resetDefault(user.getId());
            }

            address.setUserId(user.getId());
            apAddressService.updateById(apAddress);
        }
        return CommonResult.success(address.getId());
    }

    /**
     * 删除收货地址
     *
     * @param address 用户收货地址，{ id: xxx }
     * @return 删除操作结果
     */
    @PostMapping("/delete")
    public CommonResult delete(@RequestBody ApAddress address) {
        ApUser user = UserUtil.getUser();
        Long id = address.getId();
        if (id == null) {
            return CommonResult.validateFailed();
        }
        LambdaQueryWrapper<ApAddress> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApAddress::getId, id).eq(ApAddress::getUserId, user.getId()).eq(ApAddress::getDeleteFlag, true);
        ApAddress apAddress = apAddressService.getOne(queryWrapper);
        if (apAddress == null) {
            return CommonResult.error();
        }

        apAddressService.removeById(id);
        return CommonResult.success();
    }


}
