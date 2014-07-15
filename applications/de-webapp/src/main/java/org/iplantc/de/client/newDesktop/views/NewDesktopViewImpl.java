package org.iplantc.de.client.newDesktop.views;

import org.iplantc.de.client.desktop.widget.TaskBar;
import org.iplantc.de.client.newDesktop.NewDesktopView;
import org.iplantc.de.client.newDesktop.views.widgets.DesktopIconButton;
import org.iplantc.de.commons.client.widgets.IPlantAnchor;
import org.iplantc.de.resources.client.messages.IplantNewUserTourStrings;
import org.iplantc.de.shared.DeModule;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

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
    DesktopIconButton userSettingsBtn;
    @UiField
    IPlantAnchor preferencesBtn;
    @UiField
    IPlantAnchor collaboratorsBtn;
    @UiField
    IPlantAnchor systemMsgsBtn;
    @UiField
    IPlantAnchor documentationBtn;
    @UiField
    IPlantAnchor introBtn;
    @UiField
    IPlantAnchor contactSupportBtn;
    @UiField
    IPlantAnchor aboutBtn;
    @UiField
    IPlantAnchor logoutBtn;
    private static NewViewUiBinder ourUiBinder = GWT.create(NewViewUiBinder.class);
    private final Widget widget;
    private NewDesktopView.Presenter presenter;

    @Inject
    public NewDesktopViewImpl(final IplantNewUserTourStrings tourStrings) {
        widget = ourUiBinder.createAndBindUi(this);
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

    @Override
    public void setUnseenNotificationCount(int count) {

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

    @UiHandler({"preferencesBtn", "collaboratorsBtn", "systemMsgsBtn",
                   "documentationBtn", "introBtn", "contactSupportBtn", "aboutBtn", "logoutBtn"})
    void onAnyUserSettingsItemClick(ClickEvent event){
        userSettingsBtn.hideMenu();
    }

    @UiHandler("preferencesBtn")
    void onPreferencesClick(ClickEvent event){
        presenter.onPreferencesClick();
    }

    @UiHandler("collaboratorsBtn")
    void onCollaboratorsClick(ClickEvent event){
        presenter.onCollaboratorsClick();
    }

    @UiHandler("systemMsgsBtn")
    void onSystemMessagesClick(ClickEvent event){
        presenter.onSystemMessagesClick();
    }

    @UiHandler("documentationBtn")
    void onDocumentationClick(ClickEvent event){
        presenter.onDocumentationClick();
    }

    @UiHandler("introBtn")
    void onIntroClick(ClickEvent event){
        presenter.onIntroClick();
    }

    @UiHandler("contactSupportBtn")
    void onContactSupportClick(ClickEvent event){
        presenter.onContactSupportClick();
    }

    @UiHandler("aboutBtn")
    void onAboutClick(ClickEvent event){
        presenter.onAboutClick();
    }

    @UiHandler("logoutBtn")
    void onLogoutClick(ClickEvent event){
        presenter.onLogoutClick();
    }

}
