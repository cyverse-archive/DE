package org.iplantc.de.fileViewers.client.views;

import org.iplantc.de.client.models.diskResources.File;

public abstract class StructuredTextViewer extends AbstractFileViewer implements FileViewer.EditingSupport {

    public StructuredTextViewer(File file, String infoType) {
        super(file, infoType);
    }

    public abstract void addRow();

    public abstract void deleteRow();

    public abstract void loadDataWithHeader(boolean header);

    public abstract void skipRows(int val);

}
