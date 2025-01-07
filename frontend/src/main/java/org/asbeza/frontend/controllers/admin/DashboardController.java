package org.asbeza.frontend.controllers.admin;

import javafx.scene.control.Label;

public class DashboardController extends CommonController {

    public Label lblUserName;

    public void initialize() {
        lblUserName.setText(session.getUserName());
    }

}
