package org.iplantc.de.theme.base.client.fileViewers.callbacks;

import org.iplantc.de.fileViewers.client.callbacks.EnsemblUtil;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;
import org.iplantc.de.theme.base.client.fileViewers.FileViewerErrorStrings;

import com.google.gwt.core.client.GWT;

/**
 * Created by jstroot on 1/15/15.
 * @author jstroot
 */
public class EnsemblUtilDefaultAppearance implements EnsemblUtil.EnsemblUtilAppearance {
    final IplantDisplayStrings displayStrings;
    final FileViewerErrorStrings fileViewerErrorStrings;

    public EnsemblUtilDefaultAppearance() {
        this(GWT.<IplantDisplayStrings> create(IplantDisplayStrings.class),
             GWT.<FileViewerErrorStrings> create(FileViewerErrorStrings.class));
    }

    EnsemblUtilDefaultAppearance(final IplantDisplayStrings displayStrings,
                                 final FileViewerErrorStrings fileViewerErrorStrings) {
        this.displayStrings = displayStrings;
        this.fileViewerErrorStrings = fileViewerErrorStrings;
    }

    @Override
    public String indexFileMissing() {
        return displayStrings.indexFileMissing();
    }

    @Override
    public String indexFileMissingError() {
        return fileViewerErrorStrings.indexFileMissingError();
    }
}
