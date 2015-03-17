package org.iplantc.de.theme.base.client.admin.toolRequest;

import org.iplantc.de.admin.desktop.client.toolRequest.ToolRequestView;
import org.iplantc.de.resources.client.IplantResources;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;

/**
 * @author jstroot
 */
public class ToolRequestViewDefaultAppearance implements ToolRequestView.ToolRequestViewAppearance {
    private final ToolRequestDisplayStrings displayStrings;
    private final IplantResources iplantResources;
    private final IplantDisplayStrings iplantDisplayStrings;

    public ToolRequestViewDefaultAppearance() {
        this(GWT.<ToolRequestDisplayStrings> create(ToolRequestDisplayStrings.class),
             GWT.<IplantDisplayStrings> create(IplantDisplayStrings.class),
             GWT.<IplantResources> create(IplantResources.class));
    }

    ToolRequestViewDefaultAppearance(final ToolRequestDisplayStrings displayStrings,
                                     final IplantDisplayStrings iplantDisplayStrings,
                                     final IplantResources iplantResources) {
        this.displayStrings = displayStrings;
        this.iplantDisplayStrings = iplantDisplayStrings;
        this.iplantResources = iplantResources;
    }

    @Override
    public String dateSubmittedColumnLabel() {
        return iplantDisplayStrings.dateSubmitted();
    }

    @Override
    public int dateSubmittedColumnWidth() {
        return 90;
    }

    @Override
    public String dateUpdatedColumnLabel() {
        return displayStrings.dateUpdatedColumnLabel();
    }

    @Override
    public int dateUpdatedColumnWidth() {
        return 90;
    }

    @Override
    public String nameColumnLabel() {
        return iplantDisplayStrings.name();
    }

    @Override
    public int nameColumnWidth() {
        return 90;
    }

    @Override
    public String statusColumnLabel() {
        return iplantDisplayStrings.status();
    }

    @Override
    public int statusColumnWidth() {
        return 90;
    }

    @Override
    public String submitBtnText() {
        return iplantDisplayStrings.submit();
    }

    @Override
    public String updateBtnText() {
        return displayStrings.updateBtnText();
    }

    @Override
    public ImageResource updateIcon() {
        return iplantResources.add();
    }

    @Override
    public double eastPanelSize() {
        return 500d;
    }

    @Override
    public double northPanelSize() {
        return 30d;
    }

    @Override
    public String updateToolRequestDlgHeading() {
        return displayStrings.updateToolRequestDlgHeading();
    }

    @Override
    public String updateToolRequestDlgHeight() {
        return "400px";
    }

    @Override
    public String updateToolRequestDlgWidth() {
        return "350px";
    }

    @Override
    public String updatedByColumnLabel() {
        return displayStrings.updatedByColumnLabel();
    }

    @Override
    public int updatedByColumnWidth() {
        return 90;
    }

    @Override
    public String versionColumnLabel() {
        return displayStrings.versionColumnLabel();
    }

    @Override
    public int versionColumnWidth() {
        return 90;
    }

    @Override
    public String currentStatusLabel() {
        return displayStrings.currentStatusLabel();
    }

    @Override
    public String setStatusLabel() {
        return displayStrings.setStatusLabel();
    }

    @Override
    public String setArbitraryStatusLabel() {
        return displayStrings.setArbitraryStatusLabel();
    }

    @Override
    public String commentsLabel() {
        return iplantDisplayStrings.comments();
    }

    @Override
    public String detailsPanelHeading() {
        return iplantDisplayStrings.details();
    }

    @Override
    public String additionalDataFileLabel() {
        return displayStrings.additionalDataFileLabel();
    }

    @Override
    public String additionalInfoLabel() {
        return displayStrings.additionalInfoLabel();
    }

    @Override
    public String architectureLabel() {
        return displayStrings.architectureLabel();
    }

    @Override
    public String attributionLabel() {
        return displayStrings.attributionLabel();
    }

    @Override
    public String cmdLineDescriptionLabel() {
        return displayStrings.cmdLineDescriptionLabel();
    }

    @Override
    public String documentationUrlLabel() {
        return displayStrings.documentationUrlLabel();
    }

    @Override
    public String multiThreadedLabel() {
        return displayStrings.multiThreadedLabel();
    }

    @Override
    public String phoneLabel() {
        return displayStrings.phoneLabel();
    }

    @Override
    public String sourceUrlLabel() {
        return displayStrings.sourceUrlLabel();
    }

    @Override
    public String submittedByLabel() {
        return displayStrings.submittedByLabel();
    }

    @Override
    public String testDataPathLabel() {
        return displayStrings.testDataPathLabel();
    }

    @Override
    public String versionLabel() {
        return displayStrings.versionColumnLabel();
    }
}
