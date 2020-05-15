package com.chason.springbootshiro.infrastructure.rbac.service;

import com.chason.springbootshiro.infrastructure.rbac.entity.Permission;
import com.chason.springbootshiro.infrastructure.rbac.entity.Role;
import com.chason.springbootshiro.infrastructure.rbac.entity.User;

import java.util.List;

public interface UserService {
    User getUser(String username);

    List<Role> getRoleList(String username);

    List<Permission> getPermissionList(String rolename);
}
