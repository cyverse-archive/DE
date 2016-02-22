/**
 *
 */
package org.iplantc.de.notifications.client.views;

import org.iplantc.de.client.models.notifications.NotificationCategory;
import org.iplantc.de.client.models.notifications.NotificationMessage;
import org.iplantc.de.commons.client.widgets.DEPagingToolbar;
import org.iplantc.de.notifications.client.events.NotificationGridRefreshEvent;
import org.iplantc.de.notifications.client.events.NotificationSelectionEvent;
import org.iplantc.de.notifications.client.model.NotificationMessageProperties;
import org.iplantc.de.notifications.client.views.cells.NotificationMessageCell;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.gwt.cell.client.DateCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoader;
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.event.RefreshEvent;
import com.sencha.gxt.widget.core.client.grid.CheckBoxSelectionModel;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent.SelectionChangedHandler;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * 
 * Notification View as grid
 * 
 * @author sriram
 * 
 */
public class NotificationViewImpl extends Composite implements NotificationView {

    interface NotificationViewImplUiBinder extends UiBinder<Widget, NotificationViewImpl> {
    }

    private static NotificationViewImplUiBinder uiBinder = GWT.create(NotificationViewImplUiBinder.class);

    @UiField(provided = true) final ListStore<NotificationMessage> listStore;
    @UiField Grid<NotificationMessage> grid;
    @UiField FramedPanel mainPanel;
    @UiField BorderLayoutContainer con;
    @UiField DEPagingToolbar toolBar;
    @UiField BorderLayoutData northData;

    CheckBoxSelectionModel<NotificationMessage> checkBoxModel;
    private NotificationViewAppearance appearance;

    @Inject
    public NotificationViewImpl(@Assisted ListStore<NotificationMessage> listStore,
                                NotificationViewAppearance appearance) {
        this.listStore = listStore;
        this.appearance = appearance;
        initWidget(uiBinder.createAndBindUi(this));
        toolBar.getElement().getStyle().setProperty("borderBottom", "none");
        grid.setSelectionModel(checkBoxModel);
        grid.getSelectionModel().setSelectionMode(SelectionMode.MULTI);
        addGridSelectionHandler();
        addGridRefreshHandler();
    }


    @Override
    public HandlerRegistration addNotificationGridRefreshEventHandler(NotificationGridRefreshEvent.NotificationGridRefreshEventHandler handler) {
        return addHandler(handler, NotificationGridRefreshEvent.TYPE);
    }

    @Override
    public HandlerRegistration addNotificationSelectionEventHandler(NotificationSelectionEvent.NotificationSelectionEventHandler handler) {
        return addHandler(handler, NotificationSelectionEvent.TYPE);
    }

    private void addGridRefreshHandler() {
        grid.addRefreshHandler(new RefreshEvent.RefreshHandler() {
            @Override
            public void onRefresh(RefreshEvent event) {
                fireEvent(new NotificationGridRefreshEvent());
            }
        });
    }

    private void addGridSelectionHandler() {
        grid.getSelectionModel().addSelectionChangedHandler(
                new SelectionChangedHandler<NotificationMessage>() {

                    @Override
                    public void onSelectionChanged(SelectionChangedEvent<NotificationMessage> event) {
                        fireEvent(new NotificationSelectionEvent(event.getSelection()));
                    }
                });
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.gwt.user.client.ui.IsWidget#asWidget()
     */
    @Override
    public Widget asWidget() {
        return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.iplantc.de.client.gxt3.views.NotificationView#getSelectedItems()
     */
    @Override
    public List<NotificationMessage> getSelectedItems() {
        return grid.getSelectionModel().getSelectedItems();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void loadNotifications(FilterPagingLoadConfig config) {
        listStore.clear();
        ((PagingLoader<FilterPagingLoadConfig, PagingLoadResult<NotificationMessage>>)grid.getLoader())
                .load(config);
    }

    @Override
    public void setLoader(
            PagingLoader<FilterPagingLoadConfig, PagingLoadResult<NotificationMessage>> loader) {
        grid.setLoader(loader);
        toolBar.bind(loader);
    }

    @Override
    public void setNorthWidget(IsWidget widget) {
        con.setNorthWidget(widget, northData);
    }

    @Override
    public FilterPagingLoadConfig getCurrentLoadConfig() {
        FilterPagingLoadConfig lastConfig = (FilterPagingLoadConfig)grid.getLoader().getLastLoadConfig();
        return lastConfig;
    }

    @Override
    public void mask() {
        mainPanel.mask(I18N.DISPLAY.loadingMask());

    }

    @Override
    public void unmask() {
        mainPanel.unmask();
    }

    @Override
    public TextButton getRefreshButton() {
        return toolBar.getRefreshButton();
    }


    @UiFactory
    ColumnModel<NotificationMessage> createColumnModel() {
        NotificationMessageProperties props = GWT.create(NotificationMessageProperties.class);
        List<ColumnConfig<NotificationMessage, ?>> configs = new LinkedList<>();

        checkBoxModel =
                new CheckBoxSelectionModel<>(new IdentityValueProvider<NotificationMessage>());
        @SuppressWarnings("rawtypes")
        ColumnConfig colCheckBox = checkBoxModel.getColumn();
        configs.add(colCheckBox);

        ColumnConfig<NotificationMessage, NotificationCategory> colCategory =
                new ColumnConfig<>(props.category(),
                                   appearance.categoryColumnWidth(),
                                   appearance.category());
        configs.add(colCategory);
        colCategory.setMenuDisabled(true);
        colCategory.setSortable(false);

        ColumnConfig<NotificationMessage, NotificationMessage> colMessage =
                new ColumnConfig<>(new IdentityValueProvider<NotificationMessage>(),
                                   appearance.messagesColumnWidth(),
                                   appearance.messagesGridHeader());
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
        }, appearance.createdDateColumnWidth(), appearance.createdDateGridHeader());
        colTimestamp.setCell(new DateCell(DateTimeFormat
                                                  .getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM)));

        configs.add(colTimestamp);
        return new ColumnModel<>(configs);
    }
}
