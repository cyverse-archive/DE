package org.iplantc.admin.belphegor.theme.base.client.diskResource;

import org.iplantc.de.diskResource.client.views.widgets.FileFolderSelectorField;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;

import com.google.gwt.core.client.GWT;

/**
 * @author jstroot
 */
public class FileFolderSelectorFieldDefaultAppearance implements FileFolderSelectorField.FileFolderSelectorFieldAppearance {
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
    public String dataDragDropStatusText(int size) {
        return displayStrings.dataDragDropStatusText(size);
    }

    @Override
    public String emptyText() {
        return messages.fileFolderSelectorFieldEmptyText();
    }
}
