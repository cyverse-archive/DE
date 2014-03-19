package org.iplantc.admin.belphegor.client.apps.views;

import org.iplantc.admin.belphegor.client.I18N;
import org.iplantc.de.client.models.apps.AppGroup;
import org.iplantc.de.resources.client.IplantResources;

import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.Store.StoreSortInfo;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.tree.Tree;
import com.sencha.gxt.widget.core.client.tree.Tree.CheckNodes;
import com.sencha.gxt.widget.core.client.tree.Tree.TreeAppearance;
import com.sencha.gxt.widget.core.client.tree.TreeStyle;

import java.util.Comparator;
import java.util.List;

public class AppCategorizeViewImpl implements AppCategorizeView {

    @UiTemplate("AppCategorizeView.ui.xml")
    interface AppCategorizeViewUiBinder extends UiBinder<Widget, AppCategorizeViewImpl> {
    }

    private static AppCategorizeViewUiBinder uiBinder = GWT.create(AppCategorizeViewUiBinder.class);

    @UiField(provided = true)
    TreeStore<AppGroup> treeStore;

    @UiField(provided = true)
    Tree<AppGroup, String> tree;

    @UiField
    ContentPanel con;

    final private Widget widget;

    public AppCategorizeViewImpl() {
        initCategoryTree();

        widget = uiBinder.createAndBindUi(this);

        addClearButton();
    }

    private void addClearButton() {
        TextButton btnClear = new TextButton(I18N.DISPLAY.clearSelection(), new SelectHandler() {

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
        treeStore = new TreeStore<AppGroup>(new ModelKeyProvider<AppGroup>() {

            @Override
            public String getKey(AppGroup item) {
                return item.getId();
            }
        });

        initTreeStoreSorter();

        tree = new Tree<AppGroup, String>(treeStore, new ValueProvider<AppGroup, String>() {

            @Override
            public String getValue(AppGroup object) {
                return object.getName();
            }

            @Override
            public void setValue(AppGroup object, String value) {
                // do nothing intentionally
            }

            @Override
            public String getPath() {
                return "name"; //$NON-NLS-1$
            }
        });

        setTreeIcons();
        tree.setCheckable(true);
        tree.setCheckNodes(CheckNodes.LEAF);
    }

    private void initTreeStoreSorter() {
        Comparator<AppGroup> comparator = new Comparator<AppGroup>() {

            @Override
            public int compare(AppGroup group1, AppGroup group2) {
                return group1.getName().compareToIgnoreCase(group2.getName());
            }
        };

        treeStore.addSortInfo(new StoreSortInfo<AppGroup>(comparator, SortDir.ASC));
    }

    /**
     * FIXME JDS This needs to be implemented in an {@link TreeAppearance}
     */
    private void setTreeIcons() {
        TreeStyle style = tree.getStyle();
        style.setNodeCloseIcon(IplantResources.RESOURCES.category());
        style.setNodeOpenIcon(IplantResources.RESOURCES.category_open());
        style.setLeafIcon(IplantResources.RESOURCES.subCategory());
    }

    @Override
    public void setAppGroups(List<AppGroup> groups) {
        treeStore.clear();
        addAppGroup(null, groups);
    }

    private void addAppGroup(AppGroup parent, List<AppGroup> children) {
        if ((children == null) || children.isEmpty()) {
            return;
        }

        if (parent == null) {
            treeStore.add(children);
        } else {
            treeStore.add(parent, children);
        }

        for (AppGroup group : children) {
            addAppGroup(group, group.getGroups());
        }
    }

    @Override
    public void setSelectedGroups(List<AppGroup> groups) {
        List<AppGroup> selection = Lists.newArrayList();
        for (AppGroup group : groups) {
            AppGroup model = treeStore.findModel(group);
            if (model != null) {
                selection.add(model);
                tree.setExpanded(model, true);
            }
        }

        tree.setCheckedSelection(selection);
    }

    @Override
    public List<AppGroup> getSelectedGroups() {
        return tree.getCheckedSelection();
    }

    @Override
    public void removeGroupWithId(String groupId) {
        AppGroup group = treeStore.findModelWithKey(groupId);
        if (group != null) {
            treeStore.remove(group);
        }
    }
}
