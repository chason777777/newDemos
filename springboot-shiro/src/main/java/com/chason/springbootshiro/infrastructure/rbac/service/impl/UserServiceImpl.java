package com.chason.springbootshiro.infrastructure.rbac.service.impl;

import com.chason.springbootshiro.infrastructure.rbac.entity.*;
import com.chason.springbootshiro.infrastructure.rbac.repository.*;
import com.chason.springbootshiro.infrastructure.rbac.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Service("userService")
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private UserRoleMapper userRoleMapper;
    @Autowired
    private PermissionMapper permissionMapper;
    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    @Override
    public User getUser(String username) {
        if (StringUtils.isEmpty(username)) {
            return null;
        }

        UserExample example = new UserExample();
        UserExample.Criteria criteria = example.createCriteria();
        criteria.andUsernameEqualTo(username);
        List<User> list = userMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }

        return list.get(0);
    }

    @Override
    public List<Role> getRoleList(String username) {
        if (StringUtils.isEmpty(username)) {
            return null;
        }

        RoleExample example = new RoleExample();
        RoleExample.Criteria criteria = example.createCriteria();
        criteria.andRolenameIn(getRolenames(username));

        return roleMapper.selectByExample(example);
    }

    @Override
    public List<Permission> getPermissionList(String rolename) {
        if (StringUtils.isEmpty(rolename)) {
            return null;
        }

        PermissionExample example = new PermissionExample();
        PermissionExample.Criteria criteria = example.createCriteria();
        criteria.andPermissionnameIn(getPermissionnames(rolename));

        return permissionMapper.selectByExample(example);
    }

    private List<String> getRolenames(String username) {
        if (StringUtils.isEmpty(username)) {
            return null;
        }

        UserRoleExample example = new UserRoleExample();
        UserRoleExample.Criteria criteria = example.createCriteria();
        criteria.andUsernameEqualTo(username);
        List<UserRole> list = userRoleMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }

        List<String> rolenames = new ArrayList<String>();
        for (UserRole userRole : list) {
            rolenames.add(userRole.getRolename());
        }
        return rolenames;
    }

    private List<String> getPermissionnames(String rolename) {
        if (StringUtils.isEmpty(rolename)) {
            return null;
        }

        RolePermissionExample example = new RolePermissionExample();
        RolePermissionExample.Criteria criteria = example.createCriteria();
        criteria.andRolenameEqualTo(rolename);
        List<RolePermission> list = rolePermissionMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }

        List<String> permissionnames = new ArrayList<String>();
        for (RolePermission rolePermission : list) {
            permissionnames.add(rolePermission.getPermissionname());
        }
        return permissionnames;
    }
}
