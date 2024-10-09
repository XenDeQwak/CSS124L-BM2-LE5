module com.groupfour {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires com.fasterxml.jackson.databind;
    requires javafx.base;
    requires javafx.media;
    requires java.xml;
    
    opens com.groupfour to javafx.fxml;
    exports com.groupfour;
}

// gab was here
