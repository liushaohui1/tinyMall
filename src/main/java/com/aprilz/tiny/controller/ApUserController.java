package com.aprilz.tiny.controller;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.hutool.core.util.StrUtil;
import com.aprilz.tiny.common.api.CommonResult;
import com.aprilz.tiny.common.api.ResultCode;
import com.aprilz.tiny.common.cache.Cache;
import com.aprilz.tiny.common.cache.CachePrefix;
import com.aprilz.tiny.common.exception.ServiceException;
import com.aprilz.tiny.common.utils.IpUtils;
import com.aprilz.tiny.common.utils.JwtTokenUtil;
import com.aprilz.tiny.dto.AdminUserDetails;
import com.aprilz.tiny.dto.ApAdminLoginParam;
import com.aprilz.tiny.dto.UserInfo;
import com.aprilz.tiny.dto.WxLoginParam;
import com.aprilz.tiny.mbg.entity.ApUser;
import com.aprilz.tiny.service.IApUserService;
import com.aprilz.tiny.vo.Token;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author aprilz
 * @since 2022-07-13
 */
@RestController
@RequestMapping("/auth")
@Api(tags = "会员登录注册管理")
public class ApUserController {
    @Autowired
    private IApUserService userService;

    @Resource
    private WxMaService wxService;

    @Value("${jwt.tokenHead}")
    private String tokenHead;

    @Value("${jwt.tokenHeader}")
    private String tokenHeader;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private Cache cache;

    /**
     * @param loginParam
     * @return com.aprilz.tiny.common.api.CommonResult
     * @description 账号登录
     * @since 2022/7/14
     **/
    @PostMapping("/login")
    public CommonResult login(@Valid @RequestBody ApAdminLoginParam loginParam) {
        Token token = userService.login(loginParam.getUsername(), loginParam.getPassword());
        if (token == null) {
            return CommonResult.validateFailed("用户名或密码错误");
        }
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", tokenHead + " " + token.getToken());
        tokenMap.put("refreshToken", tokenHead + " " + token.getRefreshToken());
        // tokenMap.put("tokenHead", tokenHead);
        return CommonResult.success(tokenMap);
    }


    /**
     * 微信登录
     *
     * @param wxLoginParam 请求内容，{ code: xxx, userInfo: xxx }
     * @param request      请求对象
     * @return 登录结果
     */
    @PostMapping("/wxLogin")
    public CommonResult wxLogin(@RequestBody WxLoginParam wxLoginParam, HttpServletRequest request) {
        String code = wxLoginParam.getCode();
        UserInfo userInfo = wxLoginParam.getUserInfo();
        if (code == null || userInfo == null) {
            return CommonResult.error(ResultCode.PARAMS_ERROR);
        }

        String sessionKey = null;
        String openId = null;
        try {
            WxMaJscode2SessionResult result = this.wxService.getUserService().getSessionInfo(code);
            sessionKey = result.getSessionKey();
            openId = result.getOpenid();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (sessionKey == null || openId == null) {
            return CommonResult.error();
        }

        LambdaQueryWrapper<ApUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApUser::getWxOpenid, openId).eq(ApUser::getStatus, true);
        ApUser user = userService.getOne(queryWrapper);
        if (Objects.isNull(user)) {
            user = new ApUser();
            user.setUsername(openId);
            user.setPassword(openId);
            user.setWxOpenid(openId);
            user.setAvatar(userInfo.getAvatarUrl());
            user.setNickname(userInfo.getNickName());
            user.setGender(userInfo.getGender().intValue());
            user.setUserLevel(0);
            user.setLastLoginTime(new Date());
            user.setLastLoginIp(IpUtils.getIpAddress(request));
            user.setSessionKey(sessionKey);

            userService.save(user);

            // 新用户发送注册优惠券
            //couponAssignService.assignForRegister(user.getId());
        } else {
            user.setLastLoginTime(new Date());
            user.setLastLoginIp(IpUtils.getIpAddress(request));
            user.setSessionKey(sessionKey);
            if (!userService.updateById(user)) {
                throw new ServiceException();
            }
        }

        // token
        Token tokens = jwtTokenUtil.generateToken(new AdminUserDetails(user));

        Map<Object, Object> result = new HashMap<Object, Object>();
        result.put("token", tokenHead + " " + tokens.getToken());
        //result.put("tokenHead", tokenHead);
        result.put("userInfo", userInfo);
        return CommonResult.success(result);
    }


    @PostMapping("logout")
    public CommonResult logout(HttpServletRequest request) {
        String authHeader = request.getHeader(this.tokenHeader);
        if (StrUtil.isNotBlank(authHeader)) {
            String authToken = authHeader.substring(this.tokenHead.length()).trim();
            cache.remove(CachePrefix.AUTH_TOKEN + authToken);
        }
        return CommonResult.success("success");
    }


}
