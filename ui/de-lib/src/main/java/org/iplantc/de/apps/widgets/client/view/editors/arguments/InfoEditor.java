package org.iplantc.de.apps.widgets.client.view.editors.arguments;

import org.iplantc.de.apps.widgets.client.events.ArgumentSelectedEvent;
import org.iplantc.de.apps.widgets.client.view.editors.arguments.converters.ArgumentEditorConverter;
import org.iplantc.de.apps.widgets.client.view.editors.style.AppTemplateWizardAppearance;
import org.iplantc.de.client.models.apps.integration.Argument;
import org.iplantc.de.client.util.AppTemplateUtils;

import com.google.common.base.Strings;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;

public class InfoEditor extends AbstractArgumentEditor {

    private HandlerRegistration handlerRegistration;

    public InfoEditor(AppTemplateWizardAppearance appearance) {
        super(appearance);
    }

    @Override
    public void setValue(Argument value) {
        super.setValue(value);
        argumentLabel.removeStyleName(appearance.getStyle().emptyGroupBgText());
        argumentLabel.setLabelSeparator("");
        final String id = Strings.nullToEmpty(value.getId());
        if (id.equals(AppTemplateUtils.EMPTY_GROUP_ARG_ID)) {
            handlerRegistration.removeHandler();
            argumentLabel.setStyleName(appearance.getStyle().emptyGroupBgText());
            argumentLabel.getElement().getStyle().setMarginTop(100, Style.Unit.PX);
            argumentLabel.getElement().getStyle().setMarginBottom(100, Style.Unit.PX);
        }
    }

    @Override
    protected void init() {
        handlerRegistration = argumentLabel.addDomHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                InfoEditor.this.fireEvent(new ArgumentSelectedEvent(model));
            }
        }, ClickEvent.getType());
        initWidget(argumentLabel);
    }

    @Override
    public ArgumentEditorConverter<?> valueEditor() {
        return null;
    }

}
