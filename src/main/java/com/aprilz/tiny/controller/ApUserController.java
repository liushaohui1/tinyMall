package com.aprilz.tiny.controller;

import com.aprilz.tiny.common.api.CommonResult;
import com.aprilz.tiny.dto.ApAdminLoginParam;
import com.aprilz.tiny.mbg.entity.ApTest;
import com.aprilz.tiny.mbg.entity.ApUser;
import com.aprilz.tiny.service.IApTestService;
import com.aprilz.tiny.service.IApUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

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
@Api("会员登录注册管理")
public class ApUserController {


    @Autowired
    private IApUserService userService;

    @Autowired
    private IApTestService testService;

    @Value("${jwt.tokenHead}")
    private String tokenHead;

    /**
     * @param loginParam
     * @return com.aprilz.tiny.common.api.CommonResult
     * @description 账号登录
     * @since 2022/7/14
     **/
    @PostMapping("/login")
    public CommonResult login(@Valid @RequestBody ApAdminLoginParam loginParam) {
        String token = userService.login(loginParam.getUsername(), loginParam.getPassword());
        if (token == null) {
            return CommonResult.validateFailed("用户名或密码错误");
        }
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", token);
        tokenMap.put("tokenHead", tokenHead);
        return CommonResult.success(tokenMap);
    }

    @ApiOperation(value = "test")
    @GetMapping(value = "/test")
    public  String test(){
        ApTest apTest = new ApTest();
        apTest.setUsername("aaa");
        testService.save(apTest);
        return  "success";
    }


}
