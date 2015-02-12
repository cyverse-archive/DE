package org.iplantc.de.theme.base.client.diskResource.widgets;

import org.iplantc.de.diskResource.client.views.widgets.MultiFileSelectorField;
import org.iplantc.de.resources.client.IplantResources;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;
import org.iplantc.de.resources.client.messages.IplantErrorStrings;
import org.iplantc.de.theme.base.client.diskResource.DiskResourceMessages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;

/**
 * @author jstroot
 */
public class MultiFileSelectorFieldDefaultAppearance implements MultiFileSelectorField.MultiFileSelectorFieldAppearance {
    private final DiskResourceMessages diskResourceMessages;
    private final IplantDisplayStrings iplantDisplayStrings;
    private final IplantErrorStrings iplantErrorStrings;
    private final IplantResources iplantResources;

    public MultiFileSelectorFieldDefaultAppearance() {
        this(GWT.<DiskResourceMessages> create(DiskResourceMessages.class),
             GWT.<IplantDisplayStrings> create(IplantDisplayStrings.class),
             GWT.<IplantErrorStrings> create(IplantErrorStrings.class),
             GWT.<IplantResources> create(IplantResources.class));
    }

    MultiFileSelectorFieldDefaultAppearance(final DiskResourceMessages diskResourceMessages,
                                            final IplantDisplayStrings iplantDisplayStrings,
                                            final IplantErrorStrings iplantErrorStrings,
                                            final IplantResources iplantResources) {
        this.diskResourceMessages = diskResourceMessages;
        this.iplantDisplayStrings = iplantDisplayStrings;
        this.iplantErrorStrings = iplantErrorStrings;
        this.iplantResources = iplantResources;
    }

    @Override
    public String analysisFailureWarning(String s) {
        return iplantDisplayStrings.analysisFailureWarning(s);
    }

    @Override
    public SafeHtml dataDragDropStatusText(int size) {
        return diskResourceMessages.dataDragDropStatusText(size);
    }

    @Override
    public String diskResourceDoesNotExist(String drErrList) {
        return iplantErrorStrings.diskResourceDoesNotExist(drErrList);
    }

    @Override
    public String nameColumnLabel() {
        return iplantDisplayStrings.name();
    }

    @Override
    public String permissionSelectErrorMessage() {
        return diskResourceMessages.permissionSelectErrorMessage();
    }

    @Override
    public String requiredField() {
        return diskResourceMessages.requiredField();
    }

    @Override
    public String selectMultipleInputs() {
        return diskResourceMessages.selectMultipleInputs();
    }

    @Override
    public String add() {
        return iplantDisplayStrings.add();
    }

    @Override
    public ImageResource addIcon() {
        return iplantResources.add();
    }

    @Override
    public String delete() {
        return iplantDisplayStrings.delete();
    }

    @Override
    public ImageResource deleteIcon() {
        return iplantResources.delete();
    }
}
