package com.lumm.tool.process.activiti5x.design.node;

import com.lumm.tool.process.activiti5x.design.AbstractServiceNode;
import lombok.Getter;
import lombok.Setter;
import org.activiti.bpmn.model.ImplementationType;
import org.activiti.bpmn.model.ServiceTask;



/**
 * 表达式的服务节点
 *
 * @author <a href="mailto:brucezhang_jjz@163.com">zhangj</a>
 */
public class ExpressionServiceNode extends AbstractServiceNode {

    /**
     * 表达式
     */
    @Getter
    @Setter
    private String expression;

    @Override
    public ServiceTask createNode() {
        /*
        <serviceTask id="print" activiti:expression="#{printer.printMessage()}" />
         */
        ServiceTask serviceTask = super.createSimpleServiceNode();
        serviceTask.setImplementationType(ImplementationType.IMPLEMENTATION_TYPE_EXPRESSION);
        serviceTask.setImplementation(expression);
//        serviceTask.addAttribute(
//                ExtensionAttributeHelper.createActivitiExtensionAttribute(ATTRIBUTE_TASK_SERVICE_EXPRESSION, expression)
////                ExtensionAttributeHelper.createExtensionAttribute(ATTRIBUTE_TASK_SERVICE_EXPRESSION, expression)
//        );
        return serviceTask;
    }
}
