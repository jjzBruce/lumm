package com.lumm.tool.process.activiti5x.design;


import org.activiti.bpmn.model.Task;

/**
 * 节点接口，对应流程每个Task
 *
 * @author <a href="mailto:brucezhang_jjz@163.com">zhangj</a>
 * @since 1.0.0
 */
public interface TaskNode<T extends Task> {

    /**
     * 任务id
     *
     * @return String
     */
    String getNodeId();

    /**
     * 任务名称
     *
     * @return String
     */
    String getNodeName();

    /**
     * 任务文档
     *
     * @return String
     */
    String getNodeDocumentation();

    /**
     * 创建 任务
     *
     * @return
     */
    T createNode();

}
