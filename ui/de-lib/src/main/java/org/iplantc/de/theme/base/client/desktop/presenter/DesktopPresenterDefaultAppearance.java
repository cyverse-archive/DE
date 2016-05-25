package org.iplantc.de.theme.base.client.desktop.presenter;

import org.iplantc.de.desktop.client.DesktopView;
import org.iplantc.de.resources.client.messages.IplantNewUserTourStrings;
import org.iplantc.de.theme.base.client.desktop.DesktopErrorMessages;
import org.iplantc.de.theme.base.client.desktop.DesktopMessages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtml;

/**
 * @author jstroot
 */
public class DesktopPresenterDefaultAppearance implements DesktopView.Presenter.DesktopPresenterAppearance {
    private final DesktopMessages desktopMessages;
    private final DesktopErrorMessages desktopErrorMessages;
    private final IplantNewUserTourStrings tourStrings;

    public DesktopPresenterDefaultAppearance() {
        this(GWT.<DesktopMessages> create(DesktopMessages.class),
             GWT.<DesktopErrorMessages> create(DesktopErrorMessages.class),
             GWT.<IplantNewUserTourStrings> create(IplantNewUserTourStrings.class));
    }
    DesktopPresenterDefaultAppearance(DesktopMessages desktopMessages,
                                      DesktopErrorMessages desktopErrorMessages,
                                      IplantNewUserTourStrings tourStrings) {
        this.desktopMessages = desktopMessages;
        this.desktopErrorMessages = desktopErrorMessages;
        this.tourStrings = tourStrings;
    }

    @Override
    public String feedbackServiceFailure() {
        return desktopErrorMessages.feedbackServiceFailure();
    }

    @Override
    public String feedbackSubmitted() {
        return desktopMessages.feedbackSubmitted();
    }

    @Override
    public String fetchNotificationsError() {
        return desktopErrorMessages.fetchNotificationsError();
    }

    @Override
    public String introWelcome() {
        return tourStrings.introWelcome();
    }

    @Override
    public String loadSessionFailed() {
        return desktopErrorMessages.loadSessionFailed();
    }

    @Override
    public String loadingSession() {
        return desktopMessages.loadingSession();
    }

    @Override
    public String loadingSessionWaitNotice() {
        return desktopMessages.loadingSessionWaitNotice();
    }

    @Override
    public String markAllAsSeenSuccess() {
        return desktopMessages.markAllAsSeenSuccess();
    }

    @Override
    public String newNotificationsAlert() {
        return desktopMessages.newNotificationsAlert();
    }

    @Override
    public String permissionErrorMessage() {
        return desktopErrorMessages.permissionErrorMessage();
    }

    @Override
    public String permissionErrorTitle() {
        return desktopErrorMessages.permissionErrorTitle();
    }

    @Override
    public String saveSessionFailed() {
        return desktopErrorMessages.saveSessionFailed();
    }

    @Override
    public String saveSettings() {
        return desktopMessages.saveSettings();
    }

    @Override
    public String savingSession() {
        return desktopMessages.savingSession();
    }

    @Override
    public String savingSessionWaitNotice() {
        return desktopMessages.savingSessionWaitNotice();
    }

    @Override
    public SafeHtml sessionRestoreCancelled() {
        return desktopMessages.sessionRestoreCancelled();
    }

    @Override
    public String systemInitializationError() {
        return desktopErrorMessages.systemInitializationError();
    }

    @Override
    public String welcome() {
        return desktopMessages.welcome();
    }

    @Override
    public String requestHistoryError() {
        return desktopMessages.requestHistoryError();
    }
	@Override
	public String checkSysMessageError() {
		return desktopErrorMessages.checkSysMessageError();
	}
}
