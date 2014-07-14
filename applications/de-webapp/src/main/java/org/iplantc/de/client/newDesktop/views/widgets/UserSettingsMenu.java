package org.iplantc.de.client.newDesktop.views.widgets;

import org.iplantc.de.client.newDesktop.NewDesktopView;
import org.iplantc.de.commons.client.widgets.IPlantAnchor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;

import com.sencha.gxt.widget.core.client.menu.Menu;

/**
 * Created by jstroot on 7/10/14.
 */
public class UserSettingsMenu {
    public interface UserSettingsMenuAppearance {
        interface UserSettingsMenuStyles extends CssResource {
            String menu();
        }

        UserSettingsMenuStyles styles();
    }

    interface UserPreferencesViewUiBinder extends UiBinder<Menu, UserSettingsMenu> { }

    private static UserPreferencesViewUiBinder ourUiBinder = GWT.create(UserPreferencesViewUiBinder.class);
    @UiField
    IPlantAnchor logoutBtn;
    @UiField
    IPlantAnchor aboutBtn;
    @UiField
    IPlantAnchor contactSupportBtn;
    @UiField
    IPlantAnchor introBtn;
    @UiField
    IPlantAnchor documentationBtn;
    @UiField
    IPlantAnchor systemMsgsBtn;
    @UiField
    IPlantAnchor collaboratorsBtn;
    @UiField
    IPlantAnchor preferencesBtn;
    @UiField
    UserSettingsMenuAppearance appearance;
    private final Menu menu;
    private NewDesktopView.UserSettingsMenuPresenter presenter;

    public UserSettingsMenu() {
        menu = ourUiBinder.createAndBindUi(this);
    }

    public Menu getMenu() {
        return menu;
    }

    public void setPresenter(NewDesktopView.UserSettingsMenuPresenter presenter){
        this.presenter = presenter;
    }

    @UiHandler({"preferencesBtn", "collaboratorsBtn", "systemMsgsBtn",
                   "documentationBtn", "introBtn", "contactSupportBtn", "aboutBtn", "logoutBtn"})
    void onAnyItemClick(ClickEvent event){
        menu.hide();
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