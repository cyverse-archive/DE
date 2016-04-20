package org.iplantc.de.apps.client.views.hierarchies;

import org.iplantc.de.apps.client.OntologyHierarchiesView;
import org.iplantc.de.apps.client.events.selection.AppCategorySelectionChangedEvent;
import org.iplantc.de.apps.shared.AppsModule;
import org.iplantc.de.client.models.ontologies.OntologyHierarchy;
import org.iplantc.de.client.util.OntologyUtil;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;
import com.sencha.gxt.widget.core.client.tree.Tree;

/**
 * @author aramsey
 */
public class OntologyHierarchiesViewImpl extends ContentPanel implements OntologyHierarchiesView,
                                                                         SelectionChangedEvent.SelectionChangedHandler<OntologyHierarchy> {

    interface OntologyHierarchiesViewImplUiBinder extends UiBinder<Widget, OntologyHierarchiesViewImpl> {}

    private static final OntologyHierarchiesViewImplUiBinder uiBinder = GWT.create(OntologyHierarchiesViewImplUiBinder.class);
    @UiField Tree<OntologyHierarchy, String> tree;
    private TreeStore<OntologyHierarchy> treeStore;
    private OntologyHierarchiesAppearance appearance;
    private OntologyUtil ontologyUtil;

    @Inject
    OntologyHierarchiesViewImpl(final OntologyHierarchiesAppearance appearance,
                                @Assisted TreeStore<OntologyHierarchy> treeStore) {
        this.appearance = appearance;
        this.treeStore = treeStore;
        this.ontologyUtil = OntologyUtil.getInstance();
        setWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public HandlerRegistration addAppCategorySelectedEventHandler(AppCategorySelectionChangedEvent.AppCategorySelectionChangedEventHandler handler) {
        return addHandler(handler, AppCategorySelectionChangedEvent.TYPE);
    }

    @Override
    public Tree<OntologyHierarchy, String> getTree() {
        return tree;
    }

    @Override
    public void onSelectionChanged(SelectionChangedEvent<OntologyHierarchy> event) {
//        final List<String> groupHierarchy = Lists.newArrayList();
//        if(!event.getSelection().isEmpty()){
//            groupHierarchy.addAll(hierarchyProvider.getGroupHierarchy(event.getSelection().iterator().next()));
//        }
//        fireEvent(new AppCategorySelectionChangedEvent(event.getSelection(),
//                                                       groupHierarchy));
    }

    @Override
    protected void onEnsureDebugId(String baseID) {
        super.onEnsureDebugId(baseID);
        tree.ensureDebugId(baseID + AppsModule.Ids.CATEGORIES_TREE);
    }

    @UiFactory
    Tree<OntologyHierarchy, String> createTree() {
        return new Tree<>(treeStore, new ValueProvider<OntologyHierarchy, String>() {
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
    }
}
