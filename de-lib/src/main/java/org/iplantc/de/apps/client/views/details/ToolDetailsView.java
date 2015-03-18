package org.iplantc.de.apps.client.views.details;

import org.iplantc.de.apps.shared.AppsModule;
import org.iplantc.de.client.models.tool.Tool;

import com.google.common.base.Strings;
import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.LeafValueEditor;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.IsWidget;

import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.Header;

/**
 * FIXME Ensure search highlighting
 * @author jstroot
 */
public class ToolDetailsView extends ContentPanel implements IsWidget,
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
    interface ToolsDetailsViewUiBinder extends UiBinder<HTMLPanel, ToolDetailsView> { }

    private final ToolsDetailsViewUiBinder BINDER = GWT.create(ToolsDetailsViewUiBinder.class);

    @UiField InlineLabel attribution;
    @UiField InlineLabel name;
    @UiField InlineLabel description;
    @UiField InlineLabel location;
    @UiField InlineLabel version;

    @Path("name") final
    HeaderEditor headerEditor;
    private String baseID;


    public ToolDetailsView() {
        setWidget(BINDER.createAndBindUi(this));
        headerEditor = new HeaderEditor(getHeader());
    }

    @Override
    protected void onEnsureDebugId(String baseID) {
        super.onEnsureDebugId(baseID);
        this.baseID = baseID;
    }

    @Override
    protected void initTools() {
        super.initTools();
        if(!isCollapsible() || isHideCollapseTool()){
            return;
        }
        // Content panel header should have one tool. Assume it is the collapse btn
        if(DebugInfo.isDebugIdEnabled()
               && !Strings.isNullOrEmpty(baseID)
               && getHeader().getToolCount() == 1){
            getHeader().getTool(0).ensureDebugId(baseID + AppsModule.Ids.TOOL_COLLAPSE_BTN);
        }
    }

    @UiFactory
    ContentPanel createContentPanel() {
        final ContentPanel contentPanel = new ContentPanel();
        contentPanel.setAnimCollapse(false);
        contentPanel.setBorders(false);
        return contentPanel;
    }

}
