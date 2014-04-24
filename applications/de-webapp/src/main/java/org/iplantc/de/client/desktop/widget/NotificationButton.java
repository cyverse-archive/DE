package org.iplantc.de.client.desktop.widget;

import org.iplantc.de.client.DeResources;
import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.notifications.views.ViewNotificationMenu;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.gwt.user.client.Window;

import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.util.Point;
import com.sencha.gxt.widget.core.client.button.IconButton;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.event.ShowEvent;
import com.sencha.gxt.widget.core.client.event.ShowEvent.ShowHandler;

public class NotificationButton extends IconButton {

    private ViewNotificationMenu notificationsView;
    private final DeResources resources;
    private XElement countElement;
    private int count;

    public NotificationButton(DeResources resources) {
        super(resources.css().notifications());
        this.resources = resources;
        setSize("28", "28");
        setToolTip(I18N.DISPLAY.notifications());
        buildNotificationMenu();
        addSelectHandler(new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                Point p = getElement().getPosition(false);
                notificationsView.showAt(p.getX() - 50, p.getY() + 25);
            }
        });
        getElement().setAttribute("data-intro", I18N.TOUR.introNotifications());
        getElement().setAttribute("data-position", "left");
        getElement().setAttribute("data-step", "4");
    }

    public void setNotificationCount(int new_count) {
        if (new_count > 0 && new_count > getCount()) {
            notificationsView.fetchUnseenNotifications();
        } else {
            notificationsView.setUnseenNotificationsFetchedOnce(true);
        }
        notificationsView.setUnseenCount(new_count);
        this.count = new_count;
        if (countElement != null) {
            getElement().removeChild(countElement);
            countElement = null;
        }

        if (count > 0) {
            countElement = getElement().createChild(
                    "<span style='background-color:#DB6619; font-size:10px;top:0px;position:absolute;right:0px;color:white;padding:2px;height:10px;'> " + new_count + "</span>");
            Window.setTitle("(" + count + ") " + I18N.DISPLAY.rootApplicationTitle());
        } else {
            Window.setTitle(I18N.DISPLAY.rootApplicationTitle());
        }

    }

    public int getCount() {
        return count;
    }

    private void buildNotificationMenu() {
        notificationsView = new ViewNotificationMenu(EventBus.getInstance());
        notificationsView.setStyleName(resources.css().de_header_menu_body());
        notificationsView.addShowHandler(new ShowHandler() {

            @Override
            public void onShow(ShowEvent event) {
                notificationsView.addStyleName(resources.css().de_header_menu());
            }
        });
        notificationsView.addHideHandler(new HideHandler() {
            @Override
            public void onHide(HideEvent event) {
                notificationsView.removeStyleName(resources.css().de_header_menu());
            }
        });

        // do an initial fetch of the last 10 messages.
        notificationsView.fetchUnseenNotifications();
    }

}
