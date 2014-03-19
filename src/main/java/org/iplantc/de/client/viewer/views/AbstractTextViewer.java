package org.iplantc.de.client.viewer.views;

import org.iplantc.de.client.models.diskResources.File;

public abstract class AbstractTextViewer extends AbstractFileViewer {

    public AbstractTextViewer(File file, String infoType) {
        super(file, infoType);
    }

    public abstract void loadDataWithHeader(boolean header);

    public abstract void skipRows(int val);

}
