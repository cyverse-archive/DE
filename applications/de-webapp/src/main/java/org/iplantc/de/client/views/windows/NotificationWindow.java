/**
 * 
 */
package org.iplantc.de.client.views.windows;

import org.iplantc.de.client.models.WindowState;
import org.iplantc.de.client.models.notifications.NotificationCategory;
import org.iplantc.de.client.models.notifications.NotificationMessage;
import org.iplantc.de.client.notifications.presenter.NotificationPresenter;
import org.iplantc.de.client.notifications.views.NotificationMessageProperties;
import org.iplantc.de.client.notifications.views.NotificationView;
import org.iplantc.de.client.notifications.views.NotificationViewImpl;
import org.iplantc.de.client.notifications.views.cells.NotificationMessageCell;
import org.iplantc.de.client.views.windows.configs.ConfigFactory;
import org.iplantc.de.client.views.windows.configs.NotifyWindowConfig;

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

    private static CheckBoxSelectionModel<NotificationMessage> checkBoxModel;
    private final NotificationView.Presenter presenter;

    public NotificationWindow(NotifyWindowConfig config) {
        super(null, null);
        setTitle(org.iplantc.de.resources.client.messages.I18N.DISPLAY.notifications());
        NotificationKeyProvider keyProvider = new NotificationKeyProvider();
        ListStore<NotificationMessage> store = new ListStore<NotificationMessage>(keyProvider);
        ColumnModel<NotificationMessage> cm = buildNotificationColumnModel();
        NotificationView view = new NotificationViewImpl(store, cm, checkBoxModel);
        presenter = new NotificationPresenter(view);
        setSize("600", "375");
        presenter.go(this);
        if (config != null) {
            presenter.filterBy(config.getSortCategory());
        }
    }

    @SuppressWarnings("unchecked")
    private static ColumnModel<NotificationMessage> buildNotificationColumnModel() {
        NotificationMessageProperties props = GWT.create(NotificationMessageProperties.class);
        List<ColumnConfig<NotificationMessage, ?>> configs = new LinkedList<ColumnConfig<NotificationMessage, ?>>();

        checkBoxModel = new CheckBoxSelectionModel<NotificationMessage>(
                new IdentityValueProvider<NotificationMessage>());
        @SuppressWarnings("rawtypes")
        ColumnConfig colCheckBox = checkBoxModel.getColumn();
        configs.add(colCheckBox);

        ColumnConfig<NotificationMessage, NotificationCategory> colCategory = new ColumnConfig<NotificationMessage, NotificationCategory>(
                props.category(), 100);
        colCategory.setHeader(org.iplantc.de.resources.client.messages.I18N.DISPLAY.category());
        configs.add(colCategory);
        colCategory.setMenuDisabled(true);
        colCategory.setSortable(false);

        ColumnConfig<NotificationMessage, NotificationMessage> colMessage = new ColumnConfig<NotificationMessage, NotificationMessage>(
                new IdentityValueProvider<NotificationMessage>(), 420);
        colMessage.setHeader(org.iplantc.de.resources.client.messages.I18N.DISPLAY.messagesGridHeader());
        colMessage.setCell(new NotificationMessageCell());
        configs.add(colMessage);
        colMessage.setSortable(false);
        colMessage.setMenuDisabled(true);

        ColumnConfig<NotificationMessage, Date> colTimestamp = new ColumnConfig<NotificationMessage, Date>(
                new ValueProvider<NotificationMessage, Date>() {

                    @Override
                    public Date getValue(NotificationMessage object) {
                        return new Date(object.getTimestamp());
                    }

                    @Override
                    public void setValue(NotificationMessage object, Date value) {
                        // do nothing
                    }

                    @Override
                    public String getPath() {
                        return "timestamp";
                    }
                }, 170);
        colTimestamp.setCell(new DateCell(DateTimeFormat
                .getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM)));
        colTimestamp.setHeader(org.iplantc.de.resources.client.messages.I18N.DISPLAY.createdDateGridHeader());

        configs.add(colTimestamp);
        ColumnModel<NotificationMessage> columnModel = new ColumnModel<NotificationMessage>(configs);
        return columnModel;
    }

    private class NotificationKeyProvider implements ModelKeyProvider<NotificationMessage> {

        @Override
        public String getKey(NotificationMessage item) {
            return item.getId();
        }

    }

    @Override
    public WindowState getWindowState() {
        NotifyWindowConfig config = ConfigFactory.notifyWindowConfig(presenter.getCurrentCategory());
        return createWindowState(config);
    }

}
