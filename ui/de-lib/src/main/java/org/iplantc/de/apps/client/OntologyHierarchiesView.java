package org.iplantc.de.apps.client;

import org.iplantc.de.apps.client.events.AppSearchResultLoadEvent;
import org.iplantc.de.apps.client.events.selection.AppInfoSelectedEvent;
import org.iplantc.de.apps.client.events.selection.OntologyHierarchySelectionChangedEvent;
import org.iplantc.de.client.models.IsMaskable;
import org.iplantc.de.client.models.ontologies.OntologyHierarchy;

import com.google.gwt.user.client.ui.IsWidget;

import com.sencha.gxt.widget.core.client.TabPanel;
import com.sencha.gxt.widget.core.client.tree.Tree;

/**
 * @author aramsey
 */
public interface OntologyHierarchiesView extends IsWidget,
                                                 IsMaskable,
                                                 OntologyHierarchySelectionChangedEvent.HasOntologyHierarchySelectionChangedEventHandlers {

    interface OntologyHierarchiesAppearance extends AppCategoriesView.AppCategoriesAppearance {

        String hierarchyLabelName(OntologyHierarchy hierarchy);
    }

    interface Presenter extends AppInfoSelectedEvent.AppInfoSelectedEventHandler,
                                AppSearchResultLoadEvent.AppSearchResultLoadEventHandler,
                                OntologyHierarchySelectionChangedEvent.HasOntologyHierarchySelectionChangedEventHandlers {

        OntologyHierarchy getSelectedHierarchy();

        OntologyHierarchiesView getView();

        void go(TabPanel tabPanel);
    }

    Tree<OntologyHierarchy, String> getTree();

}
