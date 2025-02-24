module FBGApp {
    requires java.base;
    requires java.sql;
    requires java.desktop;
    requires jcalendar;
    requires mail;  // Automatic module name for javax.mail
    requires activation;  // Automatic module name for javax.activation
    
    exports view;
    exports controller;
    exports model;
    exports utils;
    exports database;
    
    // Open packages for testing
    opens model;
    opens controller;
    opens utils;
}