package org.iplantc.de.admin.desktop.client.ontologies.views.dialogs;

import org.iplantc.de.admin.desktop.client.ontologies.OntologiesView;
import org.iplantc.de.admin.desktop.client.ontologies.events.SaveOntologyHierarchyEvent;
import org.iplantc.de.client.models.IsHideable;
import org.iplantc.de.client.models.ontologies.Ontology;
import org.iplantc.de.commons.client.validators.UrlValidator;
import org.iplantc.de.commons.client.views.dialogs.IPlantDialog;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.gwt.event.shared.HasHandlers;
import com.google.inject.Inject;

import com.sencha.gxt.core.client.dom.ScrollSupport;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

import java.util.List;

/**
 * @author aramsey
 */
public class SaveHierarchiesDialog extends IPlantDialog implements IsHideable {

    private OntologiesView.OntologiesViewAppearance appearance;
    private HasHandlers handlers;
    private FlowLayoutContainer con;
    private List<TextField> iriTextFields = Lists.newArrayList();
    private TextButton addButton = new TextButton();
    private TextButton deleteButton = new TextButton();
    private Ontology editedOntology;

    @Inject
    public SaveHierarchiesDialog(OntologiesView.OntologiesViewAppearance appearance,
                                 Ontology editedOntology,
                                 final HasHandlers handlers) {
        super(true);

        this.appearance = appearance;
        this.editedOntology = editedOntology;
        this.handlers = handlers;

        setHideOnButtonClick(false);
        setHeadingText(appearance.saveHierarchy());
        setResizable(true);
        setPixelSize(500, 200);
        setMinHeight(200);
        setMinWidth(500);

        setOnEsc(false);

        setUpButtons();

        con = new FlowLayoutContainer();
        con.getScrollSupport().setScrollMode(ScrollSupport.ScrollMode.AUTO);
        setupToolBar();
        addText();
        add(con);

        show();

    }

    private void setupToolBar() {
        ToolBar toolbar = new ToolBar();

        addButton.setIcon(appearance.addIcon());
        addButton.setText(appearance.add());
        addButton.addSelectHandler(new SelectEvent.SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                addText();
            }
        });

        deleteButton.setIcon(appearance.deleteIcon());
        deleteButton.setText(appearance.delete());
        deleteButton.addSelectHandler(new SelectEvent.SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                deleteText();
            }
        });

        toolbar.add(addButton);
        toolbar.add(deleteButton);

        con.add(toolbar);
    }

    private void addText() {
        FieldLabel rootIriLabel = new FieldLabel();
        TextField rootIriValue = new TextField();
        iriTextFields.add(rootIriValue);

        rootIriLabel.setText(appearance.rootIriLabel() + " #" + iriTextFields.size());
        rootIriValue.setEmptyText(appearance.enterIriEmptyText());
        rootIriValue.setWidth("400");
        rootIriValue.addValidator(new UrlValidator());
        rootIriLabel.add(rootIriValue);
        con.add(rootIriLabel);
    }

    private void deleteText() {
        if (iriTextFields.size() > 1) {
            TextField lastText = iriTextFields.get(iriTextFields.size() - 1);
            iriTextFields.remove(lastText);
            con.remove(con.getElement().getChildCount() - 1);
        }
    }

    private void setUpButtons() {
        setPredefinedButtons(PredefinedButton.OK, PredefinedButton.CANCEL);
        getOkButton().setText(appearance.saveHierarchy());
        getButton(PredefinedButton.CANCEL).addSelectHandler(new SelectEvent.SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                hide();
            }
        });
        getOkButton().addSelectHandler(new SelectEvent.SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                if (isValid()) {
                    List<String> iris = getIris();
                    handlers.fireEvent(new SaveOntologyHierarchyEvent(editedOntology, iris));
                    hide();
                }
            }
        });
    }

    private boolean isValid() {
        for (TextField field : iriTextFields) {
            if (!field.isValid()) return false;
        }
        return true;
    }

    public List<String> getIris() {
        List<String> iris = Lists.newArrayList();
        for (TextField field : iriTextFields) {
            if (!Strings.isNullOrEmpty(field.getCurrentValue())) {
                iris.add(field.getCurrentValue());
            }
        }
        return iris;
    }
}
