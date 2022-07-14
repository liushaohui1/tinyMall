package com.aprilz.tiny.dto;

import com.aprilz.tiny.mbg.entity.ApUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * SpringSecurity需要的用户详情
 * Created by aprilz on 2018/4/26.
 */
public class AdminUserDetails implements UserDetails {
    private ApUser apUser;
    //  private List<ApPermissionEntity> permissionList;

    public AdminUserDetails(ApUser apUser) {
        this.apUser = apUser;

    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        //前台不需要区分权限
        return null;
    }

    @Override
    public String getPassword() {
        return apUser.getPassword();
    }

    @Override
    public String getUsername() {
        return apUser.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return apUser.getDeleteFlag();
    }
}
