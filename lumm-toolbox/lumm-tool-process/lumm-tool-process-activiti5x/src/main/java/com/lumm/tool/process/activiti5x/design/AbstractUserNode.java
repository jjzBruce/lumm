package com.lumm.tool.process.activiti5x.design;

import lombok.Data;
import lombok.experimental.Accessors;
import org.activiti.bpmn.model.UserTask;

/**
 * 抽象的用户节点
 *
 * @author <a href="mailto:brucezhang_jjz@163.com">zhangj</a>
 */
@Data
@Accessors(chain = true)
public abstract class AbstractUserNode extends AbstractNode<UserTask> {

    protected UserTask createSimpleUserNode() {
        UserTask userTask = new UserTask();
        userTask.setId(getNodeId());
        userTask.setName(getNodeName());
        userTask.setDocumentation(getNodeDocumentation());
        return userTask;
    }
}
