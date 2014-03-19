package org.iplantc.de.client.desktop.presenter;

import org.iplantc.de.client.desktop.presenter.UserSessionProgressMessageBox.UserSessionFactory;
import org.iplantc.de.client.desktop.views.DEView;
import org.iplantc.de.client.desktop.views.DEView.Presenter;
import org.iplantc.de.client.gin.ServicesInjector;
import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.models.UserSession;
import org.iplantc.de.client.models.WindowState;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.Splittable;

import java.util.List;

public class SaveSessionPeriodic implements Runnable {

    private final Presenter presenter;
    private final UserSessionFactory factory = GWT.create(UserSessionFactory.class);

    public SaveSessionPeriodic(DEView.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void run() {
        AutoBean<UserSession> userSession = factory.userSession();
        final List<WindowState> orderedWindowStates = presenter.getOrderedWindowStates();
        userSession.as().setWindowStates(orderedWindowStates);
        Splittable spl = AutoBeanCodex.encode(userSession);
        if (isStateChanged(orderedWindowStates, spl)) {
            GWT.log("saving periodic...");
            ServicesInjector.INSTANCE.getUserSessionServiceFacade().saveUserSession(userSession.as(), new AsyncCallback<String>() {

                @Override
                public void onSuccess(String result) {
                    // cache the update
                    UserInfo info = UserInfo.getInstance();
                    info.setSavedOrderedWindowStates(orderedWindowStates);
                }

                @Override
                public void onFailure(Throwable caught) {
                    GWT.log("Session periodic save failed");
                }
            });
        }
    }

    private boolean isStateChanged(List<WindowState> orderedWindowStates, Splittable splOws) {
        if (splOws == null || orderedWindowStates == null) {
            return false;
        }

        UserInfo info = UserInfo.getInstance();
        List<WindowState> savedStates = info.getSavedOrderedWindowStates();

        if (savedStates == null) {
            if (orderedWindowStates.size() == 0) {
                return false;
            } else {
                return true;
            }
        }

        AutoBean<UserSession> savedUserSession = factory.userSession();
        savedUserSession.as().setWindowStates(savedStates);
        Splittable savedSpl = AutoBeanCodex.encode(savedUserSession);
        return !savedSpl.getPayload().equals(splOws.getPayload());
    }
}
