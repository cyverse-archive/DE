package org.iplantc.de.client.desktop.widget;

import org.iplantc.de.client.Constants;
import org.iplantc.de.client.DeResources;
import org.iplantc.de.client.desktop.views.DEView;
import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.events.ShowAboutWindowEvent;
import org.iplantc.de.client.events.ShowSystemMessagesEvent;
import org.iplantc.de.client.preferences.views.PreferencesDialog;
import org.iplantc.de.commons.client.CommonUiConstants;
import org.iplantc.de.commons.client.collaborators.presenter.ManageCollaboratorsPresenter.MODE;
import org.iplantc.de.commons.client.collaborators.views.ManageCollaboratorsDailog;
import org.iplantc.de.commons.client.util.WindowUtil;
import org.iplantc.de.commons.client.widgets.IPlantAnchor;
import org.iplantc.de.resources.client.messages.I18N;
import org.iplantc.de.shared.DeModule;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;

import com.sencha.gxt.core.client.Style.Anchor;
import com.sencha.gxt.core.client.Style.AnchorAlignment;
import com.sencha.gxt.widget.core.client.button.IconButton;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.event.ShowEvent;
import com.sencha.gxt.widget.core.client.event.ShowEvent.ShowHandler;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.SeparatorMenuItem;

public class UserPreferencesButton extends IconButton {

    private final DeResources resources;
    private Menu userMenu;
    private final CommonUiConstants CONSTANTS;
    private IPlantAnchor sysMsgsMenuItem;
    private final DEView view;

    public UserPreferencesButton(DEView view, final DeResources resources, CommonUiConstants CONSTANTS) {
        super(resources.css().userPref());
        this.view = view;
        this.resources = resources;
        this.CONSTANTS = CONSTANTS;
        setSize("28", "28");
        setToolTip(I18N.DISPLAY.preferences());
        buildUserMenu();
        addSelectHandler(new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                AnchorAlignment menuAlign = new AnchorAlignment(Anchor.TOP_LEFT, Anchor.BOTTOM_LEFT, true);
                userMenu.show(getElement(), menuAlign);
            }
        });
        getElement().setAttribute("data-intro", I18N.TOUR.introSettings());
        getElement().setAttribute("data-position", "left");
        getElement().setAttribute("data-step", "5");
        ensureDebugId(DeModule.Ids.DESKTOP + DeModule.Ids.USER_PREF_BUTTON);
        addHandler(new MouseOverHandler() {

            @Override
            public void onMouseOver(MouseOverEvent event) {
                changeStyle(resources.css().userPrefHover());
            }
        }, MouseOverEvent.getType());

        addHandler(new MouseOutHandler() {

            @Override
            public void onMouseOut(MouseOutEvent event) {
                changeStyle(resources.css().userPref());
            }
        }, MouseOutEvent.getType());
    }

    public void updateSystemMessageLabel(final long numUnseenSysMsgs) {
        String lbl = I18N.DISPLAY.systemMessagesLabel();
        if (numUnseenSysMsgs > 0) {
            lbl += " (" + numUnseenSysMsgs + ")";
        }
        sysMsgsMenuItem.setText(lbl);
    }

    private void buildUserMenu() {
        userMenu = buildMenu();

        userMenu.add(buildPrefMenuItem());
        userMenu.add(buildCollabMenuItem());

        userMenu.add(buildSysMsgMenuItem());

        userMenu.add(new SeparatorMenuItem());

        userMenu.add(buildHelpMenuItem());
        userMenu.add(buildIntroMenuItem());
        userMenu.add(buildContactMenuItem());
        userMenu.add(buildAboutMenuItem());

        userMenu.add(new SeparatorMenuItem());

        userMenu.add(buildLogoutMenuItem());
        userMenu.addShowHandler(new ShowHandler() {

            @Override
            public void onShow(ShowEvent event) {
                userMenu.addStyleName(resources.css().de_header_menu());

            }
        });
        userMenu.addHideHandler(new HideHandler() {
            @Override
            public void onHide(HideEvent event) {
                userMenu.removeStyleName(resources.css().de_header_menu());
            }
        });

    }

    private IPlantAnchor buildLogoutMenuItem() {
        IPlantAnchor anchor = new IPlantAnchor(I18N.DISPLAY.logout(), -1, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                view.doLogout();
                userMenu.hide();
            }
        });

        anchor.setId("idLogoutMenuItem");
        return anchor;
    }

    private IPlantAnchor buildAboutMenuItem() {
        IPlantAnchor anchor = new IPlantAnchor(I18N.DISPLAY.about(), -1, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                // displayAboutDe();
                EventBus.getInstance().fireEvent(new ShowAboutWindowEvent());
                userMenu.hide();
            }
        });
        anchor.setId("idAboutMenuItem");
        return anchor;
    }

    private IPlantAnchor buildSysMsgMenuItem() {
        sysMsgsMenuItem = new IPlantAnchor(I18N.DISPLAY.systemMessagesLabel(), -1, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                EventBus.getInstance().fireEvent(new ShowSystemMessagesEvent());
                userMenu.hide();
            }
        });
        return sysMsgsMenuItem;
    }

    private IPlantAnchor buildContactMenuItem() {
        IPlantAnchor anchor = new IPlantAnchor(I18N.DISPLAY.contactSupport(), -1, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                WindowUtil.open(CONSTANTS.supportUrl());
                userMenu.hide();
            }
        });
        anchor.setId("idSupportMenuItem");
        return anchor;
    }

    private IPlantAnchor buildIntroMenuItem() {
        IPlantAnchor anchor = new IPlantAnchor(I18N.DISPLAY.introduction(), -1, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                view.doIntro();
                userMenu.hide();
            }
        });
        anchor.setId("idIntroMenuItem");
        return anchor;
    }

    private IPlantAnchor buildHelpMenuItem() {
        IPlantAnchor anchor = new IPlantAnchor(I18N.DISPLAY.documentation(), -1, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                WindowUtil.open(Constants.CLIENT.deHelpFile());
                userMenu.hide();
            }
        });
        anchor.setId("idDocMenuItem");
        return anchor;
    }

    private IPlantAnchor buildCollabMenuItem() {
        IPlantAnchor anchor = new IPlantAnchor(I18N.DISPLAY.collaborators(), -1, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ManageCollaboratorsDailog dialog = new ManageCollaboratorsDailog(MODE.MANAGE);
                dialog.show();
                userMenu.hide();
            }
        });
        anchor.setId("idCollabMenuItem");
        return anchor;
    }

    private IPlantAnchor buildPrefMenuItem() {
        IPlantAnchor anchor = new IPlantAnchor(I18N.DISPLAY.preferences(), -1, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                buildAndShowPreferencesDialog();
                userMenu.hide();
            }

        });
        anchor.setId("idPrefMenuItem");
        return anchor;
    }

    private void buildAndShowPreferencesDialog() {
        PreferencesDialog d = new PreferencesDialog();
        d.show();
    }

    private Menu buildMenu() {
        Menu d = new Menu();
        d.setStyleName(resources.css().de_header_menu_body());
        return d;
    }

}
