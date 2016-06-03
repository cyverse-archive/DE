package org.iplantc.de.desktop.client.views;

import org.iplantc.de.client.models.UserSettings;
import org.iplantc.de.client.models.notifications.NotificationMessage;
import org.iplantc.de.commons.client.widgets.IPlantAnchor;
import org.iplantc.de.desktop.client.DesktopView;
import org.iplantc.de.desktop.client.views.widgets.DEFeedbackDialog;
import org.iplantc.de.desktop.client.views.widgets.DesktopIconButton;
import org.iplantc.de.desktop.client.views.widgets.PreferencesDialog;
import org.iplantc.de.desktop.client.views.widgets.TaskBar;
import org.iplantc.de.desktop.client.views.widgets.TaskButton;
import org.iplantc.de.desktop.client.views.widgets.UnseenNotificationsView;
import org.iplantc.de.desktop.client.views.windows.IPlantWindowInterface;
import org.iplantc.de.desktop.shared.DeModule;
import org.iplantc.de.resources.client.messages.IplantNewUserTourStrings;

import com.google.common.base.Preconditions;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Provider;

import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.WindowManager;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.button.IconButton;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent;
import com.sencha.gxt.widget.core.client.event.RegisterEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.event.ShowContextMenuEvent;
import com.sencha.gxt.widget.core.client.event.UnregisterEvent;

/**
 * Created by jstroot on 7/6/14.
 * @author jstroot
 */
public class DesktopViewImpl implements DesktopView, UnregisterEvent.UnregisterHandler<Widget>, RegisterEvent.RegisterHandler<Widget> {

    interface DesktopViewImplUiBinder extends UiBinder<Widget, DesktopViewImpl> { }

    @UiField IconButton analysisWinBtn;
    @UiField IconButton appsWinBtn;
    @UiField IconButton dataWinBtn;
    @UiField IconButton feedbackBtn;
    @UiField IconButton forumsBtn;
    @UiField IconButton notificationsBtn;
    @UiField TaskBar taskBar;
    @UiField DesktopIconButton userSettingsBtn;
    @UiField IPlantAnchor preferencesBtn;
    @UiField IPlantAnchor collaboratorsBtn;
    @UiField IPlantAnchor systemMsgsBtn;
    @UiField IPlantAnchor documentationBtn;
    @UiField IPlantAnchor introBtn;
    @UiField IPlantAnchor contactSupportBtn;
    @UiField IPlantAnchor aboutBtn;
    @UiField IPlantAnchor logoutBtn;
    @UiField(provided = true) UnseenNotificationsView notificationsListView;
    @UiField DivElement desktopContainer;
    @UiField DesktopAppearance appearance;

    @Inject Provider<PreferencesDialog> preferencesDialogProvider;
    @Inject Provider<DEFeedbackDialog> deFeedbackDialogProvider;
    @Inject UserSettings userSettings;

    private static DesktopViewImplUiBinder ourUiBinder = GWT.create(DesktopViewImplUiBinder.class);
    private final Widget widget;
    private final SpanElement notificationCountElement;
    private final WindowManager windowManager;
    int unseenNotificationCount;
    private DesktopView.Presenter presenter;


    @Inject
    DesktopViewImpl(final IplantNewUserTourStrings tourStrings,
                    final WindowManager windowManager) {
        this.windowManager = windowManager;
        notificationsListView = new UnseenNotificationsView();
        widget = ourUiBinder.createAndBindUi(this);
        notificationCountElement = Document.get().createSpanElement();
        notificationCountElement.addClassName(appearance.styles().notificationCount());
        notificationCountElement.setAttribute("hidden", "");
        notificationsBtn.getElement().appendChild(notificationCountElement);

        windowManager.addRegisterHandler(this);
        windowManager.addUnregisterHandler(this);
        initIntroAttributes(tourStrings);
    }

    @UiHandler("notificationsBtn")
    void onNotificationMenuClicked(ShowContextMenuEvent event){
        if(unseenNotificationCount < 10){
            presenter.doMarkAllSeen(false);
        }
    }

    public void hideNotificationMenu() {
        ((DesktopIconButton)notificationsBtn).hideMenu();
    }

    @Override
    public void onRegister(RegisterEvent<Widget> event) {
        final Widget eventItem = event.getItem();

        if(eventItem instanceof IPlantWindowInterface) {
            com.sencha.gxt.widget.core.client.Window iplantWindow = (com.sencha.gxt.widget.core.client.Window) eventItem;
            // If it already exists, mark button active
            for(TaskButton btn : taskBar.getButtons()){
                if(btn.getWindow() == iplantWindow){
                    // If it already exists, do not re-add
                    return;
                }
            }

            // If it is new, add task button and mark active
            taskBar.addTaskButton(iplantWindow);
        }
    }

    @Override
    public void onUnregister(UnregisterEvent<Widget> event) {

        final Widget eventItem = event.getItem();
        if(eventItem instanceof IPlantWindowInterface) {
            IPlantWindowInterface iplantWindow = (IPlantWindowInterface) eventItem;
            TaskButton taskButton = null;
            for(TaskButton btn : taskBar.getButtons()){
                if(btn.getWindow() == iplantWindow){
                   taskButton = btn;
                    break;
                }
            }

            Preconditions.checkNotNull(taskButton, "TaskButton should not be null");
            if(iplantWindow.isMinimized()){
                // Re register
                windowManager.register(eventItem);
                return;
            }
            // remove corresponding task button
            taskBar.removeTaskButton(taskButton);
        }
    }

