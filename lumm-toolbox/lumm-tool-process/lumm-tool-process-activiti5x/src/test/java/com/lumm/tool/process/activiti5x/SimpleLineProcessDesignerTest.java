package com.lumm.tool.process.activiti5x;

import com.lumm.tool.process.activiti5x.design.SimpleLineProcessDesigner;
import com.lumm.tool.process.activiti5x.design.node.DelegateExpressionServiceNode;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


import java.util.Collections;

@Slf4j
public class SimpleLineProcessDesignerTest {

    @Before
    public void init() {
        ProcessEngineConfiguration.createStandaloneInMemProcessEngineConfiguration()
                .setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_CREATE_DROP)
                .setJdbcUrl("jdbc:h2:mem:my-own-db;DB_CLOSE_DELAY=1000")
                .setAsyncExecutorEnabled(true)
                .setAsyncExecutorActivate(false)
                .buildProcessEngine();
    }

    @Test
    public void testSimpleServiceProcess() {
        String procDefKey = "test-simple-service-task-process";
        String procDefName = "test-simple-service-task-process";
        SimpleLineProcessDesigner simpleLineProcessDesigner = new SimpleLineProcessDesigner(procDefKey, procDefName);

        // 简单的一个 表达式服务节点
        DelegateExpressionServiceNode delegateExpressionServiceNode = new DelegateExpressionServiceNode();
        delegateExpressionServiceNode.setNodeId("t-001").setNodeName("脚本服务节点");
        delegateExpressionServiceNode.setDelegateExpression("${simplePrinter}");
        simpleLineProcessDesigner.addTask(delegateExpressionServiceNode.createNode());

        simpleLineProcessDesigner.generator();

        // 启动
        ProcessInstance processInstance = ProcessEngineHelper.startProcess(procDefKey, Collections.emptyMap());
        System.out.println("processInstance: " + processInstance);
        // 查看流程历史变量
        HistoricProcessInstance historicProcessInstance = ProcessEngineHelper.getHistoricProcessInstance(processInstance.getProcessInstanceId());

        Assert.assertNotNull(historicProcessInstance.getEndTime());

    }


}
