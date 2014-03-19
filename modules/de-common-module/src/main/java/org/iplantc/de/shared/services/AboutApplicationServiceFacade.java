package org.iplantc.de.shared.services;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

/**
 * Provides a singleton service facade for About Application information.
 * 
 * @author lenards
 */
public class AboutApplicationServiceFacade {
    private static AboutApplicationServiceFacade aboutApp = null;

    private AboutApplicationServiceAsync proxy;

    private AboutApplicationServiceFacade() {
        final String SESSION_SERVICE = "about"; //$NON-NLS-1$

        proxy = (AboutApplicationServiceAsync)GWT.create(AboutApplicationService.class);
        ((ServiceDefTarget)proxy).setServiceEntryPoint(GWT.getModuleBaseURL() + SESSION_SERVICE);
    }

    /**
     * Retrieve service facade singleton instance.
     * 
     * @return a singleton instance of the service facade.
     */
    public static AboutApplicationServiceFacade getInstance() {
        if (aboutApp == null) {
            aboutApp = new AboutApplicationServiceFacade();
        }

        return aboutApp;
    }

    /**
     * Fetch the application's "about" information.
     * 
     * @param callback the callback that is executed and retrieves "about" information
     */
    public void getAboutInfo(AsyncCallback<String> callback) {
        proxy.getAboutInfo(callback);
    }
}
