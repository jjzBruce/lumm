package com.lumm.tool.process.activiti5x.design;

import cn.hutool.core.collection.CollectionUtil;
import com.lumm.tool.process.activiti5x.ProcessDesigner;
import org.activiti.bpmn.BpmnAutoLayout;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.*;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;

import java.util.LinkedList;
import java.util.List;

/**
 * 抽象的基于Bpmn规范的流程设计器
 *
 * @author <a href="mailto:brucezhang_jjz@163.com">zhangj</a>
 */
public abstract class AbstractBpmnProcessDesigner implements ProcessDesigner {

    /**
     * 流程定义 Key
     */
    protected String processDefKey;

    /**
     * 流程定义名称
     */
    protected String procDefName;

    /**
     * 流程节点容器
     */
    private final List<FlowElement> flowElements = new LinkedList<>();

    /**
     * 构建
     * @param procDefKey
     * @param procDefName
     */
    protected AbstractBpmnProcessDesigner(String procDefKey, String procDefName) {
        this.processDefKey = procDefKey;
        this.procDefName = procDefName;
    }

    /**
     * 在 Process 中添加扩展元素
     *
     * @param process
     */
    protected void addExtraElementInProcess(Process process) {
        // sub
    }

    /**
     * 初始化结束节点
     *
     * @return EndEvent
     */
    protected EndEvent createEndEvent() {
        EndEvent endEvent = new EndEvent();
        endEvent.setId("end");
        endEvent.setName("结束");
        return endEvent;
    }

    /**
     * 初始化开始节点
     *
     * @return StartEvent
     */
    protected StartEvent createStartEvent() {
        StartEvent startEvent = new StartEvent();
        startEvent.setId("start");
        startEvent.setName("开始");
        return startEvent;
    }

    /**
     * 创建箭头
     *
     * @param from
     * @param to
     * @return SequenceFlow
     */
    protected SequenceFlow createSequenceFlow(String from, String to) {
        SequenceFlow flow = new SequenceFlow();
        flow.setSourceRef(from);
        flow.setTargetRef(to);
        return flow;
    }

    /**
     * 添加任务节点
     *
     * @param node
     */
    public void addTask(Task node) {
        this.flowElements.add(node);
    }

    @Override
    public boolean generator() {
        // 定义BPMN
        BpmnModel bpmnModel = new BpmnModel();
        // 定义流程
        Process process = new Process();
        process.setId(processDefKey);
        process.setName(procDefName);
        bpmnModel.addProcess(process);
        // 其他的扩展，子类实现
        addExtraElementInProcess(process);

        // 创建开始节点
        StartEvent startEvent = createStartEvent();
        process.addFlowElement(startEvent);
        // 创建结束节点
        EndEvent endEvent = createEndEvent();
        process.addFlowElement(endEvent);

        // 加入节点
        if(CollectionUtil.isNotEmpty(flowElements)) {
            flowElements.forEach(process::addFlowElement);
        }

        // 连线
        createSequenceFlows(process, startEvent, endEvent, flowElements);

        //BPMN自动布局
        BpmnAutoLayout bpmnAutoLayout = new BpmnAutoLayout(bpmnModel);
        bpmnAutoLayout.execute();

        // 部署
        // 准备服务
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        String procDefResourceName = processDefKey + ".bpmn20.xml";
        Deployment deployment = repositoryService.createDeployment()
                .addBpmnModel(procDefResourceName, bpmnModel)
                .name(procDefName)
                .deploy();

        return deployment != null && deployment.getId() != null;
    }

    /**
     * 连线，子类实现
     */
    protected abstract void createSequenceFlows(Process process, StartEvent startEvent, EndEvent endEvent,
                                                List<FlowElement> flowElements);


}
