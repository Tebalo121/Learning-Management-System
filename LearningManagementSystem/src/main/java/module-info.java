module com.example.learningmanagementsystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.postgresql.jdbc;
    requires org.slf4j;
    requires jdk.javadoc;


    opens com.example.learningmanagementsystem to javafx.fxml;
    exports com.example.learningmanagementsystem;
    exports com.example.learningmanagementsystem.dao;
    opens com.example.learningmanagementsystem.dao to javafx.fxml;
}