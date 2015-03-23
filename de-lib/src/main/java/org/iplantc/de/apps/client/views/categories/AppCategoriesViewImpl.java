package org.iplantc.de.apps.client.views.categories;

import org.iplantc.de.apps.client.AppCategoriesView;
import org.iplantc.de.apps.client.events.selection.AppCategorySelectionChangedEvent;
import org.iplantc.de.apps.client.models.AppCategoryStringValueProvider;
import org.iplantc.de.apps.shared.AppsModule;
import org.iplantc.de.client.models.apps.AppCategory;

import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import static com.sencha.gxt.core.client.Style.SelectionMode.SINGLE;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;
import com.sencha.gxt.widget.core.client.tree.Tree;

import java.util.List;

/**
 * Created by jstroot on 3/5/15.
 *
 * @author jstroot
 */
public class AppCategoriesViewImpl extends ContentPanel implements AppCategoriesView,
                                                                   SelectionChangedEvent.SelectionChangedHandler<AppCategory> {

    interface AppCategoriesViewImplUiBinder extends UiBinder<Tree<AppCategory, String>, AppCategoriesViewImpl> {
    }
    @UiField Tree<AppCategory, String> tree;

    private static final String WEST_COLLAPSE_BTN_ID = "idCategoryCollapseBtn";
    private static final AppCategoriesViewImplUiBinder ourUiBinder = GWT.create(AppCategoriesViewImplUiBinder.class);
    private final AppCategoriesAppearance appearance;
    private final AppCategoryHierarchyProvider hierarchyProvider;
    private final TreeStore<AppCategory> treeStore;

    @Inject
    AppCategoriesViewImpl(final AppCategoriesView.AppCategoriesAppearance appearance,
                          @Assisted final TreeStore<AppCategory> treeStore,
                          @Assisted final AppCategoriesView.AppCategoryHierarchyProvider hierarchyProvider) {
        this.appearance = appearance;
        this.treeStore = treeStore;
        this.hierarchyProvider = hierarchyProvider;
        setBodyBorder(false);
        setHeadingText(appearance.headingText());

        setWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public HandlerRegistration addAppCategorySelectedEventHandler(AppCategorySelectionChangedEvent.AppCategorySelectionChangedEventHandler handler) {
        return addHandler(handler, AppCategorySelectionChangedEvent.TYPE);
    }

    @Override
    public Tree<AppCategory, String> getTree() {
        return tree;
    }

    @Override
    public void onSelectionChanged(SelectionChangedEvent<AppCategory> event) {
        final List<String> groupHierarchy = Lists.newArrayList();
        if(!event.getSelection().isEmpty()){
            groupHierarchy.addAll(hierarchyProvider.getGroupHierarchy(event.getSelection().iterator().next()));
        }
        fireEvent(new AppCategorySelectionChangedEvent(event.getSelection(),
                                                       groupHierarchy));
    }

    @Override
    protected void onEnsureDebugId(String baseID) {
        super.onEnsureDebugId(baseID);
        tree.ensureDebugId(baseID + AppsModule.Ids.CATEGORIES_TREE);
        getHeader().getTool(0).getElement().setId(WEST_COLLAPSE_BTN_ID);
    }

    @UiFactory
    Tree<AppCategory, String> createTree() {
        final Tree<AppCategory, String> tree = new Tree<>(treeStore, new AppCategoryStringValueProvider());
        // Set tree icons
        appearance.setTreeIcons(tree.getStyle());
        tree.getSelectionModel().setSelectionMode(SINGLE);
        tree.getSelectionModel().addSelectionChangedHandler(this);

        return tree;
    }
}