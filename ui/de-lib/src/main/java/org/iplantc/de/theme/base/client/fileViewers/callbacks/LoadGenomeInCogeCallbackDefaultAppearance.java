package org.iplantc.de.theme.base.client.fileViewers.callbacks;

import org.iplantc.de.fileViewers.client.callbacks.LoadGenomeInCoGeCallback;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;
import org.iplantc.de.theme.base.client.fileViewers.FileViewerErrorStrings;
import org.iplantc.de.theme.base.client.fileViewers.FileViewerStrings;

import com.google.gwt.core.client.GWT;

/**
 * @author jstroot
 */
public class LoadGenomeInCogeCallbackDefaultAppearance implements LoadGenomeInCoGeCallback.LoadGenomeInCogeCallbackAppearance {

    final IplantDisplayStrings displayStrings;
    private final FileViewerStrings fileViewerStrings;
    private final FileViewerErrorStrings fileViewerErrorStrings;

    public LoadGenomeInCogeCallbackDefaultAppearance() {
        this(GWT.<IplantDisplayStrings> create(IplantDisplayStrings.class),
             GWT.<FileViewerStrings> create(FileViewerStrings.class),
             GWT.<FileViewerErrorStrings> create(FileViewerErrorStrings.class));
    }

    LoadGenomeInCogeCallbackDefaultAppearance(final IplantDisplayStrings displayStrings,
                                              final FileViewerStrings fileViewerStrings,
                                              final FileViewerErrorStrings fileViewerErrorStrings) {
        this.displayStrings = displayStrings;
        this.fileViewerStrings = fileViewerStrings;
        this.fileViewerErrorStrings = fileViewerErrorStrings;
    }

    @Override
    public String coge() {
        return displayStrings.coge();
    }

    @Override
    public String cogeError() {
        return fileViewerErrorStrings.cogeError();
    }

    @Override
    public String cogeResponse(String url) {
        return fileViewerStrings.cogeResponse(url);
    }
}
