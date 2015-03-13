package org.iplantc.de.apps.client.views.details;

import org.iplantc.de.client.models.tool.Tool;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.LeafValueEditor;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.IsWidget;

import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.Header;

/**
 * FIXME Ensure search highlighting
 * @author jstroot
 */
public class ToolDetailsView implements IsWidget,
                                        Editor<Tool> {

    static class HeaderEditor implements LeafValueEditor<String> {

        private final Header header;

        public HeaderEditor(final Header header) {
            this.header = header;
        }

        @Override
        public void setValue(String value) {
            header.setText(value);
        }

        @Override
        public String getValue() {
            return header.getText();
        }
    }

    @UiTemplate("ToolDetailsView.ui.xml")
    interface ToolsDetailsViewUiBinder extends UiBinder<ContentPanel, ToolDetailsView> { }

    private final ToolsDetailsViewUiBinder BINDER = GWT.create(ToolsDetailsViewUiBinder.class);

    @UiField InlineLabel attribution;
    @UiField InlineLabel name;
    @UiField InlineLabel description;
    @UiField InlineLabel location;
    @UiField InlineLabel version;
    @UiField ContentPanel cp;

    @Path("name") final
    HeaderEditor headerEditor;


    public ToolDetailsView() {
        BINDER.createAndBindUi(this);
        headerEditor = new HeaderEditor(cp.getHeader());
    }

    @UiFactory
    ContentPanel createContentPanel() {
        final ContentPanel contentPanel = new ContentPanel();
        contentPanel.setAnimCollapse(false);
        contentPanel.setBorders(false);
        return contentPanel;
    }

    @Override
    public ContentPanel asWidget() {
        return cp;
    }
}
