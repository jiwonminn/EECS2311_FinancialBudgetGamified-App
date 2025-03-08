module FBGApp {
    requires java.mail;
    requires java.sql;
    requires jdatepicker;
    requires java.desktop;
    requires mysql.connector.j;
    exports view;
    exports app;
    requires org.jfree.jfreechart;
    requires org.apache.commons.csv;
}