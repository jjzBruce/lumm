package com.lumm.securitydata.mp.interept;

import cn.hutool.core.util.EnumUtil;
import com.lumm.securitydata.mp.DataPerm;
import com.lumm.securitydata.mp.DataPermScope;
import com.lumm.securitydata.mp.MockSession;
import com.lumm.securitydata.mp.entity.User;
import com.lumm.securitydata.mp.service.DataPermUserService;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.HexValue;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.expression.operators.relational.ItemsList;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.PlainSelect;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 用户数据权限处理器，通过修改查询表达式达到控制权限的目的 <br/>
 * 1. 确定用户的数据权限范围（所有、部门、个人） <br/>
 * 2. 根据不同的范围对查询进行修改 <br/>
 *
 * @author zhangj
 * @since 1.0.0
 */
@Slf4j
@AllArgsConstructor
public class UserDataPermHandler {

    private DataPermUserService dataPermUserService;
    private MockSession mockSession;

    /**
     * 获取数据权限 SQL 片段
     *
     * @param plainSelect  查询对象
     * @param whereSegment 查询条件片段
     * @return JSqlParser 条件表达式
     */
    @SneakyThrows(Exception.class)
    public Expression getSqlSegment(PlainSelect plainSelect, String whereSegment) {
        // 待执行 SQL Where 条件表达式
        Expression where = plainSelect.getWhere();
        if (where == null) {
            where = new HexValue(" 1 = 1 ");
        }
        log.info("开始进行权限过滤,where: {},mappedStatementId: {}", where, whereSegment);
        //获取mapper名称
        String className = whereSegment.substring(0, whereSegment.lastIndexOf("."));
        //获取方法名
        String methodName = whereSegment.substring(whereSegment.lastIndexOf(".") + 1);
        Table fromItem = (Table) plainSelect.getFromItem();
        // 有别名用别名，无别名用表名，防止字段冲突报错
        Alias fromItemAlias = fromItem.getAlias();
        String mainTableName = fromItemAlias == null ? fromItem.getName() : fromItemAlias.getName();
        //获取当前mapper 的方法
        Method[] methods = Class.forName(className).getMethods();
        //遍历判断mapper 的所以方法，判断方法上是否有 DataPerm
        for (Method m : methods) {
            if (Objects.equals(m.getName(), methodName)) {
                DataPerm annotation = m.getAnnotation(DataPerm.class);
                if (annotation == null) {
                    return where;
                }
                // 1、当前用户Code
                String username = mockSession.getCurrentUser();
                // 2、当前角色即角色或角色类型（可能多种角色）
                List<String> roleTypeSet = dataPermUserService.currentUserRoleTypes();
                DataPermScope scopeType = DataPermScope.getDataPermScopeFromScopeCodes(roleTypeSet);
                if (scopeType != null) {
                    switch (scopeType) {
                        // 查看全部
                        case ALL:
                            return where;
                        case DEPT:
                            // 查看本部门用户数据
                            // 创建IN 表达式
                            // 创建IN 范围的元素集合
                            List<String> deptCodes = dataPermUserService.listDeptCodes(user.getId());
                            // 把集合转变为JSQLParser需要的元素列表
                            ItemsList deptList = new ExpressionList(deptCodes.stream().map(StringValue::new).collect(Collectors.toList()));
                            InExpression inExpressionDeptCodes = new InExpression(new Column(mainTableName + ".creator_code"), deptList);
                            return new AndExpression(where, inExpressionDeptCodes);
                        case MYSELF:
                            // 查看自己的数据
                            //  = 表达式
                            EqualsTo usesEqualsTo = new EqualsTo();
                            usesEqualsTo.setLeftExpression(new Column(mainTableName + ".creator_code"));
                            usesEqualsTo.setRightExpression(new StringValue(user.getCode()));
                            return new AndExpression(where, usesEqualsTo);
                        default:
                            break;
                    }
                }
            }

        }
        //说明无权查看，
        where = new HexValue(" 1 = 2 ");
        return where;
    }

}
