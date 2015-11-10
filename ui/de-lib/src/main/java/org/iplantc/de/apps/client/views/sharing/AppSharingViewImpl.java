/**
 * 
 * @author sriram
 */
package org.iplantc.de.apps.client.views.sharing;

import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.sharing.SharingPresenter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;

import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;

import java.util.List;

public class AppSharingViewImpl implements AppSharingView {

    private static AppSharingViewUiBinder uiBinder = GWT.create(AppSharingViewUiBinder.class);

    @UiTemplate("AppSharingView.ui.xml")
    interface AppSharingViewUiBinder extends UiBinder<Widget, AppSharingViewImpl> {
    }

    @UiField(provided = true)
    final ColumnModel<App> appColumnModel;
    @UiField(provided = true)
    final ListStore<App> appListStore;
    final Widget widget;
    @UiField
    VerticalLayoutContainer container;
    @UiField
    FramedPanel appListPnl;
    @UiField
    Grid<App> appGrid;

    SharingPresenter presenter;

    public AppSharingViewImpl(ColumnModel<App> appColumnModel, ListStore<App> appListStore) {
        this.appColumnModel = appColumnModel;
        this.appListStore = appListStore;
        widget = uiBinder.createAndBindUi(this);
    }
    
    @Override
    public void addShareWidget(Widget widget) {
        container.add(widget);
    }

    @Override
    public Widget asWidget() {
        return widget;
    }

    @Override
    public void setPresenter(SharingPresenter appSharingPresenter) {
        this.presenter = appSharingPresenter;
    }

    @Override
    public void setSelectedApps(List<App> models) {
        if (models != null && models.size() > 0) {
            appListStore.clear();
            appListStore.addAll(models);
        }

    }

}
