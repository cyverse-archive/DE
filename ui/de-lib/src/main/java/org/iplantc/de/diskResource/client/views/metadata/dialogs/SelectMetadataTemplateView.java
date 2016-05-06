package org.iplantc.de.diskResource.client.views.metadata.dialogs;

import org.iplantc.de.client.models.diskResources.MetadataTemplateInfo;
import org.iplantc.de.commons.client.views.dialogs.IPlantDialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.sencha.gxt.core.client.Style;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;

import java.util.Arrays;
import java.util.List;

/**
 * Created by sriram on 5/4/16.
 */
public class SelectMetadataTemplateView extends IPlantDialog implements IsWidget {


    @UiTemplate("SelectMetadataTemplateView.ui.xml")
    interface SelectMetadataTemplateViewUiBinder extends UiBinder<Widget, SelectMetadataTemplateView> {
    }

    private static final SelectMetadataTemplateViewUiBinder uiBinder =
            GWT.create(SelectMetadataTemplateViewUiBinder.class);

    @UiField
    VerticalLayoutContainer container;

    @UiField
    Grid<MetadataTemplateInfo> grid;

    @UiField(provided = true)
    ListStore<MetadataTemplateInfo> listStore;

    @UiField(provided = true)
    ColumnModel<MetadataTemplateInfo> cm;

    public SelectMetadataTemplateView(List<MetadataTemplateInfo> templates) {
        super();
        getOkButton().disable();
        listStore = new ListStore<>(new ModelKeyProvider<MetadataTemplateInfo>() {
            @Override
            public String getKey(MetadataTemplateInfo item) {
                return item.getId();
            }
        });
        cm = buildColumnModel();
        uiBinder.createAndBindUi(this);
        this.setWidget(asWidget());
        grid.getSelectionModel().setSelectionMode(Style.SelectionMode.SINGLE);
        grid.getSelectionModel()
            .addSelectionChangedHandler(new SelectionChangedEvent.SelectionChangedHandler<MetadataTemplateInfo>() {
                @Override
                public void onSelectionChanged(SelectionChangedEvent<MetadataTemplateInfo> event) {
                    if (event.getSelection().size() == 0) {
                        getOkButton().disable();
                    } else {
                        getOkButton().enable();
                    }
                }
            });
        listStore.clear();
        listStore.addAll(templates);
    }

    @Override
    public Widget asWidget() {
        return container;
    }


    ColumnModel<MetadataTemplateInfo> buildColumnModel() {
        ColumnConfig<MetadataTemplateInfo, String> name =
                new ColumnConfig<MetadataTemplateInfo, String>(new ValueProvider<MetadataTemplateInfo, String>() {
                    @Override
                    public String getValue(MetadataTemplateInfo object) {
                        return object.getName();
                    }

                    @Override
                    public void setValue(MetadataTemplateInfo object, String value) {
                        // left unimplemented
                    }

                    @Override
                    public String getPath() {
                        return null;
                    }
                }, 150, "Templates");

        return new ColumnModel<MetadataTemplateInfo>(Arrays.<ColumnConfig<MetadataTemplateInfo, ?>>asList(
                name));
    }


    public MetadataTemplateInfo getSelectedTemplate() {
        return grid.getSelectionModel().getSelectedItem();
    }

}
