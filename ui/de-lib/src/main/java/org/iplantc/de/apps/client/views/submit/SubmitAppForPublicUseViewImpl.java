package org.iplantc.de.apps.client.views.submit;

import org.iplantc.de.apps.client.SubmitAppForPublicUseView;
import org.iplantc.de.apps.client.gin.OntologyHierarchyTreeStoreProvider;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppAutoBeanFactory;
import org.iplantc.de.client.models.apps.AppRefLink;
import org.iplantc.de.client.models.apps.PublishAppRequest;
import org.iplantc.de.client.models.avu.Avu;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.models.ontologies.OntologyHierarchy;
import org.iplantc.de.client.util.OntologyUtil;
import org.iplantc.de.commons.client.validators.UrlValidator;
import org.iplantc.de.commons.client.widgets.ContextualHelpPopup;
import org.iplantc.de.diskResource.client.gin.factory.DiskResourceSelectorFieldFactory;
import org.iplantc.de.diskResource.client.views.widgets.FolderSelectorField;
import org.iplantc.de.shared.DEProperties;

import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import com.sencha.gxt.core.client.Style;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.Store.StoreSortInfo;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.button.ToolButton;
import com.sencha.gxt.widget.core.client.container.HtmlLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.CancelEditEvent;
import com.sencha.gxt.widget.core.client.event.CompleteEditEvent;
import com.sencha.gxt.widget.core.client.event.CompleteEditEvent.CompleteEditHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextArea;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.grid.CellSelectionModel;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.Grid.GridCell;
import com.sencha.gxt.widget.core.client.grid.editing.AbstractGridEditing;
import com.sencha.gxt.widget.core.client.grid.editing.ClicksToEdit;
import com.sencha.gxt.widget.core.client.grid.editing.GridEditing;
import com.sencha.gxt.widget.core.client.grid.editing.GridRowEditing;
import com.sencha.gxt.widget.core.client.selection.CellSelection;
import com.sencha.gxt.widget.core.client.selection.CellSelectionChangedEvent;
import com.sencha.gxt.widget.core.client.selection.CellSelectionChangedEvent.CellSelectionChangedHandler;
import com.sencha.gxt.widget.core.client.tree.Tree;
import com.sencha.gxt.widget.core.client.tree.Tree.TreeAppearance;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A form that enables the user to make an app public
 * 
 * @author sriram, jstroot
 */
public class SubmitAppForPublicUseViewImpl implements SubmitAppForPublicUseView {

    @UiTemplate("SubmitAppForPublicUseView.ui.xml")
    interface SubmitAppForPublicUseViewUiBinder extends UiBinder<Widget, SubmitAppForPublicUseViewImpl> { }

    private static final SubmitAppForPublicUseViewUiBinder uiBinder = GWT.create(SubmitAppForPublicUseViewUiBinder.class);

    final private Widget widget;

    @UiField TextField appName;
    @UiField TextArea appDesc;
    @UiField VerticalLayoutContainer container;
    @UiField(provided = true) TreeStore<OntologyHierarchy> treeStore;
    @UiField(provided = true) Tree<OntologyHierarchy, String> tree;
    @UiField Grid<AppRefLink> grid;
    @UiField ListStore<AppRefLink> listStore;
    @UiField TextButton addBtn;
    @UiField TextButton delBtn;
    @UiField ContentPanel catPanel;
    @UiField ContentPanel refPanel;
    @UiField FieldLabel appField;
    @UiField FieldLabel descField;
    @UiField HtmlLayoutContainer intro;
    @UiField FieldLabel descInputField;
    @UiField FieldLabel descParamField;
    @UiField FieldLabel descOutputField;
    @UiField TextArea inputDesc;
    @UiField TextArea paramDesc;
    @UiField TextArea outputDesc;
    @UiField HTML testDataLbl;
    @UiField(provided = true) final FolderSelectorField dataFolderSelector;
    @UiField(provided = true) final SubmitAppAppearance appearance;

