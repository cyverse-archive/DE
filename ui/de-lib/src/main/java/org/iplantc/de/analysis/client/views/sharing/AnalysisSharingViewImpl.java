package org.iplantc.de.analysis.client.views.sharing;

import org.iplantc.de.client.models.analysis.Analysis;
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

public class AnalysisSharingViewImpl implements AnalysisSharingView {

    private static AnalysisSharingViewUiBinder uiBinder = GWT.create(AnalysisSharingViewUiBinder.class);

    @UiTemplate("AnalysisSharingView.ui.xml")
    interface AnalysisSharingViewUiBinder extends UiBinder<Widget, AnalysisSharingViewImpl> {
    }

    @UiField(provided = true)
    final ColumnModel<Analysis> analysisColumnModel;
    @UiField(provided = true)
    final ListStore<Analysis> analysisListStore;
    final Widget widget;
    @UiField
    VerticalLayoutContainer container;
    @UiField
    FramedPanel analysisListPnl;
    @UiField
    Grid<Analysis> grid;

    SharingPresenter presenter;

    public AnalysisSharingViewImpl(ColumnModel<Analysis> columnModel, ListStore<Analysis> listStore) {
        this.analysisColumnModel = columnModel;
        this.analysisListStore = listStore;
        widget = uiBinder.createAndBindUi(this);
    }

    @Override
    public Widget asWidget() {
        return widget;
    }

    @Override
    public void addShareWidget(Widget widget) {
        container.add(widget);

    }

    @Override
    public void setPresenter(SharingPresenter sharingPresenter) {
        this.presenter = sharingPresenter;

    }

    @Override
    public void setSelectedAnalysis(List<Analysis> models) {
        if (models != null && models.size() > 0) {
            analysisListStore.clear();
            analysisListStore.addAll(models);
        }

    }

}
