package org.iplantc.de.apps.integration.client.view.propertyEditors.util;

import org.iplantc.de.apps.widgets.client.view.editors.style.AppTemplateWizardAppearance;

import com.google.gwt.editor.client.LeafValueEditor;
import com.google.gwt.user.client.ui.HasHTML;

public class PrefixedHasTextEditor implements LeafValueEditor<String> {

    private final AppTemplateWizardAppearance appearance;
    private final HasHTML peer;
    private String prefixedMmodel;

    public PrefixedHasTextEditor(HasHTML peer, AppTemplateWizardAppearance appearance) {
        this.peer = peer;
        this.appearance = appearance;
    }

    @Override
    public String getValue() {
        return prefixedMmodel;
    }

    @Override
    public void setValue(String value) {
        this.prefixedMmodel = value;
        peer.setHTML(appearance.getPropertyPanelLabels().detailsPanelHeader(value));
    }

}