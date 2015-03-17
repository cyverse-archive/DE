package org.iplantc.de.theme.base.client.fileViewers;

import org.iplantc.de.fileViewers.client.views.ExternalVisualizationURLViewerImpl;
import org.iplantc.de.resources.client.IplantResources;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;

/**
 * @author jstroot
 */
public class ExternalVisualizationURLViewerDefaultAppearance implements ExternalVisualizationURLViewerImpl.ExternalVisualizationURLViewerAppearance {
    private final FileViewerStrings fileViewerStrings;
    private final IplantDisplayStrings displayStrings;
    private final IplantResources resources;

    public ExternalVisualizationURLViewerDefaultAppearance() {
        this(GWT.<FileViewerStrings> create(FileViewerStrings.class),
             GWT.<IplantDisplayStrings> create(IplantDisplayStrings.class),
             GWT.<IplantResources> create(IplantResources.class));
    }

    ExternalVisualizationURLViewerDefaultAppearance(final FileViewerStrings fileViewerStrings,
                                                    final IplantDisplayStrings displayStrings,
                                                    final IplantResources resources) {
        this.fileViewerStrings = fileViewerStrings;
        this.displayStrings = displayStrings;
        this.resources = resources;
    }

    @Override
    public ImageResource arrowUp() {
        return resources.arrowUp();
    }

    @Override
    public String containerHeight() {
        return "380px";
    }

    @Override
    public String label() {
        return fileViewerStrings.label();
    }

    @Override
    public int labelColumnWidth() {
        return 30;
    }

    @Override
    public String sendToCogeLoadingMask() {
        return displayStrings.loadingMask();
    }

    @Override
    public String sendToCogeMenuItem() {
        return displayStrings.sendToCogeMenuItem();
    }

    @Override
    public String sendToEnsemblMenuItem() {
        return displayStrings.sendToEnsemblMenuItem();
    }

    @Override
    public String sendToEnsemblLoadingMask() {
        return displayStrings.loadingMask();
    }

    @Override
    public String sendToTreeViewerMenuItem() {
        return displayStrings.sendToTreeViewerMenuItem();
    }

    @Override
    public String sentToTreeViewerLoadingMask() {
        return displayStrings.loadingMask();
    }

    @Override
    public String toolbarHeight() {
        return "30";
    }

    @Override
    public String urlColumnHeaderLabel() {
        return fileViewerStrings.externalVizUrlColumnHeaderLabel();
    }

    @Override
    public int urlColumnHeaderWidth() {
        return 280;
    }

    @Override
    public String viewName(String fileName) {
        return fileViewerStrings.visualizationView(fileName);
    }
}
