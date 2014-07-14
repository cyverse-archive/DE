package org.iplantc.de.client.newDesktop.views;

import org.iplantc.de.client.desktop.widget.TaskBar;
import org.iplantc.de.client.newDesktop.NewDesktopView;
import org.iplantc.de.client.newDesktop.views.widgets.UserSettingsMenu;
import org.iplantc.de.resources.client.messages.IplantNewUserTourStrings;
import org.iplantc.de.shared.DeModule;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import static com.sencha.gxt.core.client.Style.Anchor.BOTTOM_LEFT;
import static com.sencha.gxt.core.client.Style.Anchor.TOP_LEFT;
import static com.sencha.gxt.core.client.Style.AnchorAlignment;
import com.sencha.gxt.widget.core.client.button.IconButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;

/**
 * Created by jstroot on 7/6/14.
 */
public class NewDesktopViewImpl implements NewDesktopView {

    interface NewViewUiBinder extends UiBinder<Widget, NewDesktopViewImpl> { }

    @UiField
    IconButton analysisWinBtn;
    @UiField
    IconButton appsWinBtn;
    @UiField
    IconButton dataWinBtn;
    @UiField
    IconButton feedbackBtn;
    @UiField
    IconButton forumsBtn;
    @UiField
    IconButton notificationsBtn;
    @UiField
    TaskBar taskBar;
    @UiField
    IconButton userSettingsBtn;
    private static NewViewUiBinder ourUiBinder = GWT.create(NewViewUiBinder.class);
    private final Widget widget;
    private final UserSettingsMenu userSettingsMenu;
    private final AnchorAlignment anchorAlignment = new AnchorAlignment(TOP_LEFT, BOTTOM_LEFT, true);
    private NewDesktopView.Presenter presenter;

    @Inject
    public NewDesktopViewImpl(final IplantNewUserTourStrings tourStrings) {
        widget = ourUiBinder.createAndBindUi(this);
        userSettingsMenu = new UserSettingsMenu();
        userSettingsMenu.setPresenter(presenter);
        initIntroAttributes(tourStrings);
    }

    private void initIntroAttributes(IplantNewUserTourStrings tourStrings) {
        // Feedback Btn
        feedbackBtn.getElement().setAttribute("data-intro", tourStrings.introFeedback());
        feedbackBtn.getElement().setAttribute("data-position", "top");
        feedbackBtn.getElement().setAttribute("data-step", "6");

        // Window Btns
        dataWinBtn.getElement().setAttribute("data-intro", tourStrings.introDataWindow());
        dataWinBtn.getElement().setAttribute("data-position", "dataWinBtn");
        dataWinBtn.getElement().setAttribute("data-step", "1");
        appsWinBtn.getElement().setAttribute("data-intro", tourStrings.introAppsWindow());
        appsWinBtn.getElement().setAttribute("data-position", "bottom");
        appsWinBtn.getElement().setAttribute("data-step", "2");
        analysisWinBtn.getElement().setAttribute("data-intro", tourStrings.introAnalysesWindow());
        analysisWinBtn.getElement().setAttribute("data-position", "bottom");
        analysisWinBtn.getElement().setAttribute("data-step", "3");

        // User Menu Btns
        notificationsBtn.getElement().setAttribute("data-intro", tourStrings.introNotifications());
        notificationsBtn.getElement().setAttribute("data-position", "left");
        notificationsBtn.getElement().setAttribute("data-step", "4");

        userSettingsBtn.getElement().setAttribute("data-intro", tourStrings.introSettings());
        userSettingsBtn.getElement().setAttribute("data-position", "left");
        userSettingsBtn.getElement().setAttribute("data-step", "5");

        forumsBtn.getElement().setAttribute("data-intro", tourStrings.introAsk());
        forumsBtn.getElement().setAttribute("data-position", "left");
        forumsBtn.getElement().setAttribute("data-step", "7");
    }

    @Override
    public Widget asWidget() {
        return widget;
    }

    @Override
    public void ensureDebugId(String baseID) {
        notificationsBtn.ensureDebugId(baseID + DeModule.Ids.NOTIFICATION_BUTTON);
        userSettingsBtn.ensureDebugId(baseID + DeModule.Ids.USER_PREF_BUTTON);
        forumsBtn.ensureDebugId(baseID + DeModule.Ids.FORUMS_BUTTON);
        dataWinBtn.ensureDebugId(baseID + DeModule.Ids.DATA_BTN);
        appsWinBtn.ensureDebugId(baseID + DeModule.Ids.APPS_BTN);
        analysisWinBtn.ensureDebugId(baseID + DeModule.Ids.ANALYSES_BTN);
        feedbackBtn.ensureDebugId(baseID + DeModule.Ids.FEEDBACK_BTN);
        taskBar.ensureDebugId(baseID + DeModule.Ids.TASK_BAR);


        // TODO JDS Set debug ids for user settings menu
    }

    @Override
    public void setPresenter(final NewDesktopView.Presenter presenter) {
        this.presenter = presenter;
    }

    @UiHandler("analysisWinBtn")
    void onAnalysesWinBtnSelect(SelectEvent event) {
        presenter.onAnalysesWinBtnSelect();
    }

    @UiHandler("appsWinBtn")
    void onAppsWinBtnSelect(SelectEvent event) {
        presenter.onAppsWinBtnSelect();
    }

    @UiHandler("dataWinBtn")
    void onDataWinBtnSelect(SelectEvent event) {
        presenter.onDataWinBtnSelect();
    }

    @UiHandler("forumsBtn")
    void onForumsSelect(SelectEvent event) {
        presenter.onForumsBtnSelect();
    }

    @UiHandler("notificationsBtn")
    void onNotificationsSelect(SelectEvent event) {
        // Show userSettingsMenu
    }

    @UiHandler("userSettingsBtn")
    void onUserPrefsSelect(SelectEvent event) {
        // show userSettingsMenu
        userSettingsMenu.getMenu().show(userSettingsBtn.getElement(), anchorAlignment);
    }

}
