package com.lumm.tool.process.activiti5x.design.node;

import cn.hutool.core.util.StrUtil;
import com.lumm.tool.process.activiti5x.design.AbstractUserNode;
import lombok.Data;
import lombok.experimental.Accessors;
import org.activiti.bpmn.model.MultiInstanceLoopCharacteristics;
import org.activiti.bpmn.model.UserTask;

/**
 * 并行的用户节点
 *
 * @author <a href="mailto:brucezhang_jjz@163.com">zhangj</a>
 */
@Data
@Accessors(chain = true)
public class ParallelUsersTaskNode extends AbstractUserNode {

    public static final String PROCESS_HANDLER_LIST = "_processHandlerList";
    public static final String PROCESS_HANDLER = "_processHandler";

    /**
     * 表单key
     */
    private String formKey;

    @Override
    public UserTask createNode() {

        /*
        <userTask id="taskForComplete" name="人工任务" activiti:formKey="${formKey}"
                  activiti:assignee="${r'${taskForComplete_processHandler}'}">
            <incoming>Flow_06m2gir</incoming>
            <outgoing>Flow_03sw65t</outgoing>
            <multiInstanceLoopCharacteristics activiti:collection="taskForComplete_processHandlerList"
                                              activiti:elementVariable="taskForComplete_processHandler">
                <completionCondition xsi:type="tFormalExpression">
                    ${r'${nrOfCompletedInstances/nrOfInstances &gt;= 1}'}
                </completionCondition>
            </multiInstanceLoopCharacteristics>
        </userTask>
         */

        UserTask userTask = createSimpleUserNode();
        if (StrUtil.isNotBlank(formKey)) {
            userTask.setFormKey(formKey);
        }
        // 设置并行
        MultiInstanceLoopCharacteristics loopCharacteristics = new MultiInstanceLoopCharacteristics();
        loopCharacteristics.setSequential(false);
        // 完成条件
        loopCharacteristics.setCompletionCondition("${nrOfCompletedInstances >= nrOfInstances}");
        // 处理人接受参数
        loopCharacteristics.setElementVariable(userTask.getId() + PROCESS_HANDLER);

        // 处理人集合参数
        loopCharacteristics.setInputDataItem(userTask.getId() + PROCESS_HANDLER_LIST);
        userTask.setLoopCharacteristics(loopCharacteristics);
        return userTask;
    }
}