    private void initIntroAttributes(IplantNewUserTourStrings tourStrings) {
        // FIXME Need to move intro to themes
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
        userSettingsBtn.ensureDebugId(baseID + DeModule.Ids.USER_PREF_MENU);
        forumsBtn.ensureDebugId(baseID + DeModule.Ids.FORUMS_BUTTON);
        dataWinBtn.ensureDebugId(baseID + DeModule.Ids.DATA_BTN);
        appsWinBtn.ensureDebugId(baseID + DeModule.Ids.APPS_BTN);
        analysisWinBtn.ensureDebugId(baseID + DeModule.Ids.ANALYSES_BTN);
        feedbackBtn.ensureDebugId(baseID + DeModule.Ids.FEEDBACK_BTN);
        taskBar.ensureDebugId(baseID + DeModule.Ids.TASK_BAR);


        // User Settings Menu Items
        preferencesBtn.ensureDebugId(baseID + DeModule.Ids.USER_PREF_MENU + DeModule.Ids.PREFERENCES_BTN);
        collaboratorsBtn.ensureDebugId(baseID + DeModule.Ids.USER_PREF_MENU + DeModule.Ids.COLLABORATORS_BTN);
        systemMsgsBtn.ensureDebugId(baseID + DeModule.Ids.USER_PREF_MENU + DeModule.Ids.SYS_MSGS_BTN);
        documentationBtn.ensureDebugId(baseID + DeModule.Ids.USER_PREF_MENU + DeModule.Ids.USER_MANUAL_BTN);
        introBtn.ensureDebugId(baseID + DeModule.Ids.USER_PREF_MENU + DeModule.Ids.INTRO_BTN);
        contactSupportBtn.ensureDebugId(baseID + DeModule.Ids.USER_PREF_MENU + DeModule.Ids.SUPPORT_BTN);
        aboutBtn.ensureDebugId(baseID + DeModule.Ids.USER_PREF_MENU + DeModule.Ids.ABOUT_BTN);
        logoutBtn.ensureDebugId(baseID + DeModule.Ids.USER_PREF_MENU + DeModule.Ids.LOGOUT_BTN);
    }

    @Override
    public Element getDesktopContainer() {
        return desktopContainer;
    }

    @Override
    public ListStore<NotificationMessage> getNotificationStore() {
        return notificationsListView.getStore();
    }

    @Override
    public int getUnseenNotificationCount() {
        return unseenNotificationCount;
    }

    @Override
    public void setPresenter(final DesktopView.Presenter presenter) {
        this.presenter = presenter;
        notificationsListView.setPresenter(presenter);
    }

    @Override
    public void setUnseenNotificationCount(int count) {
        this.unseenNotificationCount = count;
        if(count > 0){
            notificationCountElement.setInnerText(Integer.toString(count));
            notificationCountElement.removeAttribute("hidden");
            Window.setTitle(appearance.rootApplicationTitle(count));
        }else {
            notificationCountElement.setAttribute("hidden", "");
            notificationCountElement.setInnerText(null);
            Window.setTitle(appearance.rootApplicationTitle());
        }
        notificationsListView.onUnseenCountUpdated(count);
    }

    @Override
    public void setUnseenSystemMessageCount(int count) {
        String labelText = appearance.systemMessagesLabel();
        if(count > 0) {
            labelText += " (" + count + ")";
        }
        systemMsgsBtn.setText(labelText);
    }

    @UiHandler("notificationsListView")
    void onUnseenNotificationSelected(SelectionEvent<NotificationMessage> event){
        presenter.onNotificationSelected(event.getSelectedItem());
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

    @UiHandler("feedbackBtn")
    void onFeedbackBtnSelect(SelectEvent event) {
        final DEFeedbackDialog feedbackDialog = deFeedbackDialogProvider.get();
        feedbackDialog.show();
        feedbackDialog.getButton(PredefinedButton.OK).addSelectHandler(new SelectEvent.SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                if(feedbackDialog.validate()){
                    presenter.submitUserFeedback(feedbackDialog.toJson(), feedbackDialog);
                } else {
                    AlertMessageBox amb = new AlertMessageBox(appearance.feedbackAlertValidationWarning(),
                                                              appearance.completeRequiredFieldsError());
                    amb.setModal(true);
                    amb.show();
                }
            }
        });

        feedbackDialog.getButton(PredefinedButton.CANCEL).addSelectHandler(new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                feedbackDialog.hide();

            }
        });

    }

    @UiHandler("forumsBtn")
    void onForumsSelect(SelectEvent event) {
        presenter.onForumsBtnSelect();
    }

    @UiHandler({"preferencesBtn", "collaboratorsBtn", "systemMsgsBtn",
                   "documentationBtn", "introBtn", "contactSupportBtn", "aboutBtn", "logoutBtn"})
    void onAnyUserSettingsItemClick(ClickEvent event){
        userSettingsBtn.hideMenu();
    }

    @UiHandler("preferencesBtn")
    void onPreferencesClick(ClickEvent event){
        final PreferencesDialog preferencesDialog = preferencesDialogProvider.get();
        final UserSettings userSettingsCopy = new UserSettings(userSettings.asSplittable());
        preferencesDialog.initAndShow(userSettingsCopy);
        preferencesDialog.addDialogHideHandler(new DialogHideEvent.DialogHideHandler() {
            @Override
            public void onDialogHide(DialogHideEvent event) {
                if(PredefinedButton.OK.equals(event.getHideButton())){
                    presenter.saveUserSettings(preferencesDialog.getValue(), false);
                    preferencesDialog.hide();
                }
            }
        });
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
        presenter.doLogout(false);
    }

}
