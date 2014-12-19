package org.iplantc.de.client;

import org.iplantc.de.client.gin.DEInjector;
import org.iplantc.de.desktop.client.DesktopView;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.TextAreaElement;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Defines the web application entry point for the system.
 */
public class DiscoveryEnvironment implements EntryPoint {
    /**
     * Entry point for the application.
     */
    @Override
    public void onModuleLoad() {
        setEntryPointTitle();
        final DesktopView.Presenter newDesktopPresenter = DEInjector.INSTANCE.getNewDesktopPresenter();
        newDesktopPresenter.go(RootPanel.get());

        preventBackspaceNavigation();
    }

    private HandlerRegistration preventBackspaceNavigation() {
        return Event.addNativePreviewHandler(new NativePreviewHandler() {
            @Override
            public void onPreviewNativeEvent(NativePreviewEvent event) {
                if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_BACKSPACE) {
                    final EventTarget eventTarget = event.getNativeEvent().getEventTarget();
                    if (eventTarget == null) {
                        return;
                    }
                    Element as = Element.as(eventTarget);

                    final String tagName = as.getTagName().toLowerCase();
                    final String type = as.getAttribute("type").toUpperCase();

                    final boolean isInput = InputElement.TAG.equals(tagName);
                    final boolean isTextArea = TextAreaElement.TAG.equals(tagName);
                    final boolean hasReadonly = as.hasAttribute("readonly");
                    final boolean hasDisabled = as.hasAttribute("disabled");

                    /* Prevent default when:
                     *   -- item is not a textarea or input
                     *   -- item is textarea or input AND it is set to readonly or disabled
                     */
                    boolean doPrevent = !(isTextArea
                                              || (isInput && ("PASSWORD".equals(type)
                                                                  || "TEXT".equals(type)
                                                                  || "FILE".equals(type)
                                                                  || "EMAIL".equals(type)
                                                                  || "SEARCH".equals(type)
                                                                  || "DATE".equals(type))))
                                            || hasReadonly || hasDisabled;

                    if (doPrevent) {
                        event.getNativeEvent().stopPropagation();
                        event.getNativeEvent().preventDefault();
                    }
                }
            }
        });
    }

    /**
     * Set the title element of the root page/entry point.
     * <p/>
     * Enables i18n of the root page.
     */
    private void setEntryPointTitle() {
        Window.setTitle(I18N.DISPLAY.rootApplicationTitle());
    }
}
