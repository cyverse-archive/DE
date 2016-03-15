package org.iplantc.de.analysis.client.views.sharing;

import org.iplantc.de.client.models.analysis.Analysis;
import org.iplantc.de.client.sharing.SharingPresenter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;

import java.util.ArrayList;
import java.util.List;

public class AnalysisSharingViewImpl implements AnalysisSharingView {

    private static AnalysisSharingViewUiBinder uiBinder = GWT.create(AnalysisSharingViewUiBinder.class);

    @UiTemplate("AnalysisSharingView.ui.xml")
    interface AnalysisSharingViewUiBinder extends UiBinder<Widget, AnalysisSharingViewImpl> {
    }

    @UiField
    ColumnModel<Analysis> analysisColumnModel;

    @UiField
    ListStore<Analysis> analysisListStore;

    @UiField
    VerticalLayoutContainer container;

    @UiField
    FramedPanel analysisListPnl;

    @UiField
    Grid<Analysis> grid;

    SharingPresenter presenter;

    final Widget widget;

    public AnalysisSharingViewImpl() {
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

    @UiFactory
    ColumnModel<Analysis> buildAnalysisColumnModel() {
        List<ColumnConfig<Analysis, ?>> list = new ArrayList<>();

        ColumnConfig<Analysis, String> name = new ColumnConfig<>(new ValueProvider<Analysis, String>() {

            @Override
            public String getValue(Analysis object) {
                return object.getName();
            }

            @Override
            public void setValue(Analysis object, String value) {
                // TODO Auto-generated method stub
            }

            @Override
            public String getPath() {
                return "name";
            }
        }, 180, "Name");
        list.add(name);
        return new ColumnModel<>(list);
    }

    @UiFactory
    ListStore<Analysis> buildAnalyisStore() {
        ListStore<Analysis> analysisStore = new ListStore<>(new ModelKeyProvider<Analysis>() {

            @Override
            public String getKey(Analysis item) {
                return item.getId();
            }
        });

        return analysisStore;
    }

}
