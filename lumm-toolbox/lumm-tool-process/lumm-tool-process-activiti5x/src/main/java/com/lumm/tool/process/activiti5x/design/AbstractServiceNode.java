package com.lumm.tool.process.activiti5x.design;

import lombok.Data;
import lombok.experimental.Accessors;
import org.activiti.bpmn.model.ServiceTask;

/**
 * 抽象的服务节点
 *
 * @author <a href="mailto:brucezhang_jjz@163.com">zhangj</a>
 */
@Data
@Accessors(chain = true)
public abstract class AbstractServiceNode extends AbstractNode<ServiceTask> {

    protected ServiceTask createSimpleServiceNode() {
        ServiceTask serviceTask = new ServiceTask();
        serviceTask.setId(getNodeId());
        serviceTask.setName(getNodeName());
        serviceTask.setDocumentation(getNodeDocumentation());
        return serviceTask;
    }
}
