module FBGApp {
	requires java.base;
    requires java.mail;
    requires java.sql;
    requires jdatepicker;
    requires java.desktop;
	requires org.jfree.jfreechart;
	requires org.apache.commons.csv;
    requires org.postgresql.jdbc;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.swing;
    exports view;
    exports app;
    //requires org.apache.commons.csv;

}