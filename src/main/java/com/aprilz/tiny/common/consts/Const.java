package com.aprilz.tiny.common.consts;

/**
 * @description: 公共定义变量
 * @author: liushaohui
 * @since: 2022/7/14
 **/
public class Const {
    // 状态，0删除 1可用 2下架
    public static final Integer TYPE_DELETE = 0;
    public static final Integer TYPE_USE = 1;
    public static final Integer TYPE_SOLDOUT = 2;

    //优惠券类型 优惠券赠送类型，如果是0则通用券，用户领取；如果是1，则是注册赠券；如果是2，则是优惠券码兑换；
    public static final Integer TYPE_COMMON = 0;
    public static final Integer TYPE_REGISTER = 1;
    public static final Integer TYPE_CODE = 2;


    public static final Integer TIME_TYPE_DAYS = 0;
    public static final Integer TIME_TYPE_TIME = 1;

}
