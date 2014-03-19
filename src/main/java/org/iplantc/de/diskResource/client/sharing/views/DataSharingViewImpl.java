package org.iplantc.de.diskResource.client.sharing.views;

import org.iplantc.de.client.models.diskResources.DiskResource;

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

public class DataSharingViewImpl implements DataSharingView {

    @UiField
    VerticalLayoutContainer container;

     @UiField
    FramedPanel diskResourceListPnl;

 
    @UiField(provided = true)
    ColumnModel<DiskResource> diskResourcesColumnModel;

    @UiField(provided = true)
    ListStore<DiskResource> diskResourcesListStore;

 
    @UiField
    Grid<DiskResource> diskResourcesGrid;

 
    Presenter presenter;

    final Widget widget;

    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    @UiTemplate("DataSharingView.ui.xml")
    interface MyUiBinder extends UiBinder<Widget, DataSharingViewImpl> {
    }

    public DataSharingViewImpl(ColumnModel<DiskResource> diskReColumnModel,
            ListStore<DiskResource> drStore) {
        this.diskResourcesColumnModel = diskReColumnModel;
        this.diskResourcesListStore = drStore;
        widget = uiBinder.createAndBindUi(this);
    }

 
    @Override
    public Widget asWidget() {
        return widget;
    }

  
    @Override
    public void setPresenter(Presenter dataSharingPresenter) {
        this.presenter = dataSharingPresenter;
    }

   

    @Override
    public void setSelectedDiskResources(List<DiskResource> models) {
        if (models != null && models.size() > 0) {
            diskResourcesListStore.clear();
            diskResourcesListStore.addAll(models);
        }

    }


	@Override
	public void addShareWidget(Widget widget) {
        container.add(widget);
	}


}
