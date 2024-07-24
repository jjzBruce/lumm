package com.lumm.dynamic.table.utils;

import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * DynamicTableNameUtils
 *
 * @author <a href="mailto:brucezhang_jjz@163.com">zhangjun</a>
 * @since 1.0.0
 */
public abstract class DynamicTableNameUtils {

    public static String parseTableName(String tableName, String paramExpression) {
        StandardEvaluationContext context = new StandardEvaluationContext();
        ExpressionParser parser = new SpelExpressionParser();
        context.setVariable("tableName", tableName);
        context.setVariable("tenant", resolveTenantId(UserContext.getTenantId()));
        Expression expr = parser.parseExpression(paramExpression, new TemplateParserContext());
        return expr.getValue(context, String.class);
    }

    private static String convertToBase36(long number, int minLength) {
        if (number == 0) {
            return "0".repeat(minLength);
        }

        StringBuilder base36 = new StringBuilder();
        while (number > 0) {
            long remainder = number % 36;
            char digit = (char) ((remainder < 10) ? ('0' + remainder) : ('a' + (remainder - 10)));
            base36.insert(0, digit);
            number /= 36;
        }

        // 填充零直到达到最小长度
        while (base36.length() < minLength) {
            base36.insert(0, '0');
        }

        return base36.toString();
    }

    private static String resolveTenantId(Long tenantId) {
        return convertToBase36(tenantId, 3);
    }

}
