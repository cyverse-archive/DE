package org.iplantc.de.analysis.client.views;

import org.iplantc.de.analysis.client.AnalysesView;
import org.iplantc.de.analysis.client.AnalysesView.Appearance;
import org.iplantc.de.client.models.analysis.AnalysisStep;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.CellClickEvent;
import com.sencha.gxt.widget.core.client.event.CellClickEvent.CellClickHandler;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.GridViewConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class AnalysisStepsView extends Composite {

    private static AnalysisStepsViewUiBinder uiBinder = GWT.create(AnalysisStepsViewUiBinder.class);

    interface AnalysisStepsViewUiBinder extends UiBinder<Widget, AnalysisStepsView> {
    }


    @UiField
    VerticalLayoutContainer con;

    @UiField
    ColumnModel<AnalysisStep> cm;
    @UiField
    ListStore<AnalysisStep> listStore;
    @UiField
    Grid<AnalysisStep> grid;

    Logger LOG = Logger.getLogger(AnalysisStepsView.class.getSimpleName());

    private final Appearance appearance;

    @Inject
    public AnalysisStepsView(AnalysesView.Appearance appearance) {
        this.appearance = appearance;
        initWidget(uiBinder.createAndBindUi(this));
        grid.addCellClickHandler(new CellClickHandler() {
            
            @Override
            public void onCellClick(CellClickEvent event) {
                AnalysisStep as = listStore.get(event.getRowIndex());
                Window d = new Window();
                d.setSize("300px", "100px");
                d.setHeadingText(AnalysisStepsView.this.appearance.jobId());
                TextField tf = new TextField();
                tf.setValue(as.getId());
                d.add(tf);
                d.show();
                tf.selectAll();
            }
        });
        grid.getView().setViewConfig(new GridViewConfig<AnalysisStep>() {

            @Override
            public String getRowStyle(AnalysisStep model, int rowIndex) {
                return AnalysisStepsView.this.appearance.css().row();
            }

            @Override
            public String getColStyle(AnalysisStep model,
                                      ValueProvider<? super AnalysisStep, ?> valueProvider,
                                      int rowIndex,
                                      int colIndex) {
                return null;
            }
        });
    }

    @UiFactory
    ListStore<AnalysisStep> buildListStore() {
        ListStore<AnalysisStep> store = new ListStore<>(new ModelKeyProvider<AnalysisStep>() {

            @Override
            public String getKey(AnalysisStep item) {
                return item.getStepNumber() + "";
            }
        });

        return store;
    }

    @UiFactory
    ColumnModel<AnalysisStep> buildColumnModel() {
        ColumnConfig<AnalysisStep, String> stepType = new ColumnConfig<AnalysisStep, String>(new ValueProvider<AnalysisStep, String>() {

                                                                             @Override
                                                                             public String
                                                                                     getValue(AnalysisStep object) {
                                                                                 return object.getStepType();
                                                                             }

                                                                             @Override
                                                                             public void
                                                                                     setValue(AnalysisStep object,
                                                                                              String value) {
                                                                                 object.setStepType(value);
                                                                             }

                                                                             @Override
                                                                             public String getPath() {
                                                                                 return "step_type";
                                                                             }
                                                                         },
                                                                         200,
                                                                         appearance.stepType());

        ColumnConfig<AnalysisStep, String> externalId = new ColumnConfig<AnalysisStep, String>(new ValueProvider<AnalysisStep, String>() {

                                                                               @Override
                                                                               public String
                                                                                       getValue(AnalysisStep object) {
                                                                                   return object.getId();
                                                                               }

                                                                               @Override
                                                                               public void
                                                                                       setValue(AnalysisStep object,
                                                                                                String value) {
                                                                                   object.setId(value);

                                                                               }

                                                                               @Override
                                                                               public String getPath() {
                                                                                   return "external_id";
                                                                               }
                                                                           },
                                                                           275,
                                                                           appearance.jobId());
        ArrayList<ColumnConfig<AnalysisStep, ?>> list = new ArrayList<>();
        list.add(externalId);
        list.add(stepType);
        return new ColumnModel<AnalysisStep>(list);
    }

    public void setData(List<AnalysisStep> steps) {
        listStore.addAll(steps);
    }

    public void clearData() {
        listStore.clear();
    }

}
