package org.asbeza.frontend.controllers.delivery;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ErrorController {
    @FXML
    public Label errorLabel;

    protected void showError(boolean show) {
        errorLabel.setVisible(show);
        errorLabel.setManaged(show);
    }

    protected void setErrorMessage(String errorMessage) {
        errorLabel.setText("Error: " + errorMessage);
    }
}
