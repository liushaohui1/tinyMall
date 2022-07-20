package com.aprilz.tiny.controller;

import com.aprilz.tiny.common.api.CommonResult;
import com.aprilz.tiny.common.utils.UserUtil;
import com.aprilz.tiny.mbg.entity.ApCart;
import com.aprilz.tiny.mbg.entity.ApUser;
import com.aprilz.tiny.service.IApCartService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 购物车商品表 前端控制器
 * </p>
 *
 * @author aprilz
 * @since 2022-07-20
 */
@RestController
@RequestMapping("/cart")
public class ApCartController {

    @Resource
    private IApCartService cartService;

    /**
     * 购物车商品货品数量
     * <p>
     * 如果用户没有登录，则返回空数据。
     *
     * @return 购物车商品货品数量
     */
    @GetMapping("/goodsCount")
    public CommonResult goodsCount() {
        ApUser user = UserUtil.getUser();
        if (Objects.isNull(user)) {
            return CommonResult.success(0);
        }
        int goodsCount = 0;
        LambdaQueryWrapper<ApCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApCart::getUserId, user.getId()).eq(ApCart::getDeleteFlag, true);
        List<ApCart> cartList = cartService.list(queryWrapper);
        for (ApCart cart : cartList) {
            goodsCount += cart.getNumber();
        }
        return CommonResult.success(goodsCount);
    }

}
