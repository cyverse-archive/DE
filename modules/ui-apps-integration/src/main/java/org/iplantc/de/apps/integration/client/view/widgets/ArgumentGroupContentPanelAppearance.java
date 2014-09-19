package org.iplantc.de.apps.integration.client.view.widgets;

import com.google.gwt.core.client.GWT;

import com.sencha.gxt.theme.base.client.panel.ContentPanelBaseAppearance;
import com.sencha.gxt.theme.base.client.widget.HeaderDefaultAppearance;
import com.sencha.gxt.theme.base.client.widget.HeaderDefaultAppearance.HeaderResources;
import com.sencha.gxt.theme.base.client.widget.HeaderDefaultAppearance.HeaderStyle;

public class ArgumentGroupContentPanelAppearance extends ContentPanelBaseAppearance {

    public interface ArgumentGroupContentPanelAppearanceResources extends ContentPanelResources {
        @Source({"com/sencha/gxt/theme/base/client/panel/ContentPanel.css", "ArgumentGroupContentPanel.css"})
        @Override
        ArgumentGroupContentPanelAppearanceStyle style();
    }

    public interface ArgumentGroupContentPanelAppearanceStyle extends ContentPanelStyle {

    }

    public final class SelectableHeaderAppearance extends HeaderDefaultAppearance {

        public SelectableHeaderAppearance() {
            super(resources1, GWT.<Template> create(Template.class));
        }

    }

    public interface SelectableHeaderResources extends HeaderResources {

        @Override
        @Source({"com/sencha/gxt/theme/base/client/widget/Header.css", "SelectableHeader.css"})
        SelectableHeaderStyle style();
    }

    public interface SelectableHeaderStyle extends HeaderStyle {

        String headerSelect();
    }

    private static final SelectableHeaderResources resources1 = GWT.create(SelectableHeaderResources.class);

    public ArgumentGroupContentPanelAppearance() {
        super(GWT.<ArgumentGroupContentPanelAppearanceResources> create(ArgumentGroupContentPanelAppearanceResources.class), GWT.<ContentPanelTemplate> create(ContentPanelTemplate.class));
    }

    public ArgumentGroupContentPanelAppearance(ArgumentGroupContentPanelAppearanceResources resources) {
        super(resources, GWT.<ContentPanelTemplate> create(ContentPanelTemplate.class));
    }

    @Override
    public HeaderDefaultAppearance getHeaderAppearance() {
        return new SelectableHeaderAppearance();
    }
}
