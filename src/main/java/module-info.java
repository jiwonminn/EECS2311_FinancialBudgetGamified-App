module FBGApp {
	requires java.base;
    requires java.mail;
    requires java.sql;
    requires jdatepicker;
    requires java.desktop;
    requires mysql.connector.j;
	requires org.jfree.jfreechart;
	requires org.apache.commons.csv;
    exports view;
    exports app;
    //requires org.apache.commons.csv;
    

}