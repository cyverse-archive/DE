package org.iplantc.de.theme.base.client.desktop;

import com.google.gwt.i18n.client.Messages;

/**
 * Created by jstroot on 1/14/15.
 * @author jstroot
 */
public interface DesktopErrorMessages extends Messages {
    @Key("feedbackServiceFailure")
    String feedbackServiceFailure();

    @Key("fetchNotificationsError")
    String fetchNotificationsError();

    @Key("loadSessionFailed")
    String loadSessionFailed();

    @Key("permissionErrorMessage")
    String permissionErrorMessage();

    @Key("permissionErrorTitle")
    String permissionErrorTitle();

    @Key("saveSessionFailed")
    String saveSessionFailed();

    @Key("systemInitializationError")
    String systemInitializationError();

	String checkSysMessageError();
}
