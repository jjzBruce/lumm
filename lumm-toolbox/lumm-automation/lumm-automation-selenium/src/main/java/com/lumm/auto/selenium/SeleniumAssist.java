package com.lumm.auto.selenium;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.openqa.selenium.chrome.ChromeDriverService.CHROME_DRIVER_EXE_PROPERTY;

/**
 * 使用Selenium爬取数据的辅助类
 */
@Slf4j
public class SeleniumAssist {

    /**
     * 驱动
     */
    @Getter
    private WebDriver webDriver;

    /**
     * 驱动路径
     */
    private String driverPath;

    private Random random = new Random();

    private Actions action;

    /**
     * 延迟的事件， 默认为 5 秒
     */
    private Wait<WebDriver> waitGet;
    private final Duration waitDuration;

    public SeleniumAssist() {
        this(Duration.ofSeconds(5));
    }

    public SeleniumAssist(Duration waitDuration) {
        this.waitDuration = waitDuration;
    }

    /**
     * 初始化驱动 - chrom 浏览器
     *
     * @return
     */
    public void initChromeDriver(String driverPath) {
        this.driverPath = driverPath;
        System.getProperties().setProperty(CHROME_DRIVER_EXE_PROPERTY, driverPath);

        // fix Invalid Status code=403 text=Forbidden
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--remote-allow-origins=*");
        // 该行代码会将禁用Chrome自动化控制功能的选项添加到ChromeOptions对象中，并在创建ChromeDriver实例时应用该选项，以确保自动化测试可以稳定地运行而不被检测到。
        chromeOptions.addArguments("--disable-blink-features=AutomationControlled");
        // 这些选项旨在防止Chrome检测到自动化测试，并阻止它弹出警告框或限制自动化操作。
        chromeOptions.setExperimentalOption("useAutomationExtension", false);
        chromeOptions.setExperimentalOption("excludeSwitches", Arrays.asList("enable-automation"));
        this.webDriver = new ChromeDriver(chromeOptions);
        this.waitGet = new WebDriverWait(webDriver, this.waitDuration);

        // 解决使用selenium-java被检测导致滑块验证失败
        // https://blog.csdn.net/weixin_43746495/article/details/109636917
        ((JavascriptExecutor) webDriver).executeScript("Object.defineProperties(navigator,{ webdriver:{ get: () => false } })");
        this.action = new Actions(webDriver);
    }

    /**
     * 是否存在某元素
     *
     * @param by
     * @return
     */
    public boolean hasElement(WebElement ele, By by) {
        try {
            ele.findElement(by);
            return true;
        } catch (NoSuchElementException e1) {
            return false;
        }
    }

    /**
     * 是否存在某元素
     *
     * @param by
     * @return
     */
    public boolean hasElement(By by) {
        try {
            webDriver.findElement(by);
            return true;
        } catch (NoSuchElementException e1) {
            return false;
        }
    }

    /**
     * 是否存在任一元素
     *
     * @param bys
     * @return
     */
    public boolean hasAnyElement(By... bys) {
        try {
            for (By by : bys) {
                webDriver.findElement(by);
                return true;
            }
        } catch (NoSuchElementException ignore) {
        }
        return false;
    }

    /**
     * 判断某个元素是否生效的
     *
     * @param by
     * @return
     */
    public boolean isEnable(By by) {
        if (!hasElement(by)) {
            return false;
        }
        WebElement element = webDriver.findElement(by);
        return element.isEnabled();
    }

    /**
     * 判断某个元素是否显示的
     *
     * @param element
     * @return
     */
    public boolean isShow(WebElement element) {
        try {
            return !element.getCssValue("display").equals("none");
        } catch (Throwable e) {
            return false;
        }
    }

    /**
     * 判断某个元素是否显示的
     *
     * @param by
     * @return
     */
    public boolean isShow(By by) {
        try {
            return isShow(webDriver.findElement(by));
        } catch (Throwable e) {
            return false;
        }
    }

    /**
     * 使元素的 display: none -> display: block
     *
     * @param cssSelector
     * @return
     */
    public void makeShow(String cssSelector) {
        try {
            WebElement element = webDriver.findElement(By.cssSelector(cssSelector));
            if (!isShow(element)) {
                String js = "document.querySelector('" + cssSelector + "').style.display='block';";
                ((JavascriptExecutor) webDriver).executeScript(js);
            }
        } catch (Throwable ignore) {
            // do nothing
        }
    }

