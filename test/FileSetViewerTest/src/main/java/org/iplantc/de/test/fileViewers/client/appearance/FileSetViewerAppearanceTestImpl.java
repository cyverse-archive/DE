package org.iplantc.de.test.fileViewers.client.appearance;

import org.iplantc.de.fileViewers.client.views.FileSetViewer;
import org.iplantc.de.resources.client.IplantResources;

import com.google.gwt.resources.client.ImageResource;
import com.google.inject.Inject;

import com.sencha.gxt.widget.core.client.Status;

public class FileSetViewerAppearanceTestImpl implements FileSetViewer.FileSetEditorAppearance {

    @Inject
    IplantResources resources;
    @Inject
    Status.BoxStatusAppearance boxStatusAppearance;

    @Inject
    public FileSetViewerAppearanceTestImpl(){ }

    @Override
    public Status.StatusAppearance getStatusAppearance() {
        return boxStatusAppearance;
    }

    @Override
    public String saveBtnText() {
        return "Save";
    }

    @Override
    public ImageResource saveBtnIcon() {
        return resources.save();
    }
}
