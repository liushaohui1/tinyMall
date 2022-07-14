# tinyMall

Mybatis-Plus基础框架,包括Mybatis-Plus-Generator。 登录/注册,以及完整的RBAC权限管理系统。


github: https://github.com/liushaohui1/tinyMall.git

后期做 登录续期方案：
考虑小程序有效期续上, token失效后，用refreshToken去验证，有效则自动重新登录，续上。
refreshToken都过期，则需要重新wx授权登录。