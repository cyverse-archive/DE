package org.iplantc.admin.belphegor.client.apps.views;

import org.iplantc.admin.belphegor.client.I18N;
import org.iplantc.de.client.models.apps.AppCategory;
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
    TreeStore<AppCategory> treeStore;

    @UiField(provided = true)
    Tree<AppCategory, String> tree;

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
        treeStore = new TreeStore<>(new ModelKeyProvider<AppCategory>() {

            @Override
            public String getKey(AppCategory item) {
                return item.getId();
            }
        });

        initTreeStoreSorter();

        tree = new Tree<>(treeStore, new ValueProvider<AppCategory, String>() {

            @Override
            public String getValue(AppCategory object) {
                return object.getName();
            }

            @Override
            public void setValue(AppCategory object, String value) {
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
        Comparator<AppCategory> comparator = new Comparator<AppCategory>() {

            @Override
            public int compare(AppCategory category1, AppCategory category2) {
                return category1.getName().compareToIgnoreCase(category2.getName());
            }
        };

        treeStore.addSortInfo(new StoreSortInfo<>(comparator, SortDir.ASC));
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
    public void setAppCategories(List<AppCategory> categories) {
        treeStore.clear();
        addAppCategory(null, categories);
    }

    private void addAppCategory(AppCategory parent, List<AppCategory> children) {
        if ((children == null) || children.isEmpty()) {
            return;
        }

        if (parent == null) {
            treeStore.add(children);
        } else {
            treeStore.add(parent, children);
        }

        for (AppCategory category : children) {
            addAppCategory(category, category.getCategories());
        }
    }

    @Override
    public void setSelectedCategories(List<AppCategory> categories) {
        List<AppCategory> selection = Lists.newArrayList();
        for (AppCategory category : categories) {
            AppCategory model = treeStore.findModel(category);
            if (model != null) {
                selection.add(model);
                tree.setExpanded(model, true);
            }
        }

        tree.setCheckedSelection(selection);
    }

    @Override
    public List<AppCategory> getSelectedCategories() {
        return tree.getCheckedSelection();
    }

    @Override
    public void removeCategoryWithId(String categoryId) {
        AppCategory category = treeStore.findModelWithKey(categoryId);
        if (category != null) {
            treeStore.remove(category);
        }
    }
}
