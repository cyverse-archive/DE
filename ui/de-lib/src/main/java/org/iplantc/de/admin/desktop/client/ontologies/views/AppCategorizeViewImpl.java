package org.iplantc.de.admin.desktop.client.ontologies.views;

import org.iplantc.de.admin.desktop.client.ontologies.OntologiesView;
import org.iplantc.de.client.models.ontologies.OntologyHierarchy;
import org.iplantc.de.client.util.OntologyUtil;

import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.Store.StoreSortInfo;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.tree.Tree;
import com.sencha.gxt.widget.core.client.tree.TreeStyle;

import java.util.Comparator;
import java.util.List;

/**
 * @author jstroot
 */
public class AppCategorizeViewImpl implements AppCategorizeView {

    @UiTemplate("AppCategorizeView.ui.xml")
    interface AppCategorizeViewUiBinder extends UiBinder<Widget, AppCategorizeViewImpl> {
    }

    private static AppCategorizeViewUiBinder uiBinder = GWT.create(AppCategorizeViewUiBinder.class);

    @UiField(provided = true) TreeStore<OntologyHierarchy> treeStore;
    @UiField(provided = true) Tree<OntologyHierarchy, String> tree;
    @UiField ContentPanel con;
    @UiField(provided = true) OntologiesView.OntologiesViewAppearance appearance = GWT.create(OntologiesView.OntologiesViewAppearance.class);

    final private Widget widget;
    private OntologyUtil ontologyUtil;

    @Inject
    public AppCategorizeViewImpl(final TreeStore<OntologyHierarchy> treeStore) {
        this.treeStore = treeStore;
        initCategoryTree();
        widget = uiBinder.createAndBindUi(this);
        ontologyUtil = OntologyUtil.getInstance();

        addClearButton();
    }

    private void addClearButton() {
        TextButton btnClear = new TextButton(appearance.clearHierarchySelection(), new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                tree.setCheckedSelection(null);
            }
        });

        con.getHeader().addTool(btnClear);
    }

    @Override
    public Widget asWidget() {
        return widget;
    }

    private void initCategoryTree() {
        initTreeStoreSorter();

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
                return "name"; //$NON-NLS-1$
            }
        });

        setTreeIcons();
        tree.setCheckable(true);
        tree.setCheckStyle(Tree.CheckCascade.NONE);
    }

    private void initTreeStoreSorter() {
        Comparator<OntologyHierarchy> comparator = new Comparator<OntologyHierarchy>() {

            @Override
            public int compare(OntologyHierarchy hierarchy1, OntologyHierarchy hierarchy2) {
                return hierarchy1.getLabel().compareToIgnoreCase(hierarchy2.getLabel());
            }
        };

        treeStore.addSortInfo(new StoreSortInfo<>(comparator, SortDir.ASC));
    }

    /**
     */
    private void setTreeIcons() {
        TreeStyle style = tree.getStyle();
        style.setNodeCloseIcon(appearance.blueFolder());
        style.setNodeOpenIcon(appearance.blueFolderOpen());
        style.setLeafIcon(appearance.blueFolderLeaf());
    }

    @Override
    public void setHierarchies(List<OntologyHierarchy> hierarchies) {
        treeStore.clear();
        addHierarchies(null, hierarchies);
    }

    private void removeUnclassifiedsAndTrash(List<OntologyHierarchy> hierarchies) {
        for (OntologyHierarchy hierarchy : hierarchies) {
            if (ontologyUtil.isUnclassified(hierarchy) || hierarchy.getIri().equalsIgnoreCase(OntologiesView.TRASH_CATEGORY)) {
                hierarchies.remove(hierarchy);
            }
        }
    }

    private void addHierarchies(OntologyHierarchy parent, List<OntologyHierarchy> children) {
        if ((children == null) || children.isEmpty()) {
            return;
        }
        
        removeUnclassifiedsAndTrash(children);

        if (parent == null) {
            treeStore.add(children);
        } else {
            treeStore.add(parent, children);
        }

        for (OntologyHierarchy hierarchy : children) {
            addHierarchies(hierarchy, hierarchy.getSubclasses());
        }
    }

    @Override
    public void setSelectedHierarchies(List<OntologyHierarchy> hierarchies) {
        List<OntologyHierarchy> selection = Lists.newArrayList();
        for (OntologyHierarchy hierarchy : hierarchies) {
            OntologyHierarchy model = treeStore.findModel(hierarchy);
            if (model != null) {
                selection.add(model);
                tree.setExpanded(model, true);
            }
        }

        tree.setCheckedSelection(selection);
    }

    @Override
    public void mask(String loadingMask) {
        tree.mask(loadingMask);
    }

    @Override
    public void unmask() {
        tree.unmask();
    }

    @Override
    public List<OntologyHierarchy> getSelectedCategories() {
        if (tree.isCheckable()) {
            return tree.getCheckedSelection();
        }
        return tree.getSelectionModel().getSelection();
    }
}