    /**
     * 找到第一个满足条件的元素
     *
     * @param bys
     * @return
     */
    public WebElement getFirstOne(By... bys) {
        try {
            for (By by : bys) {
                return webDriver.findElement(by);
            }
        } catch (NoSuchElementException ignore) {
        }
        return null;
    }

    /**
     * 如果存在则获取
     *
     * @param by
     * @return
     */
    public WebElement getIfHasExist(By by) {
        if (hasElement(by)) {
            return webDriver.findElement(by);
        } else {
            return null;
        }
    }

    /**
     * 如果存在则获取
     *
     * @param by
     * @return
     */
    public List<WebElement> listIfExist(By by) {
        if (hasElement(by)) {
            return webDriver.findElements(by);
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * 安全点击 <br/>
     * 如果点击出现 ElementClickInterceptedException 异常，尝试三次加宽 300 <br/>
     * 如果还是出现 ElementClickInterceptedException 异常，尝试全屏后点击
     * 如果还是出现 ElementClickInterceptedException 异常，返回false
     *
     * @param element
     */
    public boolean clickSafe(WebElement element, Integer... args) {
        int resizeWidth = 0;
        int resizeFullScreen = 0;
        if (args != null) {
            if (args.length > 0 && args[0] != null) {
                resizeWidth = args[0];
            }
            if (args.length > 1 && args[1] != null) {
                resizeFullScreen = args[1];
            }
        }
        if (element.isEnabled() && isShow(element)) {
            try {
                // 模拟鼠标移到中间点击

                action.moveToElement(element);
                action.click();
                action.perform();

                return true;
            } catch (ElementClickInterceptedException e) {
                // 尝试加大浏览器宽度
                if (resizeWidth < 3) {
                    Dimension size = webDriver.manage().window().getSize();
                    log.debug("点击元素[{}]被遮挡，调整浏览器宽度，再次点击", element);
                    Dimension newSize = new Dimension(size.width + 300, size.height);
                    webDriver.manage().window().setSize(newSize);
                    clickSafe(element, resizeWidth + 1, 0);
                } else if (resizeFullScreen < 1) {
                    log.debug("点击元素[{}]被遮挡，调整浏览器为全屏，再次点击", element);
                    webDriver.manage().window().fullscreen();
                    clickSafe(element, resizeWidth, resizeFullScreen + 1);
                } else {
                    return false;
                }
            } catch (ElementNotInteractableException ignore) {
                log.debug("按钮不可点击");
            }
        }
        return false;
    }

    /**
     * 安全点击，直到某一个条件成立便退出。
     * 解决某些时候点击无效的情况
     *
     * @param element     点击的元素
     * @param finish      结束条件
     * @param maxTryCount 最大尝试次数
     */
    public void clickSafeUntil(WebElement element, Predicate<SeleniumAssist> finish, int maxTryCount) {
        for (int i = 0; i < maxTryCount; i++) {
            clickSafe(element);
            if (finish.test(this)) {
                // 退出条件成立，退出操作
                return;
            }
            log.debug("尝试重新点击按钮: {}", element);
            sleep(1000);
        }
        if (!finish.test(this)) {
            // 尝试最多次还是没有效果，抛出异常
            throw new IllegalStateException("点击了按钮但是没有达到预期效果!");
        }
    }

    /**
     * 安全点击，直到某一个条件成立便退出。
     * 解决某些时候点击无效的情况
     *
     * @param by          点击的元素
     * @param finish      结束条件
     * @param maxTryCount 最大尝试次数
     */
    public void clickSafeUntil(By by, Predicate<SeleniumAssist> finish, int maxTryCount) {
        clickSafeUntil(webDriver.findElement(by), finish, maxTryCount);
    }

    /**
     * 安全点击
     *
     * @param by
     */
    public void clickSafe(By by) {
        clickSafe(webDriver.findElement(by));
    }


    /**
     * 延迟获取组件，延迟时长是 waitGet 。 如果获取失败，尝试查找当前页面是否存在校验，如果有解决校验再次获取
     *
     * @param by
     * @return
     */
    @SneakyThrows
    public WebElement delayedGetWebElement(By by) {
        return delayedGetWebElement(by, b -> webDriver.findElement(b), null);
    }

    /**
     * 延迟获取组件，延迟时长是 waitGet 。 如果获取失败，尝试查找当前页面是否存在校验，如果有解决校验再次获取
     *
     * @param by
     * @return
     */
    @SneakyThrows
    public WebElement delayedGetWebElement(By by, Supplier<WebElement> whenTimeOutReturn) {
        return delayedGetWebElement(by, b -> webDriver.findElement(b), whenTimeOutReturn);
    }

    /**
     * 延迟获取组件，延迟时长是 waitGet 。 如果获取失败，尝试查找当前页面是否存在校验，如果有解决校验再次获取
     *
     * @param by 查询条件
     * @return
     */
    @SneakyThrows
    public List<WebElement> delayedGetWebElements(By by) {
        return delayedGetWebElement(by, b -> webDriver.findElements(b), null);
    }

    /**
     * 延迟获取组件，延迟时长是 waitGet 。 如果获取失败，尝试查找当前页面是否存在校验，如果有解决校验再次获取
     *
     * @param by 查询条件
     * @return
     */
    @SneakyThrows
    public List<WebElement> delayedGetWebElements(By by, Supplier<List<WebElement>> whenTimeOutReturn) {
        return delayedGetWebElement(by, b -> webDriver.findElements(b), whenTimeOutReturn);
    }

    /**
     * 延迟获取某元素，提供正常获取与获取不到时候的异常处理
     *
     * @param by                元素坐标
     * @param normalGet         正常获取
     * @param whenTimeOutReturn 当超时的异常情况
     * @param <T>
     * @return
     */
    public <T> T delayedGetWebElement(By by, Function<By, T> normalGet, Supplier<T> whenTimeOutReturn) {
        try {
            log.info("延迟获取元素[{}]", by);
            try {
                waitGet.until(d -> d.findElement(by).isDisplayed());
            } catch (TimeoutException timeoutException) {
                log.info("延迟获取元素[{}]超时 1次", by);
                // 尝试解决验证
                passSlidingValidateIfNeed();

                // 如果遇到获取补到的情况，且异常处理存在，则观察是否是异常情况的处理逻辑
                if (whenTimeOutReturn != null) {
                    log.info("延迟获取元素[{}]未能获取到，但存在其他的情况", by);
                    return whenTimeOutReturn.get();
                } else {
                    throw timeoutException;
                }
            }
            // 正常返回
            return normalGet.apply(by);
        } catch (TimeoutException e) {
            log.info("延迟获取元素[{}]超时 2次", by);
            // 第二次尝试
            // 尝试解决验证
            passSlidingValidateIfNeed();

            waitGet.until(d -> d.findElement(by).isDisplayed());
            return normalGet.apply(by);
        }
    }

    /**
     * 延迟获取组件列表，并执行操作
     *
     * @param by              元素坐标
     * @param callback        找到元素的回调函数
     * @param timeOutCallback 超时的回调
     */
    public void delayOneEleCallback(By by, Consumer<WebElement> callback, Consumer<SeleniumAssist> timeOutCallback) {
        try {
            log.debug("延迟查找元素[{}]", by);
            try {
                waitGet.until(d -> d.findElement(by).isDisplayed());
            } catch (TimeoutException timeoutException) {
                log.info("延迟查找元素[{}]超时 1次", by);
                // 尝试解决验证
                passSlidingValidateIfNeed();

                // 如果遇到获取补到的情况，且异常处理存在，则观察是否是异常情况的处理逻辑
                if (timeOutCallback != null) {
                    log.info("延迟获取元素[{}]未能获取到，执行超时回调", by);
                    timeOutCallback.accept(this);
                } else {
                    throw timeoutException;
                }
            }
            oneEleCallback(by, callback);
        } catch (TimeoutException e) {
            log.info("延迟查找元素[{}]超时 2次", by);
            // 第二次尝试
            // 尝试解决验证
            passSlidingValidateIfNeed();

            waitGet.until(d -> d.findElement(by).isDisplayed());
            oneEleCallback(by, callback);
        }
    }

    private void oneEleCallback(By by, Consumer<WebElement> callback) {
        WebElement ele = webDriver.findElement(by);
        String styles = this.getCssStyle(ele);
        // 高亮
        this.highlightEle(ele);
        // 执行
        callback.accept(ele);
        // 还原
        this.setCssStyle(ele, styles);
    }

    /**
     * 延迟获取组件列表，并执行操作
     *
     * @param by              元素坐标
     * @param callback        找到元素的回调函数
     * @param timeOutCallback 超时的回调
     */
    public void delayListElesCallback(By by, Consumer<List<WebElement>> callback, Consumer<SeleniumAssist> timeOutCallback) {
        try {
            log.debug("延迟查找元素[{}]", by);
            try {
                waitGet.until(d -> d.findElement(by).isDisplayed());
            } catch (TimeoutException timeoutException) {
                log.info("延迟查找元素[{}]超时 1次", by);
                // 尝试解决验证
                passSlidingValidateIfNeed();

                // 如果遇到获取补到的情况，且异常处理存在，则观察是否是异常情况的处理逻辑
                if (timeOutCallback != null) {
                    log.info("延迟获取元素[{}]未能获取到，执行超时回调", by);
                    timeOutCallback.accept(this);
                } else {
                    throw timeoutException;
                }
            }
            listElesCallback(by, callback);
        } catch (TimeoutException e) {
            log.info("延迟查找元素[{}]超时 2次", by);
            // 第二次尝试
            // 尝试解决验证
            passSlidingValidateIfNeed();

            waitGet.until(d -> d.findElement(by).isDisplayed());
            listElesCallback(by, callback);
        }
    }

    private void listElesCallback(By by, Consumer<List<WebElement>> callback) {
        List<WebElement> eles = webDriver.findElements(by);
        List<String> styles = eles.stream().map(this::getCssStyle).collect(Collectors.toList());
        // 高亮
        eles.forEach(this::highlightEle);
        // 执行
        callback.accept(eles);
        // 还原
        for (int i = 0; i < eles.size(); i++) {
            this.setCssStyle(eles.get(i), styles.get(i));
        }
    }

    /**
     * 获取元素 style
     *
     * @param ele
     */
    private String getCssStyle(WebElement ele) {
        return ele.getAttribute("style");
    }

    /**
     * 高亮元素
     *
     * @return void
     */
    private void highlightEle(WebElement ele) {
        // 边框高亮并点击
        setCssStyle(ele, "background: orange;");
    }

    /**
     * 设置元素
     *
     * @return void
     */
    private void setCssStyle(WebElement ele, String style) {
        // 边框高亮并点击
        ((JavascriptExecutor) webDriver).executeScript("arguments[0].setAttribute('style', arguments[1]);",
                ele, style);
    }


    /**
     * 解决滑动验证
     */
    public void passSlidingValidateIfNeed() {
        while (isShow(By.id("waf_nc_block")) || isShow(By.id("PC"))) {
            log.debug("发现滑动验证");
            if (hasElement(By.id("`nc_1_refresh1`"))) {
                Actions action = new Actions(webDriver);
                WebElement nc_1_refresh1 = webDriver.findElement(By.id("`nc_1_refresh1`"));
                action.click(nc_1_refresh1).perform();
            }

            if (!hasAnyElement(By.id("nc_1_n1z"), By.id("nc_2_n1z"))) {
                sleep(1000);
                continue;
            }

            // 元素拖动
            //找到我们所要拖动的元素A
            WebElement source = getFirstOne(By.id("nc_1_n1z"), By.id("nc_2_n1z"));
            Actions action = new Actions(webDriver);

            // source-要拖动的元素A，target-拖动元素A到达的目标元素
            // source-要拖动的元素A,拖动元素移动多少,
            // 标准以元素A左上角为准,拖动元素相对元素A移到右边是x是正值，
            // 左边是负值，拖动元素相对元素A移到上边是y是负值，下边是正值，
            // 往右拖动
            try {
                action.moveToElement(source);
                action.clickAndHold();
                action.moveByOffset(300, 0);
                action.release();
                action.perform();
            } catch (Throwable ignore) {
                // 忽略
            }
            sleep(1000);
        }
    }

    /**
     * 休息，500ms
     */
    public void sleep500() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 休息，500ms
     */
    public void sleep(long sleep) {
        try {
            Thread.sleep(sleep);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 休息，5秒内随机
     */
    public void sleepRandom1_5() {
        try {
            Thread.sleep(random.nextInt(5000) % (5000 - 1000 + 1) + 1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


}
