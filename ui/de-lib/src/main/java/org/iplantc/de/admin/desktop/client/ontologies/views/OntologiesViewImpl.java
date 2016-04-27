package org.iplantc.de.admin.desktop.client.ontologies.views;

import org.iplantc.de.admin.apps.client.AdminAppsGridView;
import org.iplantc.de.admin.desktop.client.ontologies.OntologiesView;
import org.iplantc.de.admin.desktop.client.ontologies.events.HierarchySelectedEvent;
import org.iplantc.de.admin.desktop.client.ontologies.events.PublishOntologyClickEvent;
import org.iplantc.de.admin.desktop.client.ontologies.events.SelectOntologyVersionEvent;
import org.iplantc.de.admin.desktop.client.ontologies.events.ViewOntologyVersionEvent;
import org.iplantc.de.admin.desktop.client.ontologies.views.dialogs.EdamUploadDialog;
import org.iplantc.de.admin.desktop.client.ontologies.views.dialogs.OntologyListDialog;
import org.iplantc.de.admin.desktop.client.ontologies.views.dialogs.PublishOntologyDialog;
import org.iplantc.de.apps.client.AppCategoriesView;
import org.iplantc.de.client.DEClientConstants;
import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.models.diskResources.DiskResourceAutoBeanFactory;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.models.ontologies.Ontology;
import org.iplantc.de.client.models.ontologies.OntologyHierarchy;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.inject.client.AsyncProvider;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import com.sencha.gxt.core.client.Style;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.CardLayoutContainer;
import com.sencha.gxt.widget.core.client.container.CenterLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.form.SimpleComboBox;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;
import com.sencha.gxt.widget.core.client.tree.Tree;

import java.util.List;

/**
 * @author aramsey
 */
public class OntologiesViewImpl extends Composite implements OntologiesView {

    interface OntologiesViewImplUiBinder extends UiBinder<Widget, OntologiesViewImpl> {

    }

    private static OntologiesViewImplUiBinder uiBinder = GWT.create(OntologiesViewImplUiBinder.class);

    @UiField TextButton addButton;
    @UiField SimpleComboBox<Ontology> ontologyDropDown;
    @UiField TextButton viewVersions;
    @UiField(provided = true) OntologiesViewAppearance appearance;
    @UiField Tree<OntologyHierarchy, String> tree;
    @UiField(provided = true) AppCategoriesView categoriesView;
    @UiField(provided = true) AdminAppsGridView gridView;
    @UiField CardLayoutContainer cards;
    @UiField CenterLayoutContainer noTreePanel, emptyTreePanel;
    @UiField ContentPanel treePanel;
    @UiField TextButton publishButton;
    @Inject EventBus eventBus;
    @Inject DEClientConstants clientConstants;

    @Inject AsyncProvider<OntologyListDialog> listDialog;
    private TreeStore<OntologyHierarchy> treeStore;
    private Ontology activeOntology;

    @Inject
    public OntologiesViewImpl(OntologiesViewAppearance appearance,
                              @Assisted TreeStore<OntologyHierarchy> treeStore,
                              @Assisted AppCategoriesView categoriesView,
                              @Assisted AdminAppsGridView gridView) {
        this.appearance = appearance;
        this.treeStore = treeStore;
        this.categoriesView = categoriesView;
        this.gridView = gridView;

        initWidget(uiBinder.createAndBindUi(this));
        viewVersionsClicked(new SelectEvent());

    }

    @Override
    public HandlerRegistration addViewOntologyVersionEventHandler(ViewOntologyVersionEvent.ViewOntologyVersionEventHandler handler) {
        return addHandler(handler, ViewOntologyVersionEvent.TYPE);
    }

    @Override
    public HandlerRegistration addSelectOntologyVersionEventHandler(SelectOntologyVersionEvent.SelectOntologyVersionEventHandler handler) {
        return addHandler(handler, SelectOntologyVersionEvent.TYPE);
    }

    @Override
    public HandlerRegistration addHierarchySelectedEventHandler(HierarchySelectedEvent.HierarchySelectedEventHandler handler) {
        return addHandler(handler, HierarchySelectedEvent.TYPE);
    }

    @Override
    public HandlerRegistration addPublishOntologyClickEventHandler(PublishOntologyClickEvent.PublishOntologyClickEventHandler handler) {
        return addHandler(handler, PublishOntologyClickEvent.TYPE);
    }

    @Override
    public void showOntologyVersions(final List<Ontology> ontologies) {
        ontologyDropDown.clear();
        ontologyDropDown.getStore().replaceAll(ontologies);
    }

    @Override
    public void showEmptyTreePanel() {
        cards.setActiveWidget(emptyTreePanel);
    }

    @Override
    public void showTreePanel() {
        cards.setActiveWidget(treePanel);
    }


    @UiFactory
    SimpleComboBox<Ontology> createComboBox() {
        final SimpleComboBox<Ontology> ontologySimpleComboBox = new SimpleComboBox<Ontology>(new LabelProvider<Ontology>() {
            @Override
            public String getLabel(Ontology item) {
                if (item.isActive()){
                    activeOntology = item;
                    return item.getVersion() + "  <--- *(DE)*";
                }
                return item.getVersion();
            }
        });
        ontologySimpleComboBox.setEditable(false);
        ontologySimpleComboBox.setWidth(500);
        ontologySimpleComboBox.addSelectionHandler(new SelectionHandler<Ontology>() {
            @Override
            public void onSelection(SelectionEvent<Ontology> event) {
                fireEvent(new SelectOntologyVersionEvent(event.getSelectedItem()));
            }
        });
        return ontologySimpleComboBox;
    }
    @UiHandler("viewVersions")
    void viewVersionsClicked(SelectEvent event) {
        fireEvent(new ViewOntologyVersionEvent());
    }

    @UiHandler("publishButton")
    void publishButtonClicked(SelectEvent event) {
        Ontology editedOntology = ontologyDropDown.getCurrentValue();
        if (editedOntology != null){
            new PublishOntologyDialog(appearance, editedOntology, activeOntology, this);
        }

    }

    @UiHandler("addButton")
    void addButtonClicked(SelectEvent event) {
        DiskResourceAutoBeanFactory drFactory = GWT.create(DiskResourceAutoBeanFactory.class);
        Folder blank = drFactory.folder().as();
        blank.setPath("Ontology Database");

        new EdamUploadDialog(eventBus,
                             UriUtils.fromTrustedString(clientConstants.ontologyUploadServlet()),
                             appearance).show();
    }

    @UiFactory
    Tree<OntologyHierarchy, String> createTree() {
        Tree<OntologyHierarchy, String> ontologyTree = new Tree<OntologyHierarchy, String>(treeStore, new ValueProvider<OntologyHierarchy, String>() {
            @Override
            public String getValue(OntologyHierarchy object) {
                return object.getLabel();
            }

            @Override
            public void setValue(OntologyHierarchy object, String value) {

            }

            @Override
            public String getPath() {
                return null;
            }
        });

        ontologyTree.getSelectionModel().setSelectionMode(Style.SelectionMode.SINGLE);
        ontologyTree.getSelectionModel().addSelectionChangedHandler(new SelectionChangedEvent.SelectionChangedHandler<OntologyHierarchy>() {
            @Override
            public void onSelectionChanged(SelectionChangedEvent<OntologyHierarchy> event) {
                if (event.getSelection().size() == 1) {
                    fireEvent(new HierarchySelectedEvent(event.getSelection().get(0)));
                }
            }
        });

        return ontologyTree;
    }

}
