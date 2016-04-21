package org.iplantc.de.admin.desktop.client.ontologies;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * @author aramsey
 */
public interface OntologiesView extends IsWidget {

    interface OntologiesViewAppearance {
        String addOntology();

        ImageResource addIcon();
    }

    interface Presenter {
        void go(HasOneWidget container);
    }

}
