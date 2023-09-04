package com.lumm.tool.process.activiti5x.design.util;

import cn.hutool.core.util.ArrayUtil;
import org.activiti.bpmn.model.ExtensionAttribute;
import org.activiti.bpmn.model.ExtensionElement;

import java.util.Arrays;

import static org.activiti.bpmn.constants.BpmnXMLConstants.*;

/**
 * ExtensionElement 工具
 *
 * @author <a href="mailto:brucezhang_jjz@163.com">zhangj</a>
 * @see ExtensionElement
 */
public abstract class ExtensionElementHelper {

    /**
     * 创建一个 Activiti 为前缀的扩展元素
     *
     * @param name
     * @param attributes
     * @return ExtensionElement
     */
    public static ExtensionElement createActivitiExtensionElement(String name, ExtensionAttribute... attributes) {
        ExtensionElement extensionElement = new ExtensionElement();
        extensionElement.setNamespace(ACTIVITI_EXTENSIONS_NAMESPACE);
        extensionElement.setName(name);
        if (ArrayUtil.isNotEmpty(attributes)) {
            Arrays.stream(attributes).forEach(extensionElement::addAttribute);
        }
        return extensionElement;
    }

    /**
     * 创建一个 Activiti 为前缀的扩展元素
     *
     * @param name
     * @return ExtensionElement
     */
    public static ExtensionElement createActivitiExtensionElement(String name, String fieldName, String stringValue) {
        ExtensionElement extensionElement = new ExtensionElement();
        extensionElement.setNamespace(ACTIVITI_EXTENSIONS_NAMESPACE);
        extensionElement.setName(name);
        extensionElement.addAttribute(ExtensionAttributeHelper.createExtensionAttribute(ATTRIBUTE_FIELD_NAME, fieldName));
        extensionElement.addAttribute(ExtensionAttributeHelper.createExtensionAttribute(ATTRIBUTE_FIELD_STRING, stringValue));
        return extensionElement;
    }

    /**
     * 创建一个扩展元素
     *
     * @param namespace
     * @param name
     * @param attributes
     * @return ExtensionElement
     */
    public static ExtensionElement createExtensionElement(String namespace, String name, ExtensionAttribute... attributes) {
        ExtensionElement extensionElement = new ExtensionElement();
        extensionElement.setNamespace(namespace);
        extensionElement.setName(name);
        if (ArrayUtil.isNotEmpty(attributes)) {
            Arrays.stream(attributes).forEach(extensionElement::addAttribute);
        }
        return extensionElement;
    }

}
