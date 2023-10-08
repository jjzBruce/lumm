package com.lumm.toolbox.pdf.html.freemaker;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Html模板相关处理工具
 *
 * @author <a href="mailto:brucezhang_jjz@163.com">zhangj</a>
 * @since 1.0.0
 */
public abstract class HtmlTemplateUtil {

    /**
     * 将所有的占位符替换为空格
     *
     * @param htmlContent Html文本内容
     * @return String
     */
    public static String replacePlaceholderEmpty(String htmlContent) {
        if (StrUtil.isBlank(htmlContent)) {
            return htmlContent;
        }
        Pattern pattern = Pattern.compile("\\$\\{[^}]+\\}");
        Matcher matcher = pattern.matcher(htmlContent);
        while (matcher.find()) {
            String match = matcher.group();
            htmlContent = StrUtil.replace(htmlContent, match, " ");
        }
        return htmlContent;
    }

    /**
     * 找到所有的占位符关键字
     *
     * @param htmlContent Html文本内容
     * @return String
     */
    public static List<String> findAllPlaceholderKeys(String htmlContent) {
        List<String> keys = new LinkedList<>();
        if (StrUtil.isBlank(htmlContent)) {
            return Collections.emptyList();
        }
        findPlaceholder(htmlContent, key -> keys.add(StrUtil.sub(key, 2, key.length() - 1)));
        return keys;
    }

    /**
     * 找到所有的占位符关键字，并组装为 json
     *
     * @param schemaKeys key规范
     * @return String
     */
    public static JSONObject schemaKeys2Json(String schemaKeys) {
        JSONObject jsonObject = new JSONObject();
        List<String> keys = StrUtil.split(schemaKeys, ',');
        keys.forEach(key -> {
            // 2. 条件处理组合为 json 中的 key
            // 2.1 如果存在 . ，则分开
            if (StrUtil.contains(key, '.')) {
                String[] ks = StrUtil.splitToArray(key, '.');
                JSONObject tmp = jsonObject;
                for (int i = 0; i < ks.length; i++) {
                    String s = ks[i];
                    if (!tmp.containsKey(s)) {
                        if (i < ks.length - 1) {
                            tmp.put(s, new JSONObject());
                        } else {
                            tmp.put(s, "");
                        }
                    }
                    tmp = tmp.getJSONObject(s);
                }
            } else {
                // todo 其他的默认情况不支持
                // 默认情况
                if (!jsonObject.containsKey(key)) {
                    jsonObject.put(key, "");
                }
            }
        });
        return jsonObject;
    }

    /**
     * 内部方法，找到占位符并执行回调函数
     */
    private static void findPlaceholder(String htmlContent, Consumer<String> callback) {
        Pattern pattern = Pattern.compile("\\$\\{[^}]+\\}");
        Matcher matcher = pattern.matcher(htmlContent);
        while (matcher.find()) {
            String match = matcher.group();
            callback.accept(match);
        }
    }

    public static final String VALIDATE_ERROR = "模板填充数据校验失败，请核对参数是否满足模板要求";


    /**
     * 校验json或者填充空的值
     *
     * @param schemaKeys 规范键
     * @param jsonParam  被校验的json
     * @param struct     是否严格，当严格校验不通过则报错，不严格会补空位
     */
    public static void validateOrFill(String schemaKeys, JSONObject jsonParam, boolean struct) {
        if (StrUtil.isBlank(schemaKeys)) {
            return;
        }
        if (jsonParam == null) {
            if (struct) {
                throw new IllegalStateException(VALIDATE_ERROR);
            } else {
                jsonParam = new JSONObject();
            }
        }

        String[] keys = StrUtil.splitToArray(schemaKeys, ',');
        for (String key : keys) {
            if (StrUtil.contains(key, '.')) {
                String[] ks = StrUtil.splitToArray(key, '.');
                JSONObject tmp = jsonParam;
                for (int i = 0; i < ks.length; i++) {
                    String s = ks[i];
                    if (!tmp.containsKey(s)) {
                        if (struct) {
                            throw new IllegalStateException(VALIDATE_ERROR);
                        } else {
                            if (i < ks.length - 1) {
                                tmp.put(s, new JSONObject());
                            } else {
                                tmp.put(s, "");
                            }
                        }
                    }
                    tmp = tmp.getJSONObject(s);
                }
            } else {
                if (!jsonParam.containsKey(key)) {
                    if (struct) {
                        throw new IllegalStateException(VALIDATE_ERROR);
                    } else {
                        jsonParam.put(key, "");
                    }
                }
            }
        }
    }

}
