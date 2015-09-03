package org.iplantc.de.theme.base.client.analyses.presenter;

import org.iplantc.de.analysis.client.AnalysesView;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;
import org.iplantc.de.theme.base.client.analyses.AnalysesMessages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtml;

/**
 * @author jstroot
 */
public class AnalysesPresenterDefaultAppearance implements AnalysesView.Presenter.Appearance {
    private final AnalysesMessages analysesMessages;
    private final IplantDisplayStrings iplantDisplayStrings;

    public AnalysesPresenterDefaultAppearance() {
        this(GWT.<AnalysesMessages> create(AnalysesMessages.class),
             GWT.<IplantDisplayStrings> create(IplantDisplayStrings.class));
    }

    AnalysesPresenterDefaultAppearance(final AnalysesMessages analysesMessages,
                                       final IplantDisplayStrings iplantDisplayStrings) {
        this.analysesMessages = analysesMessages;
        this.iplantDisplayStrings = iplantDisplayStrings;
    }

    @Override
    public String analysesRetrievalFailure() {
        return analysesMessages.analysesRetrievalFailure();
    }

    @Override
    public SafeHtml analysisCommentUpdateFailed() {
        return analysesMessages.analysisCommentUpdateFailed();
    }

    @Override
    public SafeHtml analysisCommentUpdateSuccess() {
        return analysesMessages.analysisCommentUpdateSuccess();
    }

    @Override
    public SafeHtml analysisRenameFailed() {
        return analysesMessages.analysisRenameFailed();
    }

    @Override
    public SafeHtml analysisRenameSuccess() {
        return analysesMessages.analysisRenameSuccess();
    }

    @Override
    public String analysisStopSuccess(String name) {
        return analysesMessages.analysisStopSuccess(name);
    }

    @Override
    public String comments() {
        return iplantDisplayStrings.comments();
    }

    @Override
    public String deleteAnalysisError() {
        return analysesMessages.deleteAnalysisError();
    }

    @Override
    public String stopAnalysisError(String name) {
        return analysesMessages.stopAnalysisError(name);
    }

    @Override
    public String analysisStepInfoError() {
        return analysesMessages.analysisStepInfoError();
    }

}
