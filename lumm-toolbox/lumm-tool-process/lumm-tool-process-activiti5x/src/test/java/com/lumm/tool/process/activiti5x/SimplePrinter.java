package com.lumm.tool.process.activiti5x;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

import java.io.Serializable;

/**
 *
 * @author <a href="mailto:brucezhang_jjz@163.com">zhangj</a>
 */
public class SimplePrinter implements JavaDelegate, Serializable {

    private static final long serialVersionUID = -8657625557369844287L;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        System.out.println("are you ok?");
    }
}
