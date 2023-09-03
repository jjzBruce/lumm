package com.lumm.web3.demo;

import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import lombok.extern.slf4j.Slf4j;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Demo
 *
 * @author <a href="mailto:brucezhang_jjz@163.com">zhangjun</a>
 * @since 1.0.0
 */
@Slf4j
public class Main {

    private static final int COUNT = 2;

    private final Web3j web3j;

    public Main() {
        // defaults to http://localhost:8545/
        web3j = Web3j.build(new HttpService());
    }

    private void run() throws Exception {
        log.info("新区块监听实例");
        simpleFilterExample();

        log.info("新区块信息打印实例");
        blockInfoExample();

        log.info("Doing countingEtherExample");
        countingEtherExample();

        log.info("Doing clientVersionExample");
        clientVersionExample();

        // we explicitly call the exit to clean up our ScheduledThreadPoolExecutor used by web3j
        System.exit(0);
    }

    public static void main(String[] args) throws Exception {
        new Main().run();
    }

    /**
     * 新区块监听
     * @throws Exception
     */
    void simpleFilterExample() throws Exception {
        // 创建一个可观察流（Observable Flowable）来监听区块链上的新区块
        Disposable subscription = web3j.blockFlowable(false)
                // RxJava操作符，订阅了上面创建的区块流
                .subscribe(block -> log.info("新区块[{}] 已经被创建了。", block.getBlock().getNumber()), Throwable::printStackTrace);

        TimeUnit.MINUTES.sleep(2);
        // 取消对区块流的订阅，从而停止监听新区块事件。
        subscription.dispose();
    }

    /**
     * 获取区块的信息
     * @throws Exception
     */
    void blockInfoExample() throws Exception {
        // 栅栏等待
        CountDownLatch countDownLatch = new CountDownLatch(COUNT);

        log.info("等待 {} 条交易...", COUNT);
        // 监听新区块的产生，true：表示包括pending状态的区块。
        Disposable subscription = web3j.blockFlowable(true)
                // 限制获取的区块数量
                .take(COUNT)
                // 订阅回调
                .subscribe(ethBlock -> {
                    // 获取区块对象
                    EthBlock.Block block = ethBlock.getBlock();
                    // 获取区块对象中的信息
                    LocalDateTime timestamp = Instant.ofEpochSecond(
                            block.getTimestamp()
                                    .longValueExact()).atZone(ZoneId.of("UTC")).toLocalDateTime();
                    int transactionCount = block.getTransactions().size();
                    String hash = block.getHash();
                    String parentHash = block.getParentHash();
                    // 打印信息
                    log.info("{} Tx count: {}, Hash: {}, Parent hash: {}", timestamp, transactionCount, hash, parentHash);
                    // 栅栏等待计数减1
                    countDownLatch.countDown();
                }, Throwable::printStackTrace);

        // 等待线程都走完
        countDownLatch.await(10, TimeUnit.MINUTES);
        // 取消订阅操作
        subscription.dispose();
    }

    /**
     * 交易事件的实例
     * @throws Exception
     */
    void countingEtherExample() throws Exception {
        // 栅栏等待
        CountDownLatch countDownLatch = new CountDownLatch(1);

        log.info("等待 {} 条交易...", COUNT);
        // 创建了一个Single对象，它代表一个单一的值，即交易总价值
        Single<BigInteger> transactionValue = web3j.transactionFlowable()
                // 限制获取的交易数量
                .take(COUNT)
                // 做映射转换
                .map(Transaction::getValue)
                // 进行累加
                .reduce(BigInteger.ZERO, BigInteger::add);

        // 订阅了上述交易总价值的Single对象
        Disposable subscription = transactionValue.subscribe(total -> {
            BigDecimal value = new BigDecimal(total);
            log.info("交易金额: 以太币 ({} Wei)" , Convert.fromWei(value, Convert.Unit.ETHER));
            // 栅栏等待计数减1
            countDownLatch.countDown();
        }, Throwable::printStackTrace);

        // 等待线程
        countDownLatch.await(10, TimeUnit.MINUTES);
        // 取消订阅操作
        subscription.dispose();
    }

    /**
     * 以太坊客户端版本实例
     * @throws Exception
     */
    void clientVersionExample() throws Exception {
        CountDownLatch countDownLatch = new CountDownLatch(1);

        Disposable subscription = web3j.web3ClientVersion().flowable().subscribe(x -> {
            log.info("客户端版本: {}", x.getWeb3ClientVersion());
            countDownLatch.countDown();
        });

        countDownLatch.await();
        subscription.dispose();
    }

}
