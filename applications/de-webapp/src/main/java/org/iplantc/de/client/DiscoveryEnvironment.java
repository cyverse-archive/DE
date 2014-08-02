package org.iplantc.de.client;

import org.iplantc.de.client.gin.DEInjector;
import org.iplantc.de.client.newDesktop.NewDesktopView;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

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
        DEInjector injector = DEInjector.INSTANCE;
        RootPanel.get().clear();
        final NewDesktopView.Presenter newDesktopPresenter = injector.getNewDesktopPresenter();
        newDesktopPresenter.go(RootPanel.get());

        Event.addNativePreviewHandler(new NativePreviewHandler() {
            @Override
            public void onPreviewNativeEvent(NativePreviewEvent event) {
                if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_BACKSPACE) {
                    if (event.getNativeEvent().getEventTarget() != null) {
                        Element as = Element.as(event.getNativeEvent().getEventTarget());
                        if (as == RootPanel.getBodyElement()) {
                            event.getNativeEvent().stopPropagation();
                            event.getNativeEvent().preventDefault();
                        }
                    }
                }
            }
        });
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
