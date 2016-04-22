package org.iplantc.de.admin.desktop.client.ontologies.views;

import org.iplantc.de.admin.desktop.client.ontologies.OntologiesView;
import org.iplantc.de.admin.desktop.client.ontologies.model.OntologyProperties;
import org.iplantc.de.client.models.IsHideable;
import org.iplantc.de.client.models.ontologies.Ontology;

import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;

import com.sencha.gxt.core.client.Style;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;

import java.util.List;

/**
 * @author aramsey
 */
public class OntologyListView extends Composite implements IsHideable {

    interface OntologyListViewUiBinder extends UiBinder<Widget, OntologyListView> {
    }

    private static OntologyListViewUiBinder uiBinder = GWT.create(OntologyListViewUiBinder.class);

    private static final String ONTOLOGY_MODEL_KEY = "model_key";
    private int uniqueVolumeId;

    @UiField(provided = true) OntologiesView.OntologiesViewAppearance appearance;
    @UiField Grid<Ontology> grid;
    @UiField ListStore<Ontology> listStore;

    private OntologyProperties props;

    @Inject
    public OntologyListView(OntologiesView.OntologiesViewAppearance appearance,
                            OntologyProperties props) {
        this.appearance = appearance;
        this.props = props;
        initWidget(uiBinder.createAndBindUi(this));

        grid.getSelectionModel().setSelectionMode(Style.SelectionMode.SINGLE);
    }


    public void addOntologies(List<Ontology> ontologies) {
        listStore.addAll(ontologies);
    }

    @UiFactory
    ListStore<Ontology> createListStore() {
        return new ListStore<>(new ModelKeyProvider<Ontology>() {
            @Override
            public String getKey(Ontology item) {
                return getOntologyTag(item);
            }
        });
    }

    private String getOntologyTag(Ontology item) {
         if (item != null) {
             final AutoBean<Ontology> ontologyAutoBean = AutoBeanUtils.getAutoBean(item);
             String currentTag = ontologyAutoBean.getTag(ONTOLOGY_MODEL_KEY);
             if (currentTag == null) {
                 ontologyAutoBean.setTag(ONTOLOGY_MODEL_KEY, String.valueOf(uniqueVolumeId++));
             }
             return ontologyAutoBean.getTag(ONTOLOGY_MODEL_KEY);
         }
        return "";
    }

    @UiFactory
    ColumnModel<Ontology> createColumnModel() {
        List<ColumnConfig<Ontology, ?>> list = Lists.newArrayList();
        ColumnConfig<Ontology, String> iriCol = new ColumnConfig<>(props.iri(),
                                                                   appearance.iriColumnWidth(),
                                                                   appearance.iriColumnLabel());
        ColumnConfig<Ontology, String> versionCol = new ColumnConfig<>(props.version(),
                                                                       appearance.versionColumnWidth(),
                                                                       appearance.versionColumnLabel());
        ColumnConfig<Ontology, String> createdByCol = new ColumnConfig<>(props.createdBy(),
                                                                         appearance.createdByColumnWidth(),
                                                                         appearance.createdByColumnLabel());
        ColumnConfig<Ontology, String> createdOnCol = new ColumnConfig<>(props.createdOn(),
                                                                         appearance.createdOnColumnWidth(),
                                                                         appearance.createdOnColumnLabel());
        list.add(iriCol);
        list.add(versionCol);
        list.add(createdByCol);
        list.add(createdOnCol);
        return new ColumnModel<>(list);
    }

}
