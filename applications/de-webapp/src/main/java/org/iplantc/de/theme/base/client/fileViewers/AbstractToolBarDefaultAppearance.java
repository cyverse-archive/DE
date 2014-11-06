package org.iplantc.de.theme.base.client.fileViewers;

import org.iplantc.de.fileViewers.client.views.AbstractToolBar;
import org.iplantc.de.resources.client.IplantResources;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;

import com.sencha.gxt.widget.core.client.Status;

public class AbstractToolBarDefaultAppearance implements AbstractToolBar.AbstractToolBarAppearance {


    protected final IplantDisplayStrings displayStrings;
    protected final Status.StatusAppearance editStatusAppearance;
    protected final IplantResources resources;
    protected final FileViewerStrings fileViewerStrings;

    public AbstractToolBarDefaultAppearance(){
        this(GWT.<IplantDisplayStrings> create(IplantDisplayStrings.class),
             GWT.<IplantResources> create(IplantResources.class),
             GWT.<FileViewerStrings> create(FileViewerStrings.class));
    }
    AbstractToolBarDefaultAppearance(final IplantDisplayStrings displayStrings,
                                     final IplantResources resources,
                                     final FileViewerStrings fileViewerStrings){
        this.displayStrings = displayStrings;
        this.resources = resources;
        this.fileViewerStrings = fileViewerStrings;
        this.editStatusAppearance = GWT.create(Status.BoxStatusAppearance.class);
    }

    @Override
    public String editingStatusText() {
        return fileViewerStrings.editingStatusText();
    }

    @Override
    public String notEditingStatusText() {
        return fileViewerStrings.notEditingStatusText();
    }

    @Override
    public String saveButtonText() {
        return displayStrings.save();
    }

    @Override
    public ImageResource saveButtonIcon() {
        return resources.save();
    }

    @Override
    public String refreshButtonText() {
        return displayStrings.refresh();
    }

    @Override
    public ImageResource refreshButtonIcon() {
        return resources.refresh();
    }

    @Override
    public String lineNumberCheckboxLabel() {
        return fileViewerStrings.lineNumberCheckboxLabel();
    }

    @Override
    public Status.StatusAppearance editStatusAppearance() {
        return editStatusAppearance;
    }

    @Override
    public String editStatusWidth() {
        return "100";
    }
}
