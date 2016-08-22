package org.iplantc.de.admin.desktop.client.ontologies.views;

import org.iplantc.de.admin.apps.client.AdminAppsGridView;
import org.iplantc.de.admin.desktop.client.ontologies.OntologiesView;
import org.iplantc.de.admin.desktop.client.ontologies.events.CategorizeButtonClickedEvent;
import org.iplantc.de.admin.desktop.client.ontologies.events.DeleteHierarchyEvent;
import org.iplantc.de.admin.desktop.client.ontologies.events.DeleteOntologyButtonClickedEvent;
import org.iplantc.de.admin.desktop.client.ontologies.events.HierarchySelectedEvent;
import org.iplantc.de.admin.desktop.client.ontologies.events.PreviewHierarchySelectedEvent;
import org.iplantc.de.admin.desktop.client.ontologies.events.PublishOntologyClickEvent;
import org.iplantc.de.admin.desktop.client.ontologies.events.RefreshOntologiesEvent;
import org.iplantc.de.admin.desktop.client.ontologies.events.RefreshPreviewButtonClicked;
import org.iplantc.de.admin.desktop.client.ontologies.events.RestoreAppButtonClicked;
import org.iplantc.de.admin.desktop.client.ontologies.events.SaveOntologyHierarchyEvent;
import org.iplantc.de.admin.desktop.client.ontologies.events.SelectOntologyVersionEvent;
import org.iplantc.de.admin.desktop.client.ontologies.views.dialogs.PublishOntologyDialog;
import org.iplantc.de.admin.desktop.client.ontologies.views.dialogs.SaveHierarchiesDialog;
import org.iplantc.de.apps.client.events.AppSearchResultLoadEvent;
import org.iplantc.de.apps.client.events.BeforeAppSearchEvent;
import org.iplantc.de.apps.client.events.selection.AppSelectionChangedEvent;
import org.iplantc.de.apps.client.events.selection.DeleteAppsSelected;
import org.iplantc.de.apps.client.views.toolBar.AppSearchField;
import org.iplantc.de.client.DEClientConstants;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.ontologies.Ontology;
import org.iplantc.de.client.models.ontologies.OntologyHierarchy;
import org.iplantc.de.client.util.OntologyUtil;
import org.iplantc.de.commons.client.views.dialogs.EdamUploadDialog;

