package com.lumm.web3.demo;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.junit.Before;
import org.junit.Test;

/**
 * 测试用例
 *
 * @author <a href="mailto:brucezhang_jjz@163.com">zhangjun</a>
 * @since 1.0.0
 */
@Slf4j
public class MainTest {

    private Main main;

    @Before
    public void setUp() {
        main = new Main();
    }

    /**
     * 新区块监听
     * @throws Exception
     */
    @Test
    public void runSimpleFilterExample() throws Exception {
        main.simpleFilterExample();
    }

    /**
     * 获取区块的信息
     * @throws Exception
     */
    @Test
    public void runBlockInfoExample() throws Exception {
        main.blockInfoExample();
    }

    /**
     * 交易事件的实例
     * @throws Exception
     */
    @Test
    public void runCountingEtherExample() throws Exception {
        main.countingEtherExample();
    }

    /**
     * 以太坊客户端版本实例
     * @throws Exception
     */
    @Test
    public void runClientVersionExample() throws Exception {
        main.clientVersionExample();
    }

}
