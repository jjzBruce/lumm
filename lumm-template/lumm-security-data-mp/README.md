- 对业务表进行数据权限拦截，用户表等与权限相关的不进行数据权限拦截
- 每个用户都会有对应的数据权限范围，可能是多个，一般跟角色绑定在一起。数据权限范围可分为：全部、部门级、用户级
- 使用Mp对Mapper层进行拦截
