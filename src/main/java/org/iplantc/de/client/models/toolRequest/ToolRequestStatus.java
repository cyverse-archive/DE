package org.iplantc.de.client.models.toolRequest;

import org.iplantc.de.resources.client.messages.I18N;

import com.google.common.base.Strings;

/**
 * Tool Request Status values with associated help text.
 * 
 * @author psarando
 * 
 */
public enum ToolRequestStatus {
    Submitted("Submitted", I18N.HELP.toolRequestStatusSubmittedHelp()), Pending("Pending", I18N.HELP.toolRequestStatusPendingHelp()), Evaluation("Evaluation", I18N.HELP
            .toolRequestStatusEvaluationHelp()), Installation("Installation", I18N.HELP.toolRequestStatusInstallationHelp()), Validation("Validation", I18N.HELP.toolRequestStatusValidationHelp()), Completion(
            "Completion", I18N.HELP.toolRequestStatusCompleteHelp()), Failed("Failed", I18N.HELP.toolRequestStatusFailedHelp());

    private String helpText;
    private String displayText;

    ToolRequestStatus(String displayText, String helpText) {
        this.displayText = displayText;
        this.helpText = helpText;
    }

    public static ToolRequestStatus fromTypeString(String typeString) {
        if (typeString == null || typeString.isEmpty()) {
            return null;
        }
        String temp = typeString.replaceAll("\\s", "");
        return valueOf(temp);
    }

    public String getHelpText() {
        return (Strings.isNullOrEmpty(helpText) ? "" : helpText);
    }

    @Override
    public String toString() {
        return displayText;
    }
}
