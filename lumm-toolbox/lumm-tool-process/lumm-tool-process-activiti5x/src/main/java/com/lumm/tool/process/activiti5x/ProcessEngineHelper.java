package com.lumm.tool.process.activiti5x;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;

import java.util.List;
import java.util.Map;

/**
 * ProcessEngineHelper
 *
 * @author <a href="mailto:brucezhang_jjz@163.com">zhangj</a>
 */
public abstract class ProcessEngineHelper {

    public static ProcessDefinition getProcessDefinition(String processDefinitionId) {
        if (StrUtil.isBlank(processDefinitionId)) {
            return null;
        }
        return getProcessEngine().getRepositoryService().createProcessDefinitionQuery()
                .processDefinitionId(processDefinitionId)
                .singleResult();
    }

    /**
     * 启动流程
     *
     * @param processKey
     * @param startVariableMap
     * @return ProcessInstance
     */
    public static ProcessInstance startProcess(String processKey, Map<String, Object> startVariableMap) {
        return getProcessEngine().getRuntimeService().startProcessInstanceByKey(processKey, startVariableMap);
    }

    /**
     * 指派完成一个并行的任务
     *
     * @return ProcessInstance
     */
    public static boolean assignAndCompleteParallelMultiTask(String processInstanceId, String assignUserId) {
        Assert.notBlank(processInstanceId);
        Assert.notBlank(assignUserId);
        // 获取流程实例
        Task task = null;
        try {
            task = getProcessEngine().getTaskService().createTaskQuery().processInstanceId(processInstanceId).active().list().get(0);
        } catch (Throwable ignore) {
        }
        if (task == null) {
            return false;
        }

        TaskService taskService = getProcessEngine().getTaskService();
        taskService.setAssignee(task.getId(), assignUserId);
        taskService.complete(task.getId());
        return true;
    }

    /**
     * 获取历史流程实例
     *
     * @param processInstanceId
     * @return
     */
    public static HistoricProcessInstance getHistoricProcessInstance(String processInstanceId) {
        return getProcessEngine().getHistoryService().createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId).singleResult();
    }

    /**
     * 历史流程实例上下文变量值
     *
     * @param processInstanceId
     * @param key
     * @return
     */
    public static HistoricVariableInstance getHistoricVariableInstance(String processInstanceId, String key) {
        return ProcessEngines.getDefaultProcessEngine().getHistoryService().createHistoricVariableInstanceQuery()
                .processInstanceId(processInstanceId).variableName(key).singleResult();
    }

    public static List<HistoricVariableInstance> listHistoricVariableInstance(String processInstanceId) {
        return ProcessEngines.getDefaultProcessEngine().getHistoryService().createHistoricVariableInstanceQuery()
                .processInstanceId(processInstanceId).list();
    }

    private static ProcessEngine getProcessEngine() {
        return ProcessEngines.getDefaultProcessEngine();
    }

}
