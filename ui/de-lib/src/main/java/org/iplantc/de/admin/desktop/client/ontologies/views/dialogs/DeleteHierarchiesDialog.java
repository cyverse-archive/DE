package org.iplantc.de.admin.desktop.client.ontologies.views.dialogs;

import org.iplantc.de.admin.desktop.client.ontologies.OntologiesView;
import org.iplantc.de.admin.desktop.client.ontologies.events.DeleteHierarchyEvent;
import org.iplantc.de.client.models.IsHideable;
import org.iplantc.de.client.models.ontologies.Ontology;
import org.iplantc.de.client.models.ontologies.OntologyHierarchy;
import org.iplantc.de.commons.client.views.dialogs.IPlantDialog;

import com.google.gwt.event.shared.HasHandlers;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import com.sencha.gxt.widget.core.client.event.SelectEvent;

import java.util.List;

/**
 * @author aramsey
 */
public class DeleteHierarchiesDialog extends IPlantDialog implements IsHideable {

    private OntologiesView.OntologiesViewAppearance appearance;
    private HasHandlers handlers;
    private Ontology editedOntology;
    private List<OntologyHierarchy> roots;
    private DeleteHierarchiesView view;

    @Inject
    public DeleteHierarchiesDialog(@Assisted Ontology editedOntology,
                                   @Assisted List<OntologyHierarchy> roots,
                                   @Assisted final HasHandlers handlers,
                                   OntologiesView.OntologiesViewAppearance appearance,
                                   DeleteHierarchiesView view) {
        super(true);
        this.appearance = appearance;
        this.handlers = handlers;
        this.editedOntology = editedOntology;
        this.roots = roots;
        this.view = view;

        view.addHierarchyRoots(roots);

        setHideOnButtonClick(false);
        setHeadingText(appearance.deleteHierarchy());
        setResizable(true);
        setPixelSize(appearance.publishDialogWidth(), appearance.publishDialogHeight());
        setMinHeight(appearance.publishDialogMinHeight());
        setMinWidth(appearance.publishDialogMinWidth());

        setOnEsc(false);

        setUpButtons();

        setWidth(500);
        setHeight(300);
        add(view);
        show();
    }

    private void setUpButtons() {
        setPredefinedButtons(PredefinedButton.OK, PredefinedButton.CANCEL);
        getOkButton().setText(appearance.deleteHierarchy());
        getButton(PredefinedButton.CANCEL).addSelectHandler(new SelectEvent.SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                hide();
            }
        });
        getOkButton().addSelectHandler(new SelectEvent.SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                List<OntologyHierarchy> deletedHierarchies = view.getDeletedHierarchies();
                handlers.fireEvent(new DeleteHierarchyEvent(editedOntology, deletedHierarchies));
                hide();
            }
        });
    }
}
