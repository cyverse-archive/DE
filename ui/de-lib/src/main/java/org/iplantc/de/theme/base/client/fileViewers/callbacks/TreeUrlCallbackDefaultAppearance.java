package org.iplantc.de.theme.base.client.fileViewers.callbacks;

import org.iplantc.de.fileViewers.client.callbacks.TreeUrlCallback;
import org.iplantc.de.theme.base.client.fileViewers.FileViewerErrorStrings;

import com.google.gwt.core.client.GWT;

/**
 * @author jstroot
 */
public class TreeUrlCallbackDefaultAppearance implements TreeUrlCallback.TreeUrlCallbackAppearance {
    private final FileViewerErrorStrings errorStrings;

    public TreeUrlCallbackDefaultAppearance() {
        this(GWT.<FileViewerErrorStrings> create(FileViewerErrorStrings.class));
    }

    TreeUrlCallbackDefaultAppearance(final FileViewerErrorStrings errorStrings) {
        this.errorStrings = errorStrings;
    }

    @Override
    public String unableToRetrieveTreeUrls(String fileName) {
        return errorStrings.unableToRetrieveTreeUrls(fileName);
    }
}
