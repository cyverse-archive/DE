package org.iplantc.de.client.oauth;

import com.google.gwt.i18n.client.LocalizableResource;
import com.google.gwt.i18n.client.Messages;

@LocalizableResource.Generate(format = "com.google.gwt.i18n.rebind.format.PropertiesFormat")
public interface OAuthErrorDescriptions extends Messages {
    String invalidRequest();
    String unauthorizedClient();
    String accessDenied();
    String unsupportedResponseType();
    String invalidScope();
    String serverError();
    String temporarilyUnavailable();
    String invalidOauthConfig();
    String missingAuthCode();
    String missingState();
    String serviceError();
}
