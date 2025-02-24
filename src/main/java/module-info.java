module FBGApp {
    requires java.base;
    requires java.sql;
    requires java.desktop;
    requires jakarta.mail;
    requires com.toedter.jcalendar;
    
    exports view;
    exports controller;
    exports model;
    exports utils;
    exports database;
    
    // Open packages for testing
    opens com.fbg.test;
}