package org.iplantc.de.theme.base.client.desktop.views;

import org.iplantc.de.client.models.notifications.NotificationMessage;
import org.iplantc.de.desktop.client.views.widgets.UnseenNotificationsView;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.text.shared.AbstractSafeHtmlRenderer;

import com.sencha.gxt.cell.core.client.SimpleSafeHtmlCell;

/**
 * @author jstroot
 */
public class BaseUnseenNotificationsAppearance implements UnseenNotificationsView.UnseenNotificationsAppearance {

    public interface UnseenNotificationStyles extends CssResource {

        String cell();

        String cellHighlight();

        String lsOverStyle();

        String lvItemSelector();

        String lvSelStyle();
    }
    public interface UnseenNotificationResources extends ClientBundle {

        @Source("org/iplantc/de/theme/base/client/desktop/views/UnseenNotifications.css")
        UnseenNotificationStyles style();
    }

    public interface UnseenNotificationsTemplates extends SafeHtmlTemplates {

        @Template("<div class='{0}'>{1}</div>")
        SafeHtml customLvRenderItem(String style, SafeHtml content);

        @Template("<div class='{0}'></div>")
        SafeHtml customLbRenderEnd(String style);

        @Template("<div class='{0}'>{1}</div>")
        SafeHtml renderCell(String style, String content);

    }

    private final UnseenNotificationsStrings strings;

    private final UnseenNotificationStyles style;
    private final UnseenNotificationsTemplates templates;
    private final Cell<NotificationMessage> listViewCell;

    public BaseUnseenNotificationsAppearance(final UnseenNotificationResources resources,
                                             final UnseenNotificationsStrings strings) {
        this.strings = strings;
        this.style = resources.style();
        this.style.ensureInjected();
        templates = GWT.create(UnseenNotificationsTemplates.class);

        listViewCell = new SimpleSafeHtmlCell<>(new AbstractSafeHtmlRenderer<NotificationMessage>() {
            @Override
            public SafeHtml render(NotificationMessage object) {
                if(object.isSeen()){
                    return templates.renderCell(style.cell(), object.getMessage());
                }
                return templates.renderCell(style.cellHighlight(), object.getMessage());
            }
        });
    }

    public BaseUnseenNotificationsAppearance(){
        this(GWT.<UnseenNotificationResources> create(UnseenNotificationResources.class),
             GWT.<UnseenNotificationsStrings> create(UnseenNotificationsStrings.class));
    }

    @Override
    public String allNotifications() {
        return strings.allNotifications();
    }

    @Override
    public Cell<NotificationMessage> getListViewCell() {
        return listViewCell;
    }

    @Override
    public String newNotificationsLink(int unseenCount) {
        return strings.newNotificationsLink(unseenCount);
    }

    @Override
    public String markAllAsSeen() {
        return strings.markAllAsSeen();
    }

    @Override
    public String noNewNotifications() {
        return strings.noNewNotifications();
    }

    @Override
    public String unseenNotificationsViewWidth() {
        return "250px";
    }

    @Override
    public String unseenNotificationsViewHeight() {
        return "220px";
    }

}
