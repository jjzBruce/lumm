package com.lumm.javafx;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.kordamp.bootstrapfx.BootstrapFX;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Demo
 *
 * @author <a href="mailto:brucezhang_jjz@163.com">zhangjun</a>
 * @since 1.0.0
 */
@SpringBootApplication
@Slf4j
public class Demo extends Application {

    private static ConfigurableApplicationContext springContext;
    private static String[] savedArgs;

    @Override
    public void init() {
        // 在JavaFX初始化阶段启动Spring
        springContext = new SpringApplicationBuilder()
                .sources(Demo.class)
                .headless(false)
                .run(savedArgs);
    }

    @Override
    public void start(Stage primaryStage) {
        // 从Spring容器获取Bean（示例）
        // YourService service = springContext.getBean(YourService.class);

        Label label = new Label("SCADA");
        StackPane root = new StackPane(label);
        double width = 1100;
        double height = 800;
        Scene scene = new Scene(root, width, height);
        scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());

        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        double x = (bounds.getWidth() - width) / 2;
        double y = (bounds.getHeight() - height) / 2;

        primaryStage.setTitle("SCADA");
        primaryStage.setScene(scene);
        primaryStage.setX(x);
        primaryStage.setY(y);
        primaryStage.show();

        log.info("JavaFX UI started successfully");
    }

    @Override
    public void stop() {
        // 关闭Spring上下文
        springContext.close();
        Platform.exit();
        log.info("Application shutdown complete");
    }

    public static void main(String[] args) {
        savedArgs = args;
        // 只启动JavaFX，Spring会在init()阶段启动
        launch(args);
    }
}
