package org.iplantc.de.theme.base.client.diskResource;

import org.iplantc.de.diskResource.client.views.widgets.FileFolderSelectorField;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;
import org.iplantc.de.theme.base.client.diskResource.widgets.AbstractDiskResourceSelectorDefaultAppearance;

import com.google.gwt.core.client.GWT;

/**
 * @author jstroot
 */
public class FileFolderSelectorFieldDefaultAppearance extends AbstractDiskResourceSelectorDefaultAppearance
                                                       implements FileFolderSelectorField.FileFolderSelectorFieldAppearance {
    private final IplantDisplayStrings displayStrings;
    private final DiskResourceMessages messages;

    public FileFolderSelectorFieldDefaultAppearance(){
        this(GWT.<IplantDisplayStrings> create(IplantDisplayStrings.class),
             GWT.<DiskResourceMessages> create(DiskResourceMessages.class));
    }

    FileFolderSelectorFieldDefaultAppearance(final IplantDisplayStrings displayStrings,
                                             final DiskResourceMessages messages){
        this.displayStrings = displayStrings;
        this.messages = messages;
    }

    @Override
    public String emptyText() {
        return messages.fileFolderSelectorFieldEmptyText();
    }
}
