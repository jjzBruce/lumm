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
public class DelegateExpressionServiceNode extends AbstractServiceNode {

    /**
     * 表达式
     */
    @Getter
    @Setter
    private String delegateExpression;

    @Override
    public ServiceTask createNode() {
        /*
            <serviceTask id="taskForEntity" name="插入/更新单条数据" activiti:delegateExpression="${pageFlowDelegateService}">
            </serviceTask>
         */
        ServiceTask serviceTask = super.createSimpleServiceNode();
        serviceTask.setImplementationType(ImplementationType.IMPLEMENTATION_TYPE_DELEGATEEXPRESSION);
        serviceTask.setImplementation(delegateExpression);
        return serviceTask;
    }
}
