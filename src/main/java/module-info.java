module com.example.buet_java_fx {
    requires javafx.controls;
    requires javafx.fxml;

    // Open the controllers package to allow access by FXML
    opens controllers to javafx.fxml;
    opens search to javafx.base;

    // Export the controllers package for use in other modules, if needed
    exports controllers;

    // Open the root package for any FXML files referencing classes in this package
    opens com.example.buet_java_fx to javafx.fxml;

    // Export the root package
    exports com.example.buet_java_fx;
}
