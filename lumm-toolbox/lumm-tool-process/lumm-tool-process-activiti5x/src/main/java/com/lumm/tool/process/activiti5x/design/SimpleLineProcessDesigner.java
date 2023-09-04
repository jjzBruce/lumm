package com.lumm.tool.process.activiti5x.design;

import cn.hutool.core.collection.CollectionUtil;
import org.activiti.bpmn.model.EndEvent;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.StartEvent;

import java.util.List;

/**
 * 简单线性流程设计器
 *
 * @author <a href="mailto:brucezhang_jjz@163.com">zhangj</a>
 */
public class SimpleLineProcessDesigner extends AbstractBpmnProcessDesigner {

    public SimpleLineProcessDesigner(String procDefKey, String procDefName) {
        super(procDefKey, procDefName);
    }

    /**
     * 连线
     */
    @Override
    protected void createSequenceFlows(Process process, StartEvent startEvent, EndEvent endEvent,
                                       List<FlowElement> flowElements) {
        // 开始 -> 按顺序遍历List -> 结束
        if (CollectionUtil.isEmpty(flowElements)) {
            process.addFlowElement(createSequenceFlow(startEvent.getId(), endEvent.getId()));
        } else {
            int size = flowElements.size();
            process.addFlowElement(createSequenceFlow(startEvent.getId(), flowElements.get(0).getId()));
            if (size > 1) {
                for (int i = 0; i < flowElements.size() - 1; i++) {
                    process.addFlowElement(createSequenceFlow(flowElements.get(i).getId(), flowElements.get(i + 1).getId()));
                }
            }
            process.addFlowElement(createSequenceFlow(flowElements.get(size - 1).getId(), endEvent.getId()));
        }
    }

}
