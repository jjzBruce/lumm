package com.lumm.tool.process.activiti5x.design.util;

import org.activiti.bpmn.model.ExtensionAttribute;

import static org.activiti.bpmn.constants.BpmnXMLConstants.ACTIVITI_EXTENSIONS_NAMESPACE;
import static org.activiti.bpmn.constants.BpmnXMLConstants.ACTIVITI_EXTENSIONS_PREFIX;

/**
 * ExtensionAttribute 工具
 *
 * @author <a href="mailto:brucezhang_jjz@163.com">zhangj</a>
 */
public abstract class ExtensionAttributeHelper {

    /**
     * 创建一个扩展属性
     *
     * @param name
     * @param value
     * @return ExtensionAttribute
     */
    public static ExtensionAttribute createExtensionAttribute(String name, String value) {
        ExtensionAttribute extensionAttribute = new ExtensionAttribute();
        extensionAttribute.setName(name);
        extensionAttribute.setValue(value);
        return extensionAttribute;
    }

    /**
     * 创建 activiti:xxx  属性
     *
     * @param name
     * @param value
     * @return ExtensionAttribute
     */
    public static ExtensionAttribute createActivitiExtensionAttribute(String name, String value) {
        ExtensionAttribute extensionAttribute = new ExtensionAttribute();
        extensionAttribute.setNamespace(ACTIVITI_EXTENSIONS_NAMESPACE);
        extensionAttribute.setNamespacePrefix(ACTIVITI_EXTENSIONS_PREFIX);
        extensionAttribute.setName(name);
        extensionAttribute.setValue(value);
        return extensionAttribute;
    }

}
