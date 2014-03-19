package org.iplantc.de.client;

import org.iplantc.de.client.desktop.presenter.DEPresenter;
import org.iplantc.de.client.desktop.views.DEView;
import org.iplantc.de.client.desktop.views.DEViewImpl;
import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;

/**
 * Defines the web application entry point for the system.
 * 
 */
public class DiscoveryEnvironment implements EntryPoint {
    /**
     * Entry point for the application.
     */
    @Override
    public void onModuleLoad() {
        setEntryPointTitle();
        DeResources resources = GWT.create(DeResources.class);
        resources.css().ensureInjected();
        DEView view = new DEViewImpl(resources, EventBus.getInstance());
        new DEPresenter(view, resources, EventBus.getInstance());
    }

    /**
     * Set the title element of the root page/entry point.
     * 
     * Enables i18n of the root page.
     */
    private void setEntryPointTitle() {
        Window.setTitle(I18N.DISPLAY.rootApplicationTitle());
    }
}
