package org.iplantc.de.resources.client.uiapps.integration;

import com.google.gwt.safehtml.shared.SafeHtml;

public interface AppIntegrationErrorMessages {

    String publishFailureDefaultMessage();

    String unableToSave();

    String appContainsErrorsUnableToSave();

    String appContainsErrorsPromptToContinue();

    SafeHtml cannotDeleteLastArgumentGroup();

}
