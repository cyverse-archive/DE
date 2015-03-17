package org.iplantc.de.theme.base.client.admin.toolRequest.presenter;

import org.iplantc.de.admin.desktop.client.toolRequest.ToolRequestView;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;

import com.google.gwt.core.client.GWT;

/**
 * @author jstroot
 */
public class ToolRequestPresenterDefaultAppearance implements ToolRequestView.Presenter.ToolRequestPresenterAppearance {
    private final IplantDisplayStrings iplantDisplayStrings;
    private final ToolRequestPresenterDisplayStrings displayStrings;

    public ToolRequestPresenterDefaultAppearance() {
        this(GWT.<IplantDisplayStrings> create(IplantDisplayStrings.class),
             GWT.<ToolRequestPresenterDisplayStrings> create(ToolRequestPresenterDisplayStrings.class));
    }
    public ToolRequestPresenterDefaultAppearance(final IplantDisplayStrings iplantDisplayStrings,
                                                 final ToolRequestPresenterDisplayStrings displayStrings) {
        this.iplantDisplayStrings = iplantDisplayStrings;
        this.displayStrings = displayStrings;
    }

    @Override
    public String getToolRequestDetailsLoadingMask() {
        return iplantDisplayStrings.loadingMask();
    }

    @Override
    public String getToolRequestsLoadingMask() {
        return iplantDisplayStrings.loadingMask();
    }

    @Override
    public String toolRequestUpdateSuccessMessage() {
        return displayStrings.toolRequestUpdateSuccessMessage();
    }
}
