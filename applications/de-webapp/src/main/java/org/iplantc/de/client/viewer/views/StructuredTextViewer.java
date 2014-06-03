package org.iplantc.de.client.viewer.views;

import org.iplantc.de.client.models.diskResources.File;

public abstract class StructuredTextViewer extends AbstractFileViewer implements EditingSupport {

    public StructuredTextViewer(File file, String infoType) {
        super(file, infoType);
    }

    public abstract void loadDataWithHeader(boolean header);

    public abstract void skipRows(int val);

    public abstract void addRow();

    public abstract void deleteRow();

}