    private GridEditing<AppRefLink> editing;
    private App selectedApp;
    private final DEProperties deProps;
    private OntologyUtil ontologyUtil = OntologyUtil.getInstance();
    private AppAutoBeanFactory factory;

    final Logger LOG = Logger.getLogger("Submit Application for Public Use");

    @Inject
    SubmitAppForPublicUseViewImpl(final DiskResourceSelectorFieldFactory folderSelectorFieldFactory,
                                  final SubmitAppAppearance appearance,
                                  final DEProperties props,
                                  AppAutoBeanFactory factory) {
        this.appearance = appearance;
        this.dataFolderSelector = folderSelectorFieldFactory.defaultFolderSelector();
        this.deProps = props;
        this.factory = factory;
        initCategoryTree();
        widget = uiBinder.createAndBindUi(this);
        initGrid();
        dataFolderSelector.addValueChangeHandler(new ValueChangeHandler<Folder>() {

            @Override
            public void onValueChange(ValueChangeEvent<Folder> event) {
                validate();

            }
        });
        addHelp();
    }

    @UiFactory
    HtmlLayoutContainer buildIntroContainer() {
        return new HtmlLayoutContainer(appearance.submitForPublicUseIntro());
    }

    @Override
    public void loadReferences(List<AppRefLink> refs) {
        listStore.clear();
        listStore.addAll(refs);
    }

    private void initGrid() {
        CellSelectionModel<AppRefLink> csm = buildRefCellSelectionModel();
        grid.setSelectionModel(csm);
        editing = createGridEditing();
        ((AbstractGridEditing<AppRefLink>)editing).setClicksToEdit(ClicksToEdit.TWO);
        ColumnConfig<AppRefLink, String> cc = grid.getColumnModel().getColumn(0);
        final TextField editor = buildRefLinkEditor();
        editing.addCancelEditHandler(new CancelEditEvent.CancelEditHandler<AppRefLink>() {
            @Override
            public void onCancelEdit(CancelEditEvent<AppRefLink> event) {
                int editedRow = event.getEditCell().getRow();
                AppRefLink refLink = listStore.get(editedRow);
                if (refLink.getRefLink() == null) {
                    listStore.remove(refLink);
                }
            }
        });
        editing.addEditor(cc, editor);
        editing.addCompleteEditHandler(new CompleteEditHandler<AppRefLink>() {

            @Override
            public void onCompleteEdit(CompleteEditEvent<AppRefLink> event) {
                String refLink = editor.getCurrentValue();
                AppRefLink ref = listStore.get(0);
                ref.setRefLink(refLink);
                listStore.update(ref);
            }
        });
        grid.getView().setAutoExpandColumn(cc);
        grid.setHeight(100);
    }

