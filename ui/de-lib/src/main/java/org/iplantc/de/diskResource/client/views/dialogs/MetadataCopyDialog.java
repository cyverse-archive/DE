package org.iplantc.de.diskResource.client.views.dialogs;

import org.iplantc.de.client.models.HasPath;
import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.commons.client.views.dialogs.IPlantDialog;
import org.iplantc.de.diskResource.client.GridView;
import org.iplantc.de.diskResource.client.gin.factory.DiskResourceSelectorFieldFactory;
import org.iplantc.de.diskResource.client.views.widgets.MultiFileSelectorField;

import com.google.gwt.user.client.TakesValue;
import com.google.inject.Inject;

import java.util.List;

public class MetadataCopyDialog extends IPlantDialog implements TakesValue<List<HasPath>> {

    MultiFileSelectorField multiFileFolderSelector;
    final GridView.Presenter.Appearance appearance;
    DiskResource srcDr;

    @Inject
    public MetadataCopyDialog(final GridView.Presenter.Appearance appearance,
                              DiskResourceSelectorFieldFactory selectionFieldFactory) {
        this.appearance = appearance;
        multiFileFolderSelector = selectionFieldFactory.creaeteMultiFileSelector(true);
        add(multiFileFolderSelector);
        setHideOnButtonClick(false);
        setHeadingText(this.appearance.copyMetadata());
        setSize("400px", "350px");
        setModal(false);
    }

    @Override
    public void setValue(List<HasPath> value) {
        multiFileFolderSelector.setValue(value);
    }

    @Override
    public List<HasPath> getValue() {
        return multiFileFolderSelector.getValue();
    }

    @Override
    public void clear() {
        multiFileFolderSelector.clear();
    }

    public DiskResource getSource() {
        return srcDr;
    }

    public void setSource(DiskResource dr) {
        this.srcDr = dr;
    }

    public void setHeader(String srcPath) {
        setHeadingText(this.appearance.copyMetadata() + " from: " + srcPath);
    }

}
