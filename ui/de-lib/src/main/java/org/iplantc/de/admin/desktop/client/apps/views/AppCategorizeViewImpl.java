package org.iplantc.de.admin.desktop.client.apps.views;

import org.iplantc.de.admin.apps.client.AppCategorizeView;
import org.iplantc.de.client.models.apps.AppCategory;

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

    @UiField(provided = true) TreeStore<AppCategory> treeStore;
    @UiField(provided = true) Tree<AppCategory, String> tree;
    @UiField ContentPanel con;
    @UiField(provided = true) AppCategorizeViewAppearance appearance = GWT.create(AppCategorizeViewAppearance.class);

    final private Widget widget;

    public AppCategorizeViewImpl(boolean singleSelect) {
        initCategoryTree(singleSelect);
        widget = uiBinder.createAndBindUi(this);

        addClearButton();
    }

    private void addClearButton() {
        TextButton btnClear = new TextButton(appearance.clearSelection(), new SelectHandler() {

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

    private void initCategoryTree(boolean single) {
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
        tree.setCheckable(!single);
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
     */
    private void setTreeIcons() {
        TreeStyle style = tree.getStyle();
        style.setNodeCloseIcon(appearance.category());
        style.setNodeOpenIcon(appearance.category_open());
        style.setLeafIcon(appearance.subCategory());
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
        if (tree.isCheckable()) {
            return tree.getCheckedSelection();
        }
        return tree.getSelectionModel().getSelection();
    }

    @Override
    public void removeCategoryWithId(String categoryId) {
        AppCategory category = treeStore.findModelWithKey(categoryId);
        if (category != null) {
            treeStore.remove(category);
        }
    }
}
