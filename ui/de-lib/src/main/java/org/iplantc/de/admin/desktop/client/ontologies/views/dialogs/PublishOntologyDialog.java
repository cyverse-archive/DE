package org.iplantc.de.admin.desktop.client.ontologies.views.dialogs;

import org.iplantc.de.admin.desktop.client.ontologies.OntologiesView;
import org.iplantc.de.admin.desktop.client.ontologies.events.PublishOntologyClickEvent;
import org.iplantc.de.client.models.IsHideable;
import org.iplantc.de.client.models.ontologies.Ontology;
import org.iplantc.de.commons.client.views.dialogs.IPlantDialog;

import com.google.gwt.event.shared.HasHandlers;
import com.google.inject.Inject;

import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextField;

/**
 * @author aramsey
 */
public class PublishOntologyDialog extends IPlantDialog implements IsHideable {

    private OntologiesView.OntologiesViewAppearance appearance;
    private Ontology editedOntology;
    private Ontology activeOntology;
    private HasHandlers handlers;

    @Inject
    public PublishOntologyDialog(OntologiesView.OntologiesViewAppearance appearance,
                                 final Ontology editedOntology, Ontology activeOntology, final HasHandlers handlers) {
        super(true);

        this.editedOntology = editedOntology;
        this.appearance = appearance;
        this.activeOntology = activeOntology;

        setHideOnButtonClick(false);
        setHeadingText(appearance.publishOntology());
        setResizable(true);
        setPixelSize(500, 200);
        setMinHeight(200);
        setMinWidth(500);

        setOnEsc(false);

        setPredefinedButtons(PredefinedButton.OK, PredefinedButton.CANCEL);
        getOkButton().setText(appearance.setActiveVersion());
        getButton(PredefinedButton.CANCEL).addSelectHandler(new SelectEvent.SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                hide();
            }
        });
        getOkButton().addSelectHandler(new SelectEvent.SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                handlers.fireEvent(new PublishOntologyClickEvent(editedOntology));
            }
        });

        VerticalLayoutContainer con = new VerticalLayoutContainer();
        FieldLabel activeOntologyLabel = new FieldLabel();
        activeOntologyLabel.setText(appearance.activeOntologyLabel());
        TextField activeOntologyField = new TextField();
        activeOntologyField.setAllowTextSelection(false);
        activeOntologyField.setText(activeOntology.getVersion());
        activeOntologyField.setWidth(400);
        activeOntologyLabel.add(activeOntologyField);
        con.add(activeOntologyLabel);
//        add(activeOntologyField);

        FieldLabel editedOntologyLabel = new FieldLabel();
        editedOntologyLabel.setText(appearance.editedOntologyLabel());
        TextField editedOntologyField = new TextField();
        editedOntologyField.setText(editedOntology.getVersion());
        editedOntologyField.setWidth(400);
        editedOntologyLabel.add(editedOntologyField);
        con.add(editedOntologyLabel);
//        add(editedOntologyField);

        add(con);

        show();

    }
}
