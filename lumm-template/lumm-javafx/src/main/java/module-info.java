module lumm.javafx {
    requires static lombok;

    requires java.sql;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;
    requires org.kordamp.bootstrapfx.core;
    requires spring.boot;
    requires org.slf4j;
    requires spring.boot.autoconfigure;
    requires spring.context;
    requires spring.beans;
    requires spring.core;

    opens com.lumm.javafx to spring.core, javafx.graphics,javafx.fxml;

    exports com.lumm.javafx;
}