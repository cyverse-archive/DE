package org.iplantc.de.admin.desktop.client.ontologies.views.dialogs;

import org.iplantc.de.admin.desktop.client.ontologies.OntologiesView;
import org.iplantc.de.admin.desktop.client.ontologies.views.OntologyListView;
import org.iplantc.de.client.models.IsHideable;
import org.iplantc.de.client.models.ontologies.Ontology;
import org.iplantc.de.commons.client.views.dialogs.IPlantDialog;

import com.google.inject.Inject;

import com.sencha.gxt.core.client.dom.ScrollSupport;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;

import java.util.List;

/**
 * @author aramsey
 */
public class OntologyListDialog extends IPlantDialog implements IsHideable {

    private OntologiesView.OntologiesViewAppearance appearance;
    private OntologyListView view;

    @Inject
    public OntologyListDialog(OntologiesView.OntologiesViewAppearance appearance,
                              OntologyListView view) {
        super(true);

        this.appearance = appearance;
        this.view = view;

        setHideOnButtonClick(false);
        setHeadingText(appearance.ontologyListDialogName());
        setResizable(true);
        setPixelSize(1000, 500);
        setMinHeight(200);
        setMinWidth(500);

        setOnEsc(false);

        setPredefinedButtons(PredefinedButton.OK, PredefinedButton.CANCEL);
        getOkButton().setText(appearance.setActiveVersion());

        FlowLayoutContainer container = new FlowLayoutContainer();
        container.getScrollSupport().setScrollMode(ScrollSupport.ScrollMode.AUTO);
        container.add(this.view);
        add(container);
    }

    public void show(List<Ontology> ontologies) {
        view.addOntologies(ontologies);
        super.show();
    }
}
