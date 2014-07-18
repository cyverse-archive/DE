package org.iplantc.de.client.desktop.presenter;

import org.iplantc.de.client.desktop.views.DEView;
import org.iplantc.de.client.desktop.views.DEView.Presenter;
import org.iplantc.de.client.models.IsHideable;
import org.iplantc.de.client.models.UserSession;
import org.iplantc.de.client.models.WindowState;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

import com.sencha.gxt.widget.core.client.box.AutoProgressMessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;

import java.util.List;

class UserSessionProgressMessageBox extends AutoProgressMessageBox implements IsHideable {

    protected enum ProgressBoxType {
        RESTORE_SESSION, SAVE_SESSION;
    }

    public static UserSessionProgressMessageBox saveSession(DEView.Presenter presenter) {
        return saveSession(presenter, null);
    }

    public static UserSessionProgressMessageBox saveSession(DEView.Presenter presenter, String redirectUrl) {
        UserSessionProgressMessageBox saveSessionMb = new UserSessionProgressMessageBox(I18N.DISPLAY.savingSession(), I18N.DISPLAY.savingSessionWaitNotice(),
                ProgressBoxType.SAVE_SESSION,
                presenter,
                redirectUrl);
        saveSessionMb.setProgressText(I18N.DISPLAY.savingMask());
        saveSessionMb.setClosable(false);
        return saveSessionMb;
    }

    public static UserSessionProgressMessageBox restoreSession(DEView.Presenter presenter) {
        UserSessionProgressMessageBox restoreSessionMb = new UserSessionProgressMessageBox(I18N.DISPLAY.loadingSession(), I18N.DISPLAY.loadingSessionWaitNotice(),
                ProgressBoxType.RESTORE_SESSION,
                presenter);
        restoreSessionMb.setProgressText(I18N.DISPLAY.loadingMask());
        restoreSessionMb.setPredefinedButtons(PredefinedButton.CANCEL);
        restoreSessionMb.setClosable(false);
        return restoreSessionMb;
    }

    private final ProgressBoxType type;
    private final DEView.Presenter presenter;
    private final GetUserSessionCallback restoreSessionCallback;
    private final UserSessionFactory factory = GWT.create(UserSessionFactory.class);
    private final String redirectUrl;

    private UserSessionProgressMessageBox(String headingHtml, String messageHtml, ProgressBoxType type, DEView.Presenter presenter) {
        this(headingHtml, messageHtml, type, presenter, null);
    }

    private UserSessionProgressMessageBox(String headingHtml, String messageHtml, ProgressBoxType type, DEView.Presenter presenter,
            String redirectUrl) {
        super(headingHtml, messageHtml);
        this.getProgressBar().setDuration(1000);
        this.getProgressBar().setInterval(100);
        this.auto();
        this.type = type;
        this.presenter = presenter;
        this.redirectUrl = redirectUrl;
        restoreSessionCallback = new GetUserSessionCallback(this, presenter, factory);
    }

    @Override
    protected void onButtonPressed(TextButton button) {
        if (button == getButtonBar().getItemByItemId(PredefinedButton.CLOSE.name())) {
            restoreSessionCallback.cancelLoad();
        }
        super.onButtonPressed(button);
    }

    @Override
    public void show() {
        super.show();
        if (type.equals(ProgressBoxType.RESTORE_SESSION)) {
//            ServicesInjector.INSTANCE.getUserSessionServiceFacade().getUserSession(restoreSessionCallback);
        } else if (type.equals(ProgressBoxType.SAVE_SESSION)) {
            AutoBean<UserSession> userSession = factory.userSession();
            userSession.as().setWindowStates(presenter.getOrderedWindowStates());
//            ServicesInjector.INSTANCE.getUserSessionServiceFacade().saveUserSession(userSession.as(), new SaveUserSessionCallback(this, redirectUrl));
        }

    }

    interface UserSessionFactory extends AutoBeanFactory {
        AutoBean<UserSession> userSession();
    }

    private final class SaveUserSessionCallback implements AsyncCallback<String> {
        private final IsHideable msgBox;
        private final String redirectUrl;

        public SaveUserSessionCallback(IsHideable msgBox, String redirectUrl) {
            this.msgBox = msgBox;
            this.redirectUrl = redirectUrl;
        }

        private void handleRedirection() {
            if (redirectUrl != null) {
                Window.Location.assign(redirectUrl);
            }
        }

        @Override
        public void onSuccess(String result) {
            msgBox.hide();
            handleRedirection();
        }

        @Override
        public void onFailure(Throwable caught) {
            GWT.log(I18N.ERROR.saveSessionFailed(), caught);
            msgBox.hide();
            handleRedirection();
        }
    }

    private final class GetUserSessionCallback implements AsyncCallback<String> {
        private final IsHideable msgBox;
        private final Presenter presenter;
        private final UserSessionFactory factory;

        private boolean loadCancelled = false;

        public GetUserSessionCallback(IsHideable msgBox, DEView.Presenter presenter, UserSessionFactory factory) {
            this.msgBox = msgBox;
            this.presenter = presenter;
            this.factory = factory;
        }

        public void cancelLoad() {
            loadCancelled = true;
        }

        @Override
        public void onSuccess(String result) {
            if (!loadCancelled) {
                AutoBean<UserSession> userSession = AutoBeanCodex.decode(factory, UserSession.class, result);
                List<WindowState> windowStates = userSession.as().getWindowStates();
                if (windowStates != null) {
                    presenter.restoreWindows(windowStates);
                }
            }
            presenter.doPeriodicSessionSave();
            msgBox.hide();
        }

        @Override
        public void onFailure(Throwable caught) {
            GWT.log(I18N.ERROR.loadSessionFailed(), caught);
            presenter.doPeriodicSessionSave();
            msgBox.hide();
        }
    }

}
