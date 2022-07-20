package com.aprilz.tiny.vo;

import com.aprilz.tiny.mbg.entity.ApCoupon;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description: TODO
 * @author: aprilz
 * @since: 2022/7/15
 **/
@Data
public class CouponVo extends ApCoupon {

    @ApiModelProperty("用户ID")
    @TableField("user_id")
    private Long userId;
}
