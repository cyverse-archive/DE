/**
 *
 */
package org.iplantc.de.notifications.client.views;

import org.iplantc.de.commons.client.widgets.DEPagingToolbar;
import org.iplantc.de.client.models.notifications.NotificationMessage;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoader;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.GridSelectionModel;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent.SelectionChangedHandler;

import java.util.List;

/**
 * 
 * Notification View as grid
 * 
 * @author sriram
 * 
 */
public class NotificationViewImpl implements NotificationView {

    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    @UiTemplate("NotificationView.ui.xml")
    interface MyUiBinder extends UiBinder<Widget, NotificationViewImpl> {
    }

    @UiField(provided = true)
    final ListStore<NotificationMessage> listStore;
    @UiField(provided = true)
    final ColumnModel<NotificationMessage> cm;

    @UiField
    Grid<NotificationMessage> grid;

    @UiField
    FramedPanel mainPanel;

    @UiField
    BorderLayoutContainer con;

    @UiField
    DEPagingToolbar toolBar;

    @UiField
    BorderLayoutData northData;

    private final Widget widget;
    private Presenter presenter;

    public NotificationViewImpl(ListStore<NotificationMessage> listStore,
            ColumnModel<NotificationMessage> cm, GridSelectionModel<NotificationMessage> sm) {
        this.cm = cm;
        this.listStore = listStore;
        this.widget = uiBinder.createAndBindUi(this);
        toolBar.getElement().getStyle().setProperty("borderBottom", "none");
        grid.setSelectionModel(sm);
        grid.getSelectionModel().setSelectionMode(SelectionMode.MULTI);
        addGridSelectionHandler();
    }

    private void addGridSelectionHandler() {
        grid.getSelectionModel().addSelectionChangedHandler(
                new SelectionChangedHandler<NotificationMessage>() {

                    @Override
                    public void onSelectionChanged(SelectionChangedEvent<NotificationMessage> event) {
                        presenter.onNotificationSelection(event.getSelection());
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
        return widget;
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

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.iplantc.de.client.gxt3.views.NotificationView#setPresenter(org.iplantc.de.client.gxt3.views
     * .NotificationView.Presenter)
     */
    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.iplantc.de.client.gxt3.views.NotificationView#getListStore()
     */
    @Override
    public ListStore<NotificationMessage> getListStore() {
        return listStore;
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
}
