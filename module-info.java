module FBGApp {
	requires java.base;
    requires java.mail;
    requires java.sql;
    requires jdatepicker;
    requires java.desktop;
	requires org.jfree.jfreechart;
	requires org.apache.commons.csv;
	requires junit;
	requires org.junit.jupiter.api;
    exports view;
    exports app;
}