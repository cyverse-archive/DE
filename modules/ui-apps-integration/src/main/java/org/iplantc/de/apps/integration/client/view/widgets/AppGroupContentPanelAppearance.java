package org.iplantc.de.apps.integration.client.view.widgets;

import com.google.gwt.core.client.GWT;

import com.sencha.gxt.theme.base.client.panel.ContentPanelBaseAppearance;
import com.sencha.gxt.theme.base.client.widget.HeaderDefaultAppearance;
import com.sencha.gxt.theme.base.client.widget.HeaderDefaultAppearance.HeaderResources;
import com.sencha.gxt.theme.base.client.widget.HeaderDefaultAppearance.HeaderStyle;

public class AppGroupContentPanelAppearance extends ContentPanelBaseAppearance {

    public interface AppGroupContentPanelAppearanceResources extends ContentPanelResources {
        @Source({"com/sencha/gxt/theme/base/client/panel/ContentPanel.css", "AppGroupContentPanel.css"})
        @Override
        AppGroupContentPanelAppearanceStyle style();
    }

    public interface AppGroupContentPanelAppearanceStyle extends ContentPanelStyle {

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

    private static final SelectableHeaderResources resources1 = GWT.<SelectableHeaderResources> create(SelectableHeaderResources.class);

    public AppGroupContentPanelAppearance() {
        super(GWT.<AppGroupContentPanelAppearanceResources> create(AppGroupContentPanelAppearanceResources.class), GWT.<ContentPanelTemplate> create(ContentPanelTemplate.class));
    }

    public AppGroupContentPanelAppearance(AppGroupContentPanelAppearanceResources resources) {
        super(resources, GWT.<ContentPanelTemplate> create(ContentPanelTemplate.class));
    }

    @Override
    public HeaderDefaultAppearance getHeaderAppearance() {
        return new SelectableHeaderAppearance();
    }
}