    private void addHelp() {
        final ToolButton tool_help_ref = new ToolButton(ToolButton.QUESTION);
        refPanel.getHeader().addTool(tool_help_ref);
        tool_help_ref.addSelectHandler(new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                ContextualHelpPopup popup = new ContextualHelpPopup();
                popup.add(new HTML(appearance.publicSubmissionFormAttach()));
                popup.showAt(tool_help_ref.getAbsoluteLeft(), tool_help_ref.getAbsoluteTop() + 15);

            }
        });
        final ToolButton tool_help_cat = new ToolButton(ToolButton.QUESTION);
        catPanel.getHeader().addTool(tool_help_cat);
        tool_help_cat.addSelectHandler(new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                ContextualHelpPopup popup = new ContextualHelpPopup();
                popup.add(new HTML(appearance.publicSubmissionFormCategories()));
                popup.showAt(tool_help_cat.getAbsoluteLeft(), tool_help_cat.getAbsoluteTop() + 15);

            }
        });
    }

    private CellSelectionModel<AppRefLink> buildRefCellSelectionModel() {
        CellSelectionModel<AppRefLink> csm = new CellSelectionModel<>();
        csm.addCellSelectionChangedHandler(new CellSelectionChangedHandler<AppRefLink>() {
            @Override
            public void onCellSelectionChanged(CellSelectionChangedEvent<AppRefLink> event) {
                List<CellSelection<AppRefLink>> list = event.getSelection();
                if (list.size() > 0) {
                    if (list.get(0).getModel() == null) {
                        delBtn.disable();
                    } else {
                        delBtn.enable();
                    }
                } else {
                    delBtn.disable();
                }

            }
        });
        return csm;
    }

    private void initCategoryTree() {
        treeStore = new OntologyHierarchyTreeStoreProvider().get();
        tree = new Tree<>(treeStore, new ValueProvider<OntologyHierarchy, String>() {

            @Override
            public String getValue(OntologyHierarchy object) {
                return object.getLabel();
            }

            @Override
            public void setValue(OntologyHierarchy object, String value) {
                // do nothing intentionally
            }

            @Override
            public String getPath() {
                return null;
            }
        });

        setTreeIcons();
        tree.setCheckable(true);
        tree.setCheckStyle(Tree.CheckCascade.NONE);
        treeStore.addSortInfo(new StoreSortInfo<>(ontologyUtil.getOntologyNameComparator(),
                                                  SortDir.ASC));
        tree.getSelectionModel().setSelectionMode(Style.SelectionMode.MULTI);
        tree.getSelectionModel().addSelectionHandler(new SelectionHandler<OntologyHierarchy>() {
            @Override
            public void onSelection(SelectionEvent<OntologyHierarchy> event) {
                OntologyHierarchy selectedItem = event.getSelectedItem();
                if (tree.isChecked(selectedItem)) {
                    tree.setChecked(selectedItem, Tree.CheckState.UNCHECKED);
                } else {
                    tree.setChecked(selectedItem, Tree.CheckState.CHECKED);
                }
            }
        });
    }

    @UiFactory
    ListStore<AppRefLink> buildRefLinksStore() {
        return new ListStore<>(new ModelKeyProvider<AppRefLink>() {

            @Override
            public String getKey(AppRefLink item) {
                if (item == null) {
                    return null;
                }
                return item.getId();
            }
        });

    }

    @UiFactory
    ColumnModel<AppRefLink> buildRefLinkColumn() {
        List<ColumnConfig<AppRefLink, ?>> columns = new ArrayList<>();

        ColumnConfig<AppRefLink, String> link = new ColumnConfig<>(new ValueProvider<AppRefLink, String>() {

                                                                       @Override
                                                                       public String
                                                                               getValue(AppRefLink object) {
                                                                           return object.getRefLink();
                                                                       }

                                                                       @Override
                                                                       public void
                                                                               setValue(AppRefLink object,
                                                                                        String value) {
                                                                           // do nothing

                                                                       }

                                                                       @Override
                                                                       public String getPath() {

                                                                           return null;
                                                                       }
                                                                   },
                                                                   250,
                                                                   appearance.links());

        link.setHideable(false);
        link.setMenuDisabled(true);
        columns.add(link);
        return new ColumnModel<>(columns);
    }

    private TextField buildRefLinkEditor() {
        TextField field = new TextField();
        field.addValidator(new UrlValidator());
        field.setAllowBlank(false);
        return field;
    }

    /**
     * FIXME JDS This needs to be implemented in an {@link TreeAppearance}
     */
    private void setTreeIcons() {
        com.sencha.gxt.widget.core.client.tree.TreeStyle style = tree.getStyle();
        style.setNodeCloseIcon(appearance.categoryIcon());
        style.setNodeOpenIcon(appearance.categoryOpenIcon());
        style.setLeafIcon(appearance.subCategoryIcon());
    }

    @Override
    public Widget asWidget() {
        return widget;
    }

    @UiHandler("addBtn")
    public void addClicked(SelectEvent event) {
        AppAutoBeanFactory factory = GWT.create(AppAutoBeanFactory.class);
        AutoBean<AppRefLink> bean = AutoBeanCodex.decode(factory, AppRefLink.class, "{}");
        editing.cancelEditing();
        AppRefLink link = bean.as();
        link.setId(new Date().getTime() + "");
        listStore.add(0, link);
        editing.startEditing(new GridCell(0, 0));
    }

    @UiHandler("delBtn")
    public void delClicked(SelectEvent event) {
        List<AppRefLink> links = grid.getSelectionModel().getSelectedItems();
        for (AppRefLink link : links) {
            listStore.remove(link);
        }
    }

    @Override
    public TreeStore<OntologyHierarchy> getTreeStore() {
        return treeStore;
    }

    private GridEditing<AppRefLink> createGridEditing() {
        return new GridRowEditing<>(grid);
    }

    @Override
    public boolean validate() {
        Folder tdFolder = dataFolderSelector.getValue();

        if (tdFolder == null ||  tdFolder.getPath() == null) {
            return false;
        }
        
        if (!tdFolder.getPath().startsWith(deProps.getCommunityDataPath())) {
            dataFolderSelector.setInfoErrorText(appearance.testDataWarn());
            return false;
        } else {
            dataFolderSelector.setInfoErrorText(null);
        }

        return appName.getCurrentValue() != null && appName.getCurrentValue().length() <= 255
                && appDesc.getCurrentValue() != null && checkRefLinksGrid() && checkAppCategories()
                && inputDesc.isValid() && paramDesc.isValid()
                && outputDesc.isValid();

    }

    public static native String generateMarkDown(String name,
                                                 String qs,
                                                 String td,
                                                 String ifp,
                                                 String ps,
                                                 String of) /*-{

		var context = {
			appName : name,
			quickStart : qs,
			testData : td,
			inputFiles : ifp,
			params : ps,
			outputFiles : of
		};
		var template = $wnd.Handlebars
				.compile('### {{appName}} \n> #### Description and Quick Start \n>> {{quickStart}} \n> #### Test Data \n>> {{testData}} \n> #### Input File(s) \n>> {{inputFiles}} \n> #### Parameters Used in App \n>> {{params}} \n> #### Output File(s) \n>> {{outputFiles}}');
        return template(context);
    }-*/
    ;

    @Override
    public PublishAppRequest getPublishAppRequest() {
        PublishAppRequest appRequest = factory.publishAppRequest().as();

        appRequest.setId(selectedApp.getId());
        appRequest.setName(appName.getValue());
        appRequest.setDescription(appDesc.getValue());
        appRequest.setAvus(getAppAvus());
        appRequest.setReferences(getRefLinks());



        String md = generateMarkDown(appName.getValue(),
                                       appDesc.getValue(),
                                       dataFolderSelector.getValue().getPath(),
                                       inputDesc.getValue(),
                                       paramDesc.getValue(),
                                       outputDesc.getValue());
        LOG.log(Level.SEVERE, md);
        appRequest.setDocumentation(md);

        return appRequest;
    }

    private boolean checkRefLinksGrid() {
        if (listStore.size() > 0) {
            for (AppRefLink link : listStore.getAll()) {
                if (link.getRefLink() == null || link.getRefLink().isEmpty()) {
                    return false;
                }
            }
            return true;
        } else {
            return true;
        }

    }

    private boolean checkAppCategories() {
        return tree.getCheckedSelection().size() > 0;
    }

    private List<String> getRefLinks() {
        List<String> refLinks = Lists.newArrayList();
        for (AppRefLink model : listStore.getAll()) {
            refLinks.add(model.getRefLink());
        }
        return refLinks;
    }

    private List<Avu> getAppAvus() {
        List<Avu> avus = Lists.newArrayList();
        for (OntologyHierarchy model : tree.getCheckedSelection()) {
            avus.add(ontologyUtil.convertHierarchyToAvu(model));
        }
        return avus;
    }

    @Override
    public App getSelectedApp() {
        return selectedApp;
    }

    @Override
    public void setSelectedApp(App selectedApp) {
        this.selectedApp = selectedApp;
        appName.setValue(selectedApp.getName());
        appDesc.setValue(selectedApp.getDescription());
    }

}
