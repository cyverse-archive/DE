package org.iplantc.de.client.newDesktop.presenter;

import static org.iplantc.de.commons.client.collaborators.presenter.ManageCollaboratorsPresenter.MODE.MANAGE;
import org.iplantc.de.client.DEClientConstants;
import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.models.WindowState;
import org.iplantc.de.client.newDesktop.NewDesktopView;
import org.iplantc.de.client.preferences.views.PreferencesDialog;
import org.iplantc.de.client.sysmsgs.view.NewMessageView;
import org.iplantc.de.client.views.windows.IPlantWindowInterface;
import org.iplantc.de.commons.client.CommonUiConstants;
import org.iplantc.de.commons.client.collaborators.views.ManageCollaboratorsDailog;
import org.iplantc.de.commons.client.util.WindowUtil;
import org.iplantc.de.commons.client.views.window.configs.ConfigFactory;
import org.iplantc.de.commons.client.views.window.configs.WindowConfig;
import org.iplantc.de.shared.DeModule;
import org.iplantc.de.shared.services.PropertyServiceFacade;

import com.google.common.collect.Lists;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Provider;

import com.sencha.gxt.widget.core.client.WindowManager;

import java.util.List;

public class NewDesktopPresenterImpl implements NewDesktopView.Presenter {

    @Inject
    CommonUiConstants commonUiConstants;
    @Inject
    Provider<PropertyServiceFacade> propertyServiceFacade;
    private final CommonUiConstants constants;
    private final DEClientConstants deClientConstants;
    private final EventBus eventBus;
    private final NewMessageView.Presenter systemMsgPresenter;
    private final NewDesktopView view;
    private final DesktopWindowManager windowManager;

    @Inject
    public NewDesktopPresenterImpl(final NewDesktopView view,
                                   final DesktopPresenterEventHandler globalEventHandler,
                                   final DesktopPresenterWindowEventHandler windowEventHandler,
                                   final EventBus eventBus,
                                   final NewMessageView.Presenter systemMsgPresenter,
                                   final CommonUiConstants constants,
                                   final DEClientConstants deClientConstants,
                                   final WindowManager gxtWindowManager) {
        this.view = view;
        this.eventBus = eventBus;
        this.systemMsgPresenter = systemMsgPresenter;
        this.constants = constants;
        this.deClientConstants = deClientConstants;
        this.view.setPresenter(this);
        globalEventHandler.setPresenter(this);
        windowEventHandler.setPresenter(this);
        windowManager = new DesktopWindowManager(gxtWindowManager);
        if (DebugInfo.isDebugIdEnabled()) {
            this.view.ensureDebugId(DeModule.Ids.DESKTOP);
        }
    }

    @Override
    public void go(Panel panel) {
        // Need to make service calls to get all initial stuff
        panel.add(view);
    }

    @Override
    public void onAboutClick() {
        show(ConfigFactory.aboutWindowConfig());
    }

    @Override
    public void onAnalysesWinBtnSelect() {
        show(ConfigFactory.analysisWindowConfig());
    }

    @Override
    public void onAppsWinBtnSelect() {
        show(ConfigFactory.appsWindowConfig());
    }

    @Override
    public void onCollaboratorsClick() {
        ManageCollaboratorsDailog d = new ManageCollaboratorsDailog(MANAGE);
        d.show();
    }

    @Override
    public void onContactSupportClick() {
        WindowUtil.open(constants.supportUrl());
    }

    @Override
    public void onDataWinBtnSelect() {
        show(ConfigFactory.diskResourceWindowConfig(true));
    }

    @Override
    public void onDocumentationClick() {
        WindowUtil.open(deClientConstants.deHelpFile());
    }

    @Override
    public void onForumsBtnSelect() {
        WindowUtil.open(commonUiConstants.forumsUrl());
    }

    @Override
    public void onIntroClick() {
        doIntro();
    }

    @Override
    public void onLogoutClick() {
        doLogout();
    }

    @Override
    public void onPreferencesClick() {
        PreferencesDialog d = new PreferencesDialog();
        d.show();
    }

    @Override
    public void onSystemMessagesClick() {
        show(ConfigFactory.systemMessagesWindowConfig(null));
    }

    public void show(final WindowConfig config) {
        windowManager.show(config, false);
    }

    public void show(final WindowConfig config, final boolean updateExistingWindow) {
        windowManager.show(config, updateExistingWindow);
    }

    List<WindowState> getWindowStates() {
        List<WindowState> windowStates = Lists.newArrayList();
        for (Widget w : WindowManager.get().getStack()) {
            windowStates.add(((IPlantWindowInterface) w).getWindowState());
        }
        return windowStates;
    }

    private void doIntro() {
        // TODO Implement method
    }

    private void doLogout() {
        // TODO Implement method
    }

}
