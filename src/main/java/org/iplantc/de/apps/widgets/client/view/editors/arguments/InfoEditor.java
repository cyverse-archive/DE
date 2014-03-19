package org.iplantc.de.apps.widgets.client.view.editors.arguments;

import org.iplantc.de.apps.widgets.client.view.editors.arguments.converters.ArgumentEditorConverter;
import org.iplantc.de.apps.widgets.client.view.editors.style.AppTemplateWizardAppearance;
import org.iplantc.de.client.models.apps.integration.Argument;
import org.iplantc.de.client.util.AppTemplateUtils;

import com.google.common.base.Strings;
import com.google.gwt.dom.client.Style;

public class InfoEditor extends AbstractArgumentEditor {

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
            argumentLabel.setStyleName(appearance.getStyle().emptyGroupBgText());
            argumentLabel.getElement().getStyle().setMarginTop(100, Style.Unit.PX);
            argumentLabel.getElement().getStyle().setMarginBottom(100, Style.Unit.PX);
        }
    }

    @Override
    public ArgumentEditorConverter<?> valueEditor() {
        return null;
    }

}
