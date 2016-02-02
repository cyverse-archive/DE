package org.iplantc.de.diskResource.client.views.sharing;

import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.sharing.SharingPresenter;
import org.iplantc.de.diskResource.client.DataSharingView;

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

/**
 * @author jstroot
 */
public class DataSharingViewImpl implements DataSharingView {

    @UiTemplate("DataSharingView.ui.xml")
    interface MyUiBinder extends UiBinder<Widget, DataSharingViewImpl> {
    }
    @UiField(provided = true) final ColumnModel<DiskResource> diskResourcesColumnModel;
    @UiField(provided = true) final ListStore<DiskResource> diskResourcesListStore;
    final Widget widget;
    @UiField VerticalLayoutContainer container;
    @UiField FramedPanel diskResourceListPnl;
    @UiField Grid<DiskResource> diskResourcesGrid;

    SharingPresenter presenter;

    private static final MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    public DataSharingViewImpl(ColumnModel<DiskResource> diskReColumnModel,
                               ListStore<DiskResource> drStore) {
        this.diskResourcesColumnModel = diskReColumnModel;
        this.diskResourcesListStore = drStore;
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
    public void setPresenter(SharingPresenter dataSharingPresenter) {
        this.presenter = dataSharingPresenter;
    }

    @Override
    public void setSelectedDiskResources(List<DiskResource> models) {
        if (models != null && models.size() > 0) {
            diskResourcesListStore.clear();
            diskResourcesListStore.addAll(models);
        }

    }


}
