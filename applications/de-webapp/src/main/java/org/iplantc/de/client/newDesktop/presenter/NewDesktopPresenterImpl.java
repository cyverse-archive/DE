package org.iplantc.de.client.newDesktop.presenter;

import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.events.WindowShowRequestEvent;
import org.iplantc.de.client.newDesktop.NewDesktopView;
import org.iplantc.de.client.sysmsgs.view.NewMessageView;
import org.iplantc.de.commons.client.CommonUiConstants;
import org.iplantc.de.commons.client.util.WindowUtil;
import org.iplantc.de.commons.client.views.window.configs.ConfigFactory;
import org.iplantc.de.shared.DeModule;
import org.iplantc.de.shared.services.PropertyServiceFacade;

import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.user.client.ui.Panel;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class NewDesktopPresenterImpl implements NewDesktopView.Presenter {

    private final NewDesktopView view;
    private final EventBus eventBus;
    private final NewMessageView.Presenter systemMsgPresenter;
    @Inject
    CommonUiConstants commonUiConstants;

    @Inject
    Provider<PropertyServiceFacade> propertyServiceFacade;

    @Inject
    public NewDesktopPresenterImpl(final NewDesktopView view,
                                   final EventBus eventBus,
                                   final NewMessageView.Presenter systemMsgPresenter){
        this.view = view;
        this.eventBus = eventBus;
        this.systemMsgPresenter = systemMsgPresenter;
        this.view.setPresenter(this);
        if(DebugInfo.isDebugIdEnabled()) {
            this.view.ensureDebugId(DeModule.Ids.DESKTOP);
        }
    }

    @Override
    public void go(Panel panel) {
        // Need to make service calls to get all initial stuff
        panel.add(view);
    }

    @Override
    public void onAnalysesWinBtnSelect() {
       eventBus.fireEvent(new WindowShowRequestEvent(ConfigFactory.analysisWindowConfig()));
    }

    @Override
    public void onAppsWinBtnSelect() {
        eventBus.fireEvent(new WindowShowRequestEvent(ConfigFactory.appsWindowConfig()));
    }

    @Override
    public void onDataWinBtnSelect() {
        eventBus.fireEvent(new WindowShowRequestEvent(ConfigFactory.diskResourceWindowConfig(true)));
    }

    @Override
    public void onForumsBtnSelect() {
        WindowUtil.open(commonUiConstants.forumsUrl());
    }

    @Override
    public void onNotificationsBtnSelect() {
        // TODO Refactor Notifications module do a presenter.Go
    }

    @Override
    public void onUserPrefsBtnSelect() {
        // TODO Refactor Preferences.
    }
}