import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import com.sencha.gxt.cell.core.client.form.ComboBoxCell;
import com.sencha.gxt.core.client.Style;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.Store;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoader;
import com.sencha.gxt.dnd.core.client.DragSource;
import com.sencha.gxt.dnd.core.client.DropTarget;
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.CardLayoutContainer;
import com.sencha.gxt.widget.core.client.container.CenterLayoutContainer;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent;
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
    @UiField TextButton deleteButton;
    @UiField SimpleComboBox<Ontology> ontologyDropDown;
    @UiField TextButton saveHierarchy;
    @UiField TextButton deleteHierarchy;
    @UiField TextButton categorize;
    @UiField TextButton deleteApp;
    @UiField AppSearchField appSearchField;
    @UiField TextButton refreshPreview;
    @UiField TextButton restoreApp;
    @UiField(provided = true) OntologiesViewAppearance appearance;
    @UiField(provided = true) Tree<OntologyHierarchy, String> editorTree;
    @UiField(provided = true) Tree<OntologyHierarchy, String> previewTree;
    @UiField(provided = true) AdminAppsGridView previewGridView;
    @UiField(provided = true) AdminAppsGridView editorGridView;
    @UiField CardLayoutContainer cards;
    @UiField CenterLayoutContainer noTreePanel, emptyTreePanel;
    @UiField ContentPanel editorTreePanel, previewTreePanel;
    @UiField TextButton publishButton;
    @Inject DEClientConstants clientConstants;

    @UiField(provided = true) TreeStore<OntologyHierarchy> editorTreeStore;
    @UiField(provided = true) TreeStore<OntologyHierarchy> previewTreeStore;
    private Ontology activeOntology;
    private Ontology selectedOntology;
    private App targetApp;
    private OntologyHierarchyToAppDND hierarchyToAppDND;
    private AppToOntologyHierarchyDND appToHierarchyDND;
    private OntologyUtil ontologyUtil = OntologyUtil.getInstance();
    private PagingLoader<FilterPagingLoadConfig, PagingLoadResult<App>> loader;

    @Inject
    public OntologiesViewImpl(OntologiesViewAppearance appearance,
                              @Assisted("editorTreeStore") TreeStore<OntologyHierarchy> editorTreeStore,
                              @Assisted("previewTreeStore") TreeStore<OntologyHierarchy> previewTreeStore,
                              @Assisted PagingLoader<FilterPagingLoadConfig, PagingLoadResult<App>> loader,
                              @Assisted("previewGridView") AdminAppsGridView previewGridView,
                              @Assisted("editorGridView") AdminAppsGridView editorGridView,
                              @Assisted OntologyHierarchyToAppDND hierarchyToAppDND,
                              @Assisted AppToOntologyHierarchyDND appToHierarchyDND) {
        this.appearance = appearance;
        this.loader = loader;
        this.editorTreeStore = editorTreeStore;
        this.previewTreeStore = previewTreeStore;
        this.previewGridView = previewGridView;
        this.editorGridView = editorGridView;
        this.hierarchyToAppDND = hierarchyToAppDND;
        this.appToHierarchyDND = appToHierarchyDND;

        addTreeSelectionHandlers();

        initWidget(uiBinder.createAndBindUi(this));

        updateButtonStatus();

        setUpDND();
    }

    void addTreeSelectionHandlers() {
        editorTree = createTree(editorTreeStore);
        editorTree.getSelectionModel().addSelectionChangedHandler(new SelectionChangedEvent.SelectionChangedHandler<OntologyHierarchy>() {
            @Override
            public void onSelectionChanged(SelectionChangedEvent<OntologyHierarchy> event) {
                if (event.getSelection().size() == 1) {
                    fireEvent(new HierarchySelectedEvent(event.getSelection().get(0), ontologyDropDown.getCurrentValue(), ViewType.EDITOR));
                }
                updateButtonStatus();
            }
        });
        previewTree = createTree(previewTreeStore);
        previewTree.getSelectionModel().addSelectionChangedHandler(new SelectionChangedEvent.SelectionChangedHandler<OntologyHierarchy>() {
            @Override
            public void onSelectionChanged(SelectionChangedEvent<OntologyHierarchy> event) {
                if (event.getSelection().size() == 1) {
                    fireEvent(new PreviewHierarchySelectedEvent(event.getSelection().get(0), ontologyDropDown.getCurrentValue(), ViewType.PREVIEW));
                }
                updateButtonStatus();
            }
        });
    }

    @Override
    public HandlerRegistration addRefreshOntologiesEventHandler(RefreshOntologiesEvent.RefreshOntologiesEventHandler handler) {
        return addHandler(handler, RefreshOntologiesEvent.TYPE);
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
    public HandlerRegistration addSaveOntologyHierarchyEventHandler(SaveOntologyHierarchyEvent.SaveOntologyHierarchyEventHandler handler) {
        return addHandler(handler, SaveOntologyHierarchyEvent.TYPE);
    }

    @Override
    public HandlerRegistration addPublishOntologyClickEventHandler(PublishOntologyClickEvent.PublishOntologyClickEventHandler handler) {
        return addHandler(handler, PublishOntologyClickEvent.TYPE);
    }

    @Override
    public HandlerRegistration addCategorizeButtonClickedEventHandler(CategorizeButtonClickedEvent.CategorizeButtonClickedEventHandler handler) {
        return addHandler(handler, CategorizeButtonClickedEvent.TYPE);
    }

    @Override
    public HandlerRegistration addDeleteOntologyButtonClickedEventHandler(
            DeleteOntologyButtonClickedEvent.DeleteOntologyButtonClickedEventHandler handler) {
        return addHandler(handler, DeleteOntologyButtonClickedEvent.TYPE);
    }

    @Override
    public HandlerRegistration addDeleteHierarchyEventHandler(DeleteHierarchyEvent.DeleteHierarchyEventHandler handler) {
        return addHandler(handler, DeleteHierarchyEvent.TYPE);
    }

    @Override
    public HandlerRegistration addDeleteAppsSelectedHandler(DeleteAppsSelected.DeleteAppsSelectedHandler handler) {
        return addHandler(handler, DeleteAppsSelected.TYPE);
    }

    @Override
    public HandlerRegistration addAppSearchResultLoadEventHandler(AppSearchResultLoadEvent.AppSearchResultLoadEventHandler handler) {
        return addHandler(handler, AppSearchResultLoadEvent.TYPE);
    }

    @Override
    public HandlerRegistration addBeforeAppSearchEventHandler(BeforeAppSearchEvent.BeforeAppSearchEventHandler handler) {
        return addHandler(handler, BeforeAppSearchEvent.TYPE);
    }

    public HandlerRegistration addPreviewHierarchySelectedEventHandler(PreviewHierarchySelectedEvent.PreviewHierarchySelectedEventHandler handler) {
        return addHandler(handler, PreviewHierarchySelectedEvent.TYPE);
    }

    @Override
    public HandlerRegistration addRefreshPreviewButtonClickedHandler(RefreshPreviewButtonClicked.RefreshPreviewButtonClickedHandler handler) {
        return addHandler(handler, RefreshPreviewButtonClicked.TYPE);
    }

    public HandlerRegistration addRestoreAppButtonClickedHandlers(RestoreAppButtonClicked.RestoreAppButtonClickedHandler handler) {
        return addHandler(handler, RestoreAppButtonClicked.TYPE);
    }

    @Override
    public void onAppSelectionChanged(AppSelectionChangedEvent event) {
        List<App> appSelection = event.getAppSelection();
        targetApp = null;
        if (appSelection != null && appSelection.size() != 0) {
            if (event.getSource() == previewGridView) {
                editorGridView.deselectAll();
            } else {
                previewGridView.deselectAll();
            }
            targetApp = appSelection.get(0);
        }
        updateButtonStatus();
    }

    @Override
    public void showOntologyVersions(final List<Ontology> ontologies) {
        ontologyDropDown.clear();
        ontologyDropDown.getStore().replaceAll(ontologies);
        selectedOntology = null;
        cards.setActiveWidget(noTreePanel);
        updateButtonStatus();
    }

    @Override
    public void showEmptyTreePanel() {
        cards.setActiveWidget(emptyTreePanel);
    }

    @Override
    public void showTreePanel() {
        cards.setActiveWidget(editorTreePanel);
    }

    @Override
    public OntologyHierarchy getHierarchyFromElement(Element el) {
        Tree.TreeNode<OntologyHierarchy> node = editorTree.findNode(el);
        if (node != null) {
            return node.getModel();
        }
        return null;
    }

    @Override
    public OntologyHierarchy getSelectedHierarchy() {
        return editorTree.getSelectionModel().getSelectedItem();
    }

    @Override
    public Ontology getSelectedOntology() {
        return ontologyDropDown.getCurrentValue();
    }

    @Override
    public void clearTreeStore(ViewType type) {
        switch(type){
            case EDITOR:
                editorTreeStore.clear();
                break;
            case PREVIEW:
                previewTreeStore.clear();
                break;
            case ALL:
                editorTreeStore.clear();
                previewTreeStore.clear();
                break;
        }
    }

    @Override
    public void addToTreeStore(ViewType type, List<OntologyHierarchy> children) {
        switch(type) {
            case EDITOR:
                editorTreeStore.add(children);
                break;
            case PREVIEW:
                previewTreeStore.add(children);
                break;
            case ALL:
                editorTreeStore.add(children);
                previewTreeStore.add(children);
                break;
        }
    }

    @Override
    public void addToTreeStore(ViewType type, OntologyHierarchy parent, List<OntologyHierarchy> children) {
        switch(type) {
            case EDITOR:
                editorTreeStore.add(parent, children);
                break;
            case PREVIEW:
                previewTreeStore.add(parent, children);
                break;
            case ALL:
                editorTreeStore.add(parent, children);
                previewTreeStore.add(parent, children);
                break;
        }
    }

    @Override
    public void maskTree(ViewType type) {
        switch(type) {
            case EDITOR:
                editorTreePanel.mask(appearance.loadingMask());
                break;
            case PREVIEW:
                previewTreePanel.mask(appearance.loadingMask());
                break;
            case ALL:
                editorTreePanel.mask(appearance.loadingMask());
                previewTreePanel.mask(appearance.loadingMask());
                break;
        }
    }

    @Override
    public void unmaskTree(ViewType type) {
        switch(type) {
            case EDITOR:
                editorTreePanel.unmask();
                break;
            case PREVIEW:
                previewTreePanel.unmask();
                break;
            case ALL:
                editorTreePanel.unmask();
                previewTreePanel.unmask();
                break;
        }
    }

    @Override
    public void selectHierarchy(OntologyHierarchy hierarchy) {
        if (hierarchy != null) {
            editorTree.getSelectionModel().deselectAll();
            editorTree.getSelectionModel().select(hierarchy, true);
        }
    }

    @Override
    public void selectActiveOntology(Ontology ontology) {
        if (ontology != null) {
            ontologyDropDown.setValue(ontology);
            selectedOntology = ontology;
            fireEvent(new SelectOntologyVersionEvent(ontology));
            updateButtonStatus();
        }
    }

    @Override
    public void deselectHierarchies(ViewType type) {
        switch(type) {
            case EDITOR:
                editorTree.getSelectionModel().deselectAll();
                break;
            case PREVIEW:
                previewTree.getSelectionModel().deselectAll();
                break;
            case ALL:
                editorTree.getSelectionModel().deselectAll();
                previewTree.getSelectionModel().deselectAll();
                break;
        }
    }

    @Override
    public void reSortTree(ViewType type) {
        switch(type) {
            case EDITOR:
                editorTreeStore.applySort(false);
                break;
            case PREVIEW:
                previewTreeStore.applySort(false);
                break;
            case ALL:
                editorTreeStore.applySort(false);
                previewTreeStore.applySort(false);
                break;
        }
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
        ontologySimpleComboBox.setEmptyText(appearance.selectOntologyVersion());
        ontologySimpleComboBox.setTriggerAction(ComboBoxCell.TriggerAction.ALL);
        ontologySimpleComboBox.setWidth(500);
        ontologySimpleComboBox.addSelectionHandler(new SelectionHandler<Ontology>() {
            @Override
            public void onSelection(SelectionEvent<Ontology> event) {
                selectedOntology = event.getSelectedItem();
                updateButtonStatus();
                if (selectedOntology != null) {
                    fireEvent(new SelectOntologyVersionEvent(selectedOntology));
                }

            }
        });
        return ontologySimpleComboBox;
    }

    @UiFactory
    AppSearchField createAppSearchField() {
        return new AppSearchField(loader);
    }

    public void updateButtonStatus() {
        publishButton.setEnabled(selectedOntology != null && selectedOntology != activeOntology);
        saveHierarchy.setEnabled(selectedOntology != null);
        deleteButton.setEnabled(selectedOntology != null && selectedOntology != activeOntology);
        deleteHierarchy.setEnabled(selectedOntology != null && editorTree.getSelectionModel().getSelectedItem() != null);
        categorize.setEnabled(selectedOntology != null && targetApp != null && !targetApp.getAppType().equalsIgnoreCase(App.EXTERNAL_APP));
        deleteApp.setEnabled(selectedOntology != null && targetApp != null && !targetApp.getAppType().equalsIgnoreCase(App.EXTERNAL_APP));
        refreshPreview.setEnabled(selectedOntology != null && editorTreeStore.getRootItems() != null && editorTreeStore.getRootItems().size() > 0);
        restoreApp.setEnabled(targetApp != null && targetApp.isDeleted());
    }

    @Override
    public void maskGrid(ViewType type) {
        switch(type) {
            case EDITOR:
                editorGridView.mask(appearance.loadingMask());
                break;
            case PREVIEW:
                previewGridView.mask(appearance.loadingMask());
                break;
            case ALL:
                editorGridView.mask(appearance.loadingMask());
                previewGridView.mask(appearance.loadingMask());
                break;
        }
    }

    @Override
    public void unmaskGrid(ViewType type) {
        switch(type) {
            case EDITOR:
                editorGridView.unmask();
                break;
            case PREVIEW:
                previewGridView.unmask();
                break;
            case ALL:
                editorGridView.unmask();
                previewGridView.unmask();
                break;
        }
    }

    @Override
    public void removeApp(App selectedApp) {
        previewGridView.removeApp(selectedApp);
        editorGridView.removeApp(selectedApp);
    }

    @Override
    public void deselectAll() {
        previewGridView.deselectAll();
        editorGridView.deselectAll();
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
        new EdamUploadDialog(UriUtils.fromTrustedString(clientConstants.ontologyUploadServlet()), this).show();
    }

    @UiHandler("deleteButton")
    void deleteButtonClicked(SelectEvent event) {
        Ontology currentOntology = ontologyDropDown.getCurrentValue();
        ConfirmMessageBox msgBox = new ConfirmMessageBox(appearance.deleteOntology(), appearance.confirmDeleteOntology(currentOntology.getVersion()));
        msgBox.addDialogHideHandler(new DialogHideEvent.DialogHideHandler() {
            @Override
            public void onDialogHide(DialogHideEvent event) {
                if (event.getHideButton() == Dialog.PredefinedButton.YES) {
                    fireEvent(new DeleteOntologyButtonClickedEvent(selectedOntology.getVersion()));
                }
            }
        });
        msgBox.show();
    }

    @UiHandler("saveHierarchy")
    void saveHierarchyClicked(SelectEvent event) {
        new SaveHierarchiesDialog(appearance, ontologyDropDown.getCurrentValue(), this);
    }

    @UiHandler("deleteHierarchy")
    void deleteHierarchyClicked(SelectEvent event) {
        final OntologyHierarchy selectedItem = editorTree.getSelectionModel().getSelectedItem();
        if (editorTree != null && selectedItem != null) {
            ConfirmMessageBox cmb = new ConfirmMessageBox(appearance.deleteHierarchy(), appearance.confirmDeleteHierarchy(selectedItem.getLabel()));
            cmb.addDialogHideHandler(new DialogHideEvent.DialogHideHandler() {
                @Override
                public void onDialogHide(DialogHideEvent event) {
                    if (event.getHideButton() == Dialog.PredefinedButton.YES) {
                        fireEvent(new DeleteHierarchyEvent(ontologyDropDown.getCurrentValue(), selectedItem));
                    }
                }
            });
            cmb.show();
        }
    }

    @UiHandler("categorize")
    void categorizeButtonClicked(SelectEvent event) {
        fireEvent(new CategorizeButtonClickedEvent(targetApp, editorTreeStore.getRootItems()));
    }

    @UiHandler("deleteApp")
    void deleteAppClicked(SelectEvent event) {
        ConfirmMessageBox msgBox = new ConfirmMessageBox(appearance.confirmDeleteAppTitle(),
                                                         appearance.confirmDeleteAppWarning(targetApp.getName()));

        msgBox.addDialogHideHandler(new DialogHideEvent.DialogHideHandler() {
            @Override
            public void onDialogHide(DialogHideEvent event) {
                if(Dialog.PredefinedButton.YES.equals(event.getHideButton())){
                    fireEvent(new DeleteAppsSelected(Lists.newArrayList(targetApp)));
                }
            }
        });
        msgBox.show();
    }

    @UiHandler("refreshPreview")
    void refreshPreviewButtonClicked(SelectEvent event) {
        fireEvent(new RefreshPreviewButtonClicked(ontologyDropDown.getCurrentValue(), editorTreeStore.getRootItems()));
    }

    @UiHandler("restoreApp")
    void restoreAppClicked(SelectEvent event) {
        fireEvent(new RestoreAppButtonClicked(targetApp));
    }

    Tree<OntologyHierarchy, String> createTree(TreeStore<OntologyHierarchy> store) {
        Tree<OntologyHierarchy, String> ontologyTree = new Tree<>(store, new ValueProvider<OntologyHierarchy, String>() {
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

        store.addSortInfo(new Store.StoreSortInfo<>(ontologyUtil.getOntologyNameComparator(), SortDir.ASC));

        return ontologyTree;
    }

    void setUpDND() {
        //App DND
        DropTarget previewGridTarget = new DropTarget(previewGridView.asWidget());
        previewGridTarget.setAllowSelfAsSource(false);
        previewGridTarget.addDragEnterHandler(hierarchyToAppDND);
        previewGridTarget.addDragMoveHandler(hierarchyToAppDND);
        previewGridTarget.addDragEnterHandler(hierarchyToAppDND);
        previewGridTarget.addDropHandler(hierarchyToAppDND);

        DropTarget editorGridTarget = new DropTarget(editorGridView.asWidget());
        editorGridTarget.setAllowSelfAsSource(false);
        editorGridTarget.addDragEnterHandler(hierarchyToAppDND);
        editorGridTarget.addDragMoveHandler(hierarchyToAppDND);
        editorGridTarget.addDragEnterHandler(hierarchyToAppDND);
        editorGridTarget.addDropHandler(hierarchyToAppDND);

        DragSource previewGridSource = new DragSource(previewGridView.asWidget());
        previewGridSource.addDragStartHandler(appToHierarchyDND);

        DragSource editorGridSource = new DragSource(editorGridView.asWidget());
        editorGridSource.addDragStartHandler(appToHierarchyDND);

        //Tree DND
        DragSource editorTreeSource = new DragSource(editorTree);
        editorTreeSource.addDragStartHandler(hierarchyToAppDND);

        DropTarget editorTreeTarget = new DropTarget(editorTree);
        editorTreeTarget.setAllowSelfAsSource(false);
        editorTreeTarget.addDragEnterHandler(appToHierarchyDND);
        editorTreeTarget.addDragMoveHandler(appToHierarchyDND);
        editorTreeTarget.addDragEnterHandler(appToHierarchyDND);
        editorTreeTarget.addDropHandler(appToHierarchyDND);
    }

}
