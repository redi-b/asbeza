package org.asbeza.frontend.services;

import org.asbeza.frontend.NavigationHandler;
import org.asbeza.frontend.utils.SessionManager;

public class LogoutService {

    private static final NavigationHandler navigation = NavigationHandler.getInstance();
    private static final SessionManager session = SessionManager.getInstance();

    public static void logOut() {
        session.clearToken();
        navigation.navigateTo("landing-page.fxml", "Asbeza - Welcome", null);
    }
}
