package org.iplantc.de.apps.client;

import org.iplantc.de.apps.client.events.AppSearchResultLoadEvent;
import org.iplantc.de.apps.client.events.selection.AppInfoSelectedEvent;
import org.iplantc.de.apps.client.events.selection.OntologyHierarchySelectionChangedEvent;
import org.iplantc.de.client.models.IsMaskable;
import org.iplantc.de.client.models.ontologies.OntologyHierarchy;
import org.iplantc.de.commons.client.widgets.DETabPanel;

import com.google.gwt.user.client.ui.IsWidget;

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

        void go(DETabPanel tabPanel);

        void setViewDebugId(String baseID);
    }

    Tree<OntologyHierarchy, String> getTree();

}
