package com.aprilz.tiny.common.utils;

import com.aprilz.tiny.dto.AdminUserDetails;
import com.aprilz.tiny.mbg.entity.ApUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @description: TODO
 * @author: liushaohui
 * @since: 2022/7/15
 **/
public class UserUtil {

    public static ApUser getUser() {
        try {
            Authentication authentication =
                    SecurityContextHolder.getContext().getAuthentication();
            //基于认证对象获取用户身份信息
            AdminUserDetails principal = (AdminUserDetails)authentication.getPrincipal();
            return principal.getApUser();
        } catch (Exception e) {
            return  null;
        }

    }
}
