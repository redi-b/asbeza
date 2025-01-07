module org.ecommerce.asbeza.frontend {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires jjwt.api;
    requires static lombok;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires com.fasterxml.jackson.databind;
    requires java.desktop;


    opens org.asbeza.frontend to javafx.fxml;
    exports org.asbeza.frontend;
    exports org.asbeza.frontend.controllers;
    opens org.asbeza.frontend.controllers to javafx.fxml;
    exports org.asbeza.frontend.controllers.admin;
    opens org.asbeza.frontend.controllers.admin to javafx.fxml;
    exports org.asbeza.frontend.controllers.customer;
    opens org.asbeza.frontend.controllers.customer to javafx.fxml;
    exports org.asbeza.frontend.controllers.delivery;
    opens org.asbeza.frontend.controllers.delivery to javafx.fxml;
    opens org.asbeza.frontend.types to javafx.base;

    exports org.asbeza.frontend.responses to com.fasterxml.jackson.databind;
    exports org.asbeza.frontend.types to com.fasterxml.jackson.databind;
}