package org.iplantc.de.apps.client.views.details;

import org.iplantc.de.client.models.tool.Tool;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.InlineLabel;

import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.container.AccordionLayoutContainer.AccordionLayoutAppearance;

/**
 * FIXME Ensure search highlighting
 * @author jstroot
 */
public class ToolDetailsView extends Composite implements Editor<Tool> {

    @UiTemplate("ToolDetailsView.ui.xml")
    interface ToolsDetailsViewUiBinder extends UiBinder<ContentPanel, ToolDetailsView> { }

    private final ToolsDetailsViewUiBinder BINDER = GWT.create(ToolsDetailsViewUiBinder.class);

    @UiField InlineLabel attribution;
    @UiField InlineLabel name;
    @UiField InlineLabel description;
    @UiField InlineLabel location;
    @UiField InlineLabel version;
    /**
     * FIXME Simple editor to bind to cp header
     */
    @UiField ContentPanel cp;


    public ToolDetailsView() {
        initWidget(BINDER.createAndBindUi(this));
    }

    @UiFactory
    ContentPanel createContentPanel() {
        final ContentPanel contentPanel = new ContentPanel(GWT.<AccordionLayoutAppearance>create(AccordionLayoutAppearance.class));
        contentPanel.setAnimCollapse(false);
        return contentPanel;
    }
}
