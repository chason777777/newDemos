package com.chason.springbootshiro.infrastructure.shiro;

import com.chason.springbootshiro.infrastructure.rbac.entity.Permission;
import com.chason.springbootshiro.infrastructure.rbac.entity.Role;
import com.chason.springbootshiro.infrastructure.rbac.entity.User;
import com.chason.springbootshiro.infrastructure.rbac.service.UserService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import sun.security.util.Password;

import javax.annotation.Resource;

/**
 * Created by Administrator on 2017/12/11.
 * 自定义权限匹配和账号密码匹配
 */
public class MyShiroRealm extends AuthorizingRealm {
    @Resource
    private UserService userService;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        User user = (User) principals.getPrimaryPrincipal();
        for (Role role : userService.getRoleList(user.getUsername())) {
            authorizationInfo.addRole(role.getRolename());
            for (Permission p : userService.getPermissionList(role.getRolename())) {
                authorizationInfo.addStringPermission(p.getPermissionname());
            }
        }
        return authorizationInfo;
    }

    /*主要是用来进行身份认证的，也就是说验证用户输入的账号和密码是否正确。*/
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token)
            throws AuthenticationException {
        UsernamePasswordToken upToken = (UsernamePasswordToken) token;
        //获取用户的输入的账号.
        String username = upToken.getUsername();
        //通过username从数据库中查找 User对象，如果找到，没找到.
        //实际项目中，这里可以根据实际情况做缓存，如果不做，Shiro自己也是有时间间隔机制，2分钟内不会重复执行该方法
        User user = userService.getUser(username);
//        System.out.println("----->>userInfo="+userInfo);
        if (user == null) {
            throw new UnknownAccountException();
        }
        if (user.getStatus() == 0) { //账户冻结
            throw new LockedAccountException();
        }
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
                upToken.getUsername(), //用户名
                user.getPassword(), //密码
                getName()  //realm name
        );
        return authenticationInfo;
    }

}
