/**
 * 
 */
package org.iplantc.de.client.desktop.views;

import org.iplantc.de.client.DeResources;
import org.iplantc.de.client.desktop.widget.Desktop;
import org.iplantc.de.client.desktop.widget.ForumsButton;
import org.iplantc.de.client.desktop.widget.NotificationButton;
import org.iplantc.de.client.desktop.widget.UserPreferencesButton;
import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.events.NotificationCountUpdateEvent;
import org.iplantc.de.client.events.NotificationCountUpdateEvent.NotificationCountUpdateEventHandler;
import org.iplantc.de.client.models.WindowState;
import org.iplantc.de.commons.client.CommonUiConstants;
import org.iplantc.de.resources.client.DEHeaderStyle;
import org.iplantc.de.resources.client.IplantResources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;

import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.widget.core.client.container.AbstractHtmlLayoutContainer.HtmlData;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HtmlLayoutContainer;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;

import java.util.ArrayList;
import java.util.List;

/**
 * Default DE View as Desktop
 * 
 * FIXME JDS Move more UI construction into ui.xml
 * 
 * @author sriram
 * 
 */
public class DEViewImpl implements DEView {

    private static DEViewUiBinder uiBinder = GWT.create(DEViewUiBinder.class);
    private final CommonUiConstants CONSTANTS = GWT.create(CommonUiConstants.class);

    @UiField
    SimpleContainer headerPanel;
    @UiField
    SimpleContainer mainPanel;

    @UiField
    MarginData centerData;
    @UiField
    BorderLayoutContainer con;

    private final Widget widget;

    private final DeResources resources;
    private final EventBus eventBus;

    private DEView.Presenter presenter;
    private final Desktop desktop;
    private final HeaderTemplate r;
    private final DEHeaderStyle headerResources;

    private final List<HandlerRegistration> eventHandlers = new ArrayList<HandlerRegistration>();
    private UserPreferencesButton userMenu;
    private NotificationButton notify;

    @UiTemplate("DEView.ui.xml")
    interface DEViewUiBinder extends UiBinder<Widget, DEViewImpl> {
    }

    interface HeaderTemplate extends XTemplates {
        @XTemplate(source = "template_de.html")
        public SafeHtml render(DEHeaderStyle style);
    }

    public DEViewImpl(final DeResources resources, final EventBus eventBus) {
        this.resources = resources;
        this.eventBus = eventBus;
        widget = uiBinder.createAndBindUi(this);

        desktop = new Desktop(resources, eventBus);
        con.remove(con.getCenterWidget());
        con.setCenterWidget(desktop, centerData);

        resources.css().ensureInjected();
        con.setStyleName(resources.css().iplantcBackground());
        initEventHandlers();

        headerResources = IplantResources.RESOURCES.getHeaderStyle();
        headerResources.ensureInjected();
        r = GWT.create(HeaderTemplate.class);
    }

    @Override
    public Widget asWidget() {
        return widget;
    }

    private void initEventHandlers() {
        // handle data events
        eventHandlers.add(eventBus.addHandler(NotificationCountUpdateEvent.TYPE,
                new NotificationCountUpdateEventHandler() {

                    @Override
                    public void onCountUpdate(NotificationCountUpdateEvent ncue) {
                notify.setNotificationCount(ncue.getTotal());
                    }
                }));
    }

    @Override
    public void drawHeader() {
        HtmlLayoutContainer c = new HtmlLayoutContainer(r.render(headerResources));
        headerPanel.setWidget(c);
        c.add(buildHtmlActionsPanel(), new HtmlData(".menu_container"));
    }

    private HtmlLayoutContainer buildHtmlActionsPanel() {
        HtmlLayoutContainerTemplate templates = GWT.create(HtmlLayoutContainerTemplate.class);

        HtmlLayoutContainer c = new HtmlLayoutContainer(templates.getTemplate());
        notify = new NotificationButton(resources);
        c.add(notify, new HtmlData(".cell1"));
        userMenu = new UserPreferencesButton(this, resources, CONSTANTS);
        c.add(userMenu, new HtmlData(".cell2"));
        c.add(new ForumsButton(resources, CONSTANTS), new HtmlData(".cell5"));
        return c;

    }


    public interface HtmlLayoutContainerTemplate extends XTemplates {
        @XTemplate(source = "template_menu.html")
        SafeHtml getTemplate();
    }

    @Override
    public void setPresenter(DEView.Presenter presenter) {
        this.presenter = presenter;
    }


    @Override
    public List<WindowState> getOrderedWindowStates() {
        return desktop.getOrderedWindowStates();
    }

    @Override
    public void restoreWindows(List<WindowState> windowStates) {
        for (WindowState ws : windowStates) {
            desktop.restoreWindow(ws);
        }
    }

    /**
     * @see DEView#updateUnseenSystemMessageCount(long)
     */
    @Override
    public void updateUnseenSystemMessageCount(final long numUnseenSysMsgs) {
        userMenu.updateSystemMessageLabel(numUnseenSysMsgs);
    }

    @Override
    public Desktop getDesktop() {
        return desktop;
    }

    @Override
    public void cleanUp() {
        EventBus eventBus = EventBus.getInstance();
        for (HandlerRegistration hr : eventHandlers) {
            eventBus.removeHandler(hr);
        }

        desktop.cleanUp();

    }

    @Override
    public void doLogout() {
        presenter.doLogout();

    }

    @Override
    public void doIntro() {
        presenter.doWelcomeIntro();

    }

}
