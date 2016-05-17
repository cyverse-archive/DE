package org.iplantc.de.apps.client.presenter.hierarchies;

import org.iplantc.de.apps.client.OntologyHierarchiesView;
import org.iplantc.de.apps.client.events.AppSearchResultLoadEvent;
import org.iplantc.de.apps.client.events.selection.OntologyHierarchySelectionChangedEvent;
import org.iplantc.de.apps.client.gin.OntologyHierarchyTreeStoreProvider;
import org.iplantc.de.apps.client.gin.factory.OntologyHierarchiesViewFactory;
import org.iplantc.de.client.models.avu.AvuAutoBeanFactory;
import org.iplantc.de.client.models.ontologies.OntologyHierarchy;
import org.iplantc.de.client.services.OntologyServiceFacade;
import org.iplantc.de.client.util.OntologyUtil;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.info.IplantAnnouncer;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

import com.sencha.gxt.core.shared.FastMap;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.Store;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.widget.core.client.TabItemConfig;
import com.sencha.gxt.widget.core.client.TabPanel;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * @author aramsey
 */
public class OntologyHierarchiesPresenterImpl implements OntologyHierarchiesView.Presenter,
                                                         OntologyHierarchySelectionChangedEvent.OntologyHierarchySelectionChangedEventHandler {

    private class OntologyHierarchyNameComparator implements Comparator<OntologyHierarchy> {
        @Override
        public int compare(OntologyHierarchy o1, OntologyHierarchy o2) {
            return o1.getLabel().compareToIgnoreCase(o2.getLabel());
        }
    }

    @Inject IplantAnnouncer announcer;
    OntologyUtil ontologyUtil;
    private TabPanel viewTabPanel;
    private OntologyServiceFacade serviceFacade;
    private OntologyHierarchiesView.OntologyHierarchiesAppearance appearance;
    private AvuAutoBeanFactory avuFactory;
    private HandlerManager handlerManager;
    private Map<String, List<OntologyHierarchy>> iriToHierarchyMap = new FastMap<>();
    private OntologyHierarchiesViewFactory viewFactory;


    @Inject
    public OntologyHierarchiesPresenterImpl(OntologyHierarchiesViewFactory viewFactory,
                                            OntologyServiceFacade serviceFacade,
                                            OntologyHierarchiesView.OntologyHierarchiesAppearance appearance,
                                            AvuAutoBeanFactory avuFactory) {

        this.serviceFacade = serviceFacade;
        this.appearance = appearance;
        this.avuFactory = avuFactory;
        this.viewFactory = viewFactory;

        this.ontologyUtil = OntologyUtil.getInstance();
    }

    @Override
    public OntologyHierarchy getSelectedHierarchy() {
        OntologyHierarchiesView view = (OntologyHierarchiesView)viewTabPanel.getActiveWidget();
        return view.getTree().getSelectionModel().getSelectedItem();
    }

    @Override
    public OntologyHierarchiesView getView() {
        OntologyHierarchiesView view = (OntologyHierarchiesView)viewTabPanel.getActiveWidget();
        return view;
    }

    @Override
    public void go(final TabPanel tabPanel) {
        viewTabPanel = tabPanel;
        serviceFacade.getAppHierarchies(new AsyncCallback<List<OntologyHierarchy>>() {
            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(caught);
            }

            @Override
            public void onSuccess(List<OntologyHierarchy> result) {
                if (result != null && result.size() > 0) {
                    createViewTabs(result);
                }
            }
        });
    }

    void createViewTabs(List<OntologyHierarchy> results) {
        for (OntologyHierarchy hierarchy : results) {
            TreeStore<OntologyHierarchy> treeStore = getTreeStore(hierarchy);
            OntologyHierarchiesView view = viewFactory.create(treeStore);
            view.addOntologyHierarchySelectionChangedEventHandler(this);
            viewTabPanel.add(view.getTree(), new TabItemConfig(appearance.hierarchyLabelName(hierarchy)));
        }
    }

    @Override
    public void onAppSearchResultLoad(AppSearchResultLoadEvent event) {
        OntologyHierarchiesView view = (OntologyHierarchiesView)viewTabPanel.getActiveWidget();
        view.getTree().getSelectionModel().deselectAll();
    }

    TreeStore<OntologyHierarchy> getTreeStore(OntologyHierarchy hierarchy) {
        TreeStore<OntologyHierarchy> treeStore = new OntologyHierarchyTreeStoreProvider().get();
        treeStore.addSortInfo(new Store.StoreSortInfo<>(new OntologyHierarchyNameComparator(),
                                                        SortDir.ASC));
        ontologyUtil.addUnclassifiedChild(hierarchy);
        addHierarchies(treeStore, null, hierarchy.getSubclasses());
        return treeStore;
    }

    void addHierarchies(TreeStore<OntologyHierarchy> treeStore, OntologyHierarchy parent, List<OntologyHierarchy> children) {
        if ((children == null)
            || children.isEmpty()) {
            return;
        }
        if (parent == null) {
            treeStore.add(children);

        } else {
            treeStore.add(parent, children);
        }

        for (OntologyHierarchy hierarchy : children) {
            addHierarchies(treeStore, hierarchy, hierarchy.getSubclasses());
        }
    }

    @Override
    public void onOntologyHierarchySelectionChanged(OntologyHierarchySelectionChangedEvent event) {
        fireEvent(event);
    }

    @Override
    public HandlerRegistration addOntologyHierarchySelectionChangedEventHandler(
            OntologyHierarchySelectionChangedEvent.OntologyHierarchySelectionChangedEventHandler handler) {
        return ensureHandlers().addHandler(OntologyHierarchySelectionChangedEvent.TYPE, handler);
    }

    HandlerManager createHandlerManager() {
        return new HandlerManager(this);
    }

    HandlerManager ensureHandlers() {
        return handlerManager == null ? handlerManager = createHandlerManager() : handlerManager;
    }

    void fireEvent(GwtEvent<?> event) {
        if (handlerManager != null) {
            handlerManager.fireEvent(event);
        }
    }
}
