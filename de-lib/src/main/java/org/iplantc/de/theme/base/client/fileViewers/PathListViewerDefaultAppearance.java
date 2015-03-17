package org.iplantc.de.theme.base.client.fileViewers;

import org.iplantc.de.fileViewers.client.views.PathListViewer;
import org.iplantc.de.resources.client.constants.IplantValidationConstants;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;

import com.google.gwt.core.client.GWT;

/**
 * @author jstroot
 */
public class PathListViewerDefaultAppearance extends AbstractStructuredTextViewerDefaultAppearance implements PathListViewer.PathListViewerAppearance {

    final IplantValidationConstants validationConstants;
    final IplantDisplayStrings displayStrings;

    public PathListViewerDefaultAppearance() {
        this(GWT.<IplantValidationConstants> create(IplantValidationConstants.class),
             GWT.<IplantDisplayStrings> create(IplantDisplayStrings.class));
    }

    PathListViewerDefaultAppearance(final IplantValidationConstants validationConstants,
                                    final IplantDisplayStrings displayStrings) {
        this.validationConstants = validationConstants;
        this.displayStrings = displayStrings;
    }

    @Override
    public String analysisFailureWarning(String warnedNameCharacters) {
        return displayStrings.analysisFailureWarning(validationConstants.warnedDiskResourceNameChars());
    }

    @Override
    public String columnHeaderText() {
        return fileViewerStrings.pathListColumnHeaderText();
    }

    @Override
    public String pathListViewName(String name) {
        return fileViewerStrings.pathListViewName(name);
    }

    @Override
    public String preventPathListDrop() {
        return fileViewerStrings.preventPathListDrop();
    }

    @Override
    public String warnedDiskResourceNameChars() {
        return validationConstants.warnedDiskResourceNameChars();
    }
}
