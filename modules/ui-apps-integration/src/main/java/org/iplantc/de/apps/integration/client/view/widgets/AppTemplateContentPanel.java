package org.iplantc.de.apps.integration.client.view.widgets;

import org.iplantc.de.apps.widgets.client.events.AppTemplateSelectedEvent;
import org.iplantc.de.apps.widgets.client.events.AppTemplateSelectedEvent.AppTemplateSelectedEventHandler;
import org.iplantc.de.apps.widgets.client.events.AppTemplateSelectedEvent.HasAppTemplateSelectedEventHandlers;
import org.iplantc.de.apps.widgets.client.view.editors.style.AppTemplateWizardAppearance;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;

import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.widget.core.client.ContentPanel;

public final class AppTemplateContentPanel extends ContentPanel implements HasAppTemplateSelectedEventHandlers {

    private final AppTemplateWizardAppearance wizAppearance;

    public AppTemplateContentPanel() {
        this(new AppGroupContentPanelAppearance(), AppTemplateWizardAppearance.INSTANCE);
    }

    private AppTemplateContentPanel(AppGroupContentPanelAppearance appearance, AppTemplateWizardAppearance wizAppearance) {
        super(appearance);
        setCollapsible(true);
        setAnimCollapse(false);
        setTitleCollapse(true);
        getHeader().addStyleName(wizAppearance.getStyle().appHeaderSelect());
        this.wizAppearance = wizAppearance;
        sinkEvents(Event.ONCLICK | Event.MOUSEEVENTS);
    }

    @Override
    public HandlerRegistration addAppTemplateSelectedEventHandler(AppTemplateSelectedEventHandler handler) {
        return addHandler(handler, AppTemplateSelectedEvent.TYPE);
    }

    @Override
    protected void onClick(Event ce) {
        XElement element = XElement.as(header.getElement());
        if (element.isOrHasChild(ce.getEventTarget().<Element> cast())) {
            fireEvent(new AppTemplateSelectedEvent());
            getHeader().addStyleName(wizAppearance.getStyle().appHeaderSelect());
        }
        super.onClick(ce);
    }
}