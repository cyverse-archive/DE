/**
 *
 */
package org.iplantc.de.client.windows;

import org.iplantc.de.client.models.WindowState;
import org.iplantc.de.client.models.notifications.NotificationCategory;
import org.iplantc.de.client.models.notifications.NotificationMessage;
import org.iplantc.de.commons.client.views.window.configs.ConfigFactory;
import org.iplantc.de.commons.client.views.window.configs.NotifyWindowConfig;
import org.iplantc.de.notifications.client.presenter.NotificationPresenterImpl;
import org.iplantc.de.notifications.client.views.NotificationMessageProperties;
import org.iplantc.de.notifications.client.views.NotificationView;
import org.iplantc.de.notifications.client.views.NotificationViewImpl;
import org.iplantc.de.notifications.client.views.cells.NotificationMessageCell;
import org.iplantc.de.resources.client.messages.I18N;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;
import org.iplantc.de.shared.DeModule;

import com.google.gwt.cell.client.DateCell;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;

import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.widget.core.client.grid.CheckBoxSelectionModel;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * @author sriram
 */
public class NotificationWindow extends IplantWindowBase {

    private class NotificationKeyProvider implements ModelKeyProvider<NotificationMessage> {

        @Override
        public String getKey(NotificationMessage item) {
            return item.getId();
        }

    }

    private static CheckBoxSelectionModel<NotificationMessage> checkBoxModel;
    private final IplantDisplayStrings displayStrings;
    private final NotificationView.Presenter presenter;

    public NotificationWindow(NotifyWindowConfig config) {
        super(null, config);
        displayStrings = I18N.DISPLAY;
        setHeadingText(displayStrings.notifications());
        NotificationKeyProvider keyProvider = new NotificationKeyProvider();
        ListStore<NotificationMessage> store = new ListStore<>(keyProvider);
        ColumnModel<NotificationMessage> cm = buildNotificationColumnModel();
        NotificationView view = new NotificationViewImpl(store, cm, checkBoxModel);
        presenter = new NotificationPresenterImpl(view);
        ensureDebugId(DeModule.WindowIds.NOTIFICATION);
        setSize("600", "375");
        presenter.go(this);
        if (config != null) {
            presenter.filterBy(config.getSortCategory());
        }
    }

    @Override
    public WindowState getWindowState() {
        NotifyWindowConfig config = ConfigFactory.notifyWindowConfig(presenter.getCurrentCategory());
        return createWindowState(config);
    }

    @SuppressWarnings("unchecked")
    private ColumnModel<NotificationMessage> buildNotificationColumnModel() {
        NotificationMessageProperties props = GWT.create(NotificationMessageProperties.class);
        List<ColumnConfig<NotificationMessage, ?>> configs = new LinkedList<>();

        checkBoxModel = new CheckBoxSelectionModel<>(new IdentityValueProvider<NotificationMessage>());
        @SuppressWarnings("rawtypes")
        ColumnConfig colCheckBox = checkBoxModel.getColumn();
        configs.add(colCheckBox);

        ColumnConfig<NotificationMessage, NotificationCategory> colCategory = new ColumnConfig<>(props.category(), 100);
        colCategory.setHeader(displayStrings.category());
        configs.add(colCategory);
        colCategory.setMenuDisabled(true);
        colCategory.setSortable(false);

        ColumnConfig<NotificationMessage, NotificationMessage> colMessage = new ColumnConfig<>(new IdentityValueProvider<NotificationMessage>(), 420);
        colMessage.setHeader(displayStrings.messagesGridHeader());
        colMessage.setCell(new NotificationMessageCell());
        configs.add(colMessage);
        colMessage.setSortable(false);
        colMessage.setMenuDisabled(true);

        ColumnConfig<NotificationMessage, Date> colTimestamp = new ColumnConfig<>(new ValueProvider<NotificationMessage, Date>() {

            @Override
            public Date getValue(NotificationMessage object) {
                return new Date(object.getTimestamp());
            }

            @Override
            public void setValue(NotificationMessage object,
                                 Date value) {
                // do nothing
            }

            @Override
            public String getPath() {
                return "timestamp";
            }
        }, 170);
        colTimestamp.setCell(new DateCell(DateTimeFormat
                                              .getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM)));
        colTimestamp.setHeader(displayStrings.createdDateGridHeader());

        configs.add(colTimestamp);
        return new ColumnModel<>(configs);
    }

}
