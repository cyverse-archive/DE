package org.iplantc.de.admin.desktop.client.ontologies.views.dialogs;

import org.iplantc.de.admin.desktop.client.ontologies.OntologiesView;
import org.iplantc.de.admin.desktop.client.ontologies.model.OntologyHierarchyProperties;
import org.iplantc.de.client.models.ontologies.OntologyHierarchy;

import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.Style;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.grid.CheckBoxSelectionModel;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;

import java.util.List;

/**
 * @author aramsey
 */
public class DeleteHierarchiesViewImpl extends Composite implements DeleteHierarchiesView {

    interface DeleteHierarchiesViewImplUiBinder extends UiBinder<Widget, DeleteHierarchiesViewImpl> {
    }

    private static DeleteHierarchiesViewImplUiBinder uiBinder =
            GWT.create(DeleteHierarchiesViewImplUiBinder.class);

    @UiField Grid<OntologyHierarchy> grid;
    @UiField ListStore<OntologyHierarchy> listStore;
    @UiField(provided = true) OntologiesView.OntologiesViewAppearance appearance;
    private CheckBoxSelectionModel<OntologyHierarchy> checkBoxSelectionModel;
    private OntologyHierarchyProperties properties;

    @Inject
    public DeleteHierarchiesViewImpl(OntologiesView.OntologiesViewAppearance appearance,
                                     OntologyHierarchyProperties properties) {
        this.appearance = appearance;
        this.properties = properties;
        initWidget(uiBinder.createAndBindUi(this));
        grid.setSelectionModel(checkBoxSelectionModel);
        grid.getSelectionModel().setSelectionMode(Style.SelectionMode.MULTI);
    }

    @UiFactory
    ColumnModel<OntologyHierarchy> createColumnModel() {
        checkBoxSelectionModel = new CheckBoxSelectionModel<>(new IdentityValueProvider<OntologyHierarchy>());
        ColumnConfig<OntologyHierarchy, OntologyHierarchy> colCheckBox = checkBoxSelectionModel.getColumn();

        ColumnConfig<OntologyHierarchy, String> iri =
                new ColumnConfig<>(properties.iri(), 200, "Root");

        ColumnConfig<OntologyHierarchy, String> label = new ColumnConfig<>(properties.label(), 100, "Name");
        List<ColumnConfig<OntologyHierarchy, ?>> columns = Lists.newArrayList();
        columns.add(colCheckBox);
        columns.add(label);
        columns.add(iri);
        return new ColumnModel<>(columns);
    }

    public void addHierarchyRoots(List<OntologyHierarchy> roots) {
        if (roots != null && roots.size() > 0) {
            listStore.addAll(roots);
        }
    }

    @UiFactory
    ListStore<OntologyHierarchy> createListStore() {
        return new ListStore<>(new ModelKeyProvider<OntologyHierarchy>() {
            @Override
            public String getKey(OntologyHierarchy item) {
                return item.getIri();
            }
        });
    }

    public List<OntologyHierarchy> getDeletedHierarchies() {
        return checkBoxSelectionModel.getSelectedItems();
    }

}
