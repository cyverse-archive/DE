package org.iplantc.de.theme.base.client.analyses;

import org.iplantc.de.analysis.client.AnalysisParametersView;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;
import org.iplantc.de.resources.client.messages.IplantErrorStrings;

import com.google.gwt.core.client.GWT;

/**
 * @author jstroot
 */
public class AnalysisParametersViewDefaultAppearance implements AnalysisParametersView.Appearance {
    private final AnalysesMessages analysesMessages;
    private final IplantDisplayStrings iplantDisplayStrings;
    private final IplantErrorStrings iplantErrorStrings;

    public AnalysisParametersViewDefaultAppearance() {
        this(GWT.<AnalysesMessages> create(AnalysesMessages.class),
             GWT.<IplantDisplayStrings> create(IplantDisplayStrings.class),
             GWT.<IplantErrorStrings> create(IplantErrorStrings.class));
    }

    AnalysisParametersViewDefaultAppearance(final AnalysesMessages analysesMessages,
                                            final IplantDisplayStrings iplantDisplayStrings,
                                            final IplantErrorStrings iplantErrorStrings) {
        this.analysesMessages = analysesMessages;
        this.iplantDisplayStrings = iplantDisplayStrings;
        this.iplantErrorStrings = iplantErrorStrings;
    }

    @Override
    public String diskResourceDoesNotExist(String name) {
        return iplantErrorStrings.diskResourceDoesNotExist(name);
    }

    @Override
    public String fileUploadSuccess(String name) {
        return iplantDisplayStrings.fileUploadSuccess(name);
    }

    @Override
    public String importFailed(String path) {
        return iplantErrorStrings.importFailed(path);
    }

    @Override
    public String importRequestSubmit(String name) {
        return analysesMessages.importRequestSubmit(name);
    }

    @Override
    public String retrieveParametersLoadingMask() {
        return iplantDisplayStrings.loadingMask();
    }

    @Override
    public String viewParameters(String name) {
        return analysesMessages.viewParameters(name);
    }
}
