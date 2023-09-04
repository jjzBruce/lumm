package com.lumm.tool.process.activiti5x.design;

import lombok.Data;
import lombok.experimental.Accessors;
import org.activiti.bpmn.model.Task;

/**
 * 节点接口，对应流程每个Task
 * @author <a href="mailto:brucezhang_jjz@163.com">zhangj</a>
 */
@Data
@Accessors(chain = true)
public abstract class AbstractNode<T extends Task> implements TaskNode<T> {

    /**
     * 任务id
     */
    protected String nodeId;

    /**
     * 任务名称
     */
    protected String nodeName;

    /**
     * 任务文档
     */
    protected String nodeDocumentation;



}
