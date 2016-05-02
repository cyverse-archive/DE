package org.iplantc.de.admin.desktop.client.ontologies.views.dialogs;

import org.iplantc.de.admin.desktop.client.ontologies.OntologiesView;
import org.iplantc.de.admin.desktop.client.ontologies.events.PublishOntologyClickEvent;
import org.iplantc.de.client.models.IsHideable;
import org.iplantc.de.client.models.ontologies.Ontology;
import org.iplantc.de.commons.client.views.dialogs.IPlantDialog;

import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.client.ui.HTML;
import com.google.inject.Inject;

import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.form.FieldLabel;

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
        this.handlers = handlers;

        setHideOnButtonClick(false);
        setHeadingText(appearance.publishOntology());
        setResizable(true);
        setPixelSize(500, 200);
        setMinHeight(200);
        setMinWidth(500);

        setOnEsc(false);

        setUpButtons();

        VerticalLayoutContainer con = new VerticalLayoutContainer();
        addText(con);
        add(con);

        show();

    }

    private void addText(VerticalLayoutContainer con) {
        HTML publishOntologyWarning = new HTML();
        publishOntologyWarning.setHTML(appearance.publishOntologyWarning());
        con.add(publishOntologyWarning);

        FieldLabel activeOntologyLabel = new FieldLabel();
        activeOntologyLabel.setText(appearance.activeOntologyLabel());
        HTML activeOntologyField = new HTML();
        activeOntologyField.setHTML(appearance.activeOntologyField(activeOntology.getVersion()));
        activeOntologyField.setWidth("400");
        activeOntologyLabel.add(activeOntologyField);
        con.add(activeOntologyLabel);

        FieldLabel editedOntologyLabel = new FieldLabel();
        editedOntologyLabel.setText(appearance.editedOntologyLabel());
        HTML editedOntologyField = new HTML();
        editedOntologyField.setHTML(appearance.editedOntologyField(editedOntology.getVersion()));
        editedOntologyField.setWidth("400");
        editedOntologyLabel.add(editedOntologyField);
        con.add(editedOntologyLabel);
    }

    private void setUpButtons() {
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
    }
}
