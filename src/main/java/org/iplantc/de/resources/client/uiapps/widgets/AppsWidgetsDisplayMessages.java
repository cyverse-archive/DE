package org.iplantc.de.resources.client.uiapps.widgets;

import com.google.gwt.i18n.client.Messages;

public interface AppsWidgetsDisplayMessages extends Messages {
    /**
     * @return the text to be displayed inside an empty selection item.
     */
    String emptyListSelectionText();

    String editPublicAppWarning();

    String defaultAnalysisName();

    String nonEmptyFolderWarning();

    String launchAnalysisDetailsHeadingPrefix();

    String nonDefaultFolderWarning();

    String retainInputs();

    String selectOutputFolder();

    String analysisName();

    String addGroupToolTip();

    String addArgumentToolTip();

    String checkCascadeLabel();

    String checkCascadeLabelToolTip();

    String cascadeOptionsComboToolTip();

    String forceSingleSelectLabel();

    String forceSingleSelectToolTip();

    String emptyArgumentGroupBgText();

    String launchAnalysisSuccess(String analysisName);
}
