package org.iplantc.de.theme.base.client.analyses;

import com.google.gwt.i18n.client.Messages;
import com.google.gwt.safehtml.shared.SafeHtml;

/**
 * Created by jstroot on 2/19/15.
 * @author jstroot
 */
public interface AnalysesMessages extends Messages {
    String analysesExecDeleteWarning();

    String analysesMenuLbl();

    String analysesRetrievalFailure();

    SafeHtml analysisCommentUpdateFailed();

    SafeHtml analysisCommentUpdateSuccess();

    SafeHtml analysisRenameFailed();

    SafeHtml analysisRenameSuccess();

    String analysisStopSuccess(String name);

    String appName();

    String cancelAnalysis();

    String deleteAnalysisError();

    String endDate();

    String goToOutputFolder();

    String gridEmptyText();

    String importRequestSubmit(String name);

    String noParameters();

    String paramName();

    String paramType();

    String relaunchAnalysis();

    String renameAnalysis();

    String renameMenuItem();

    String savingFileMask();

    String searchFieldEmptyText();

    String selectionCount(int count);

    String showAll();

    String startDate();

    String stopAnalysisError(String name);

    String updateComments();

    String viewParamLbl();

    String viewParameters(String name);

    String analysisStepInfoError();

    String viewAnalysisStepInfo();

    String stepType();

    String jobId();

    String share();

    String shareCollab();

    String shareSupportMi();

    String shareSupportConfirm();

    String shareWithInput();

    String shareOutputOnly();
}
