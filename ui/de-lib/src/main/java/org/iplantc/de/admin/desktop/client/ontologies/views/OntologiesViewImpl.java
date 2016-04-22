package org.iplantc.de.admin.desktop.client.ontologies.views;

import org.iplantc.de.admin.desktop.client.ontologies.OntologiesView;
import org.iplantc.de.admin.desktop.client.ontologies.events.ViewOntologyVersionEvent;
import org.iplantc.de.admin.desktop.client.ontologies.views.dialogs.OntologyListDialog;
import org.iplantc.de.client.models.ontologies.Ontology;
import org.iplantc.de.commons.client.ErrorHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.inject.client.AsyncProvider;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;

import java.util.List;

/**
 * @author aramsey
 */
public class OntologiesViewImpl extends Composite implements OntologiesView {

    interface OntologiesViewImplUiBinder extends UiBinder<Widget, OntologiesViewImpl> {

    }

    private static OntologiesViewImplUiBinder uiBinder = GWT.create(OntologiesViewImplUiBinder.class);

    @UiField TextButton addButton;
    @UiField TextButton viewVersions;
    @UiField(provided = true) OntologiesViewAppearance appearance;

    @Inject AsyncProvider<OntologyListDialog> listDialog;

    @Inject
    public OntologiesViewImpl(OntologiesViewAppearance appearance) {
        this.appearance = appearance;

        initWidget(uiBinder.createAndBindUi(this));

    }

    @Override
    public HandlerRegistration addViewOntologyVersionEventHandler(ViewOntologyVersionEvent.ViewOntologyVersionEventHandler handler) {
        return addHandler(handler, ViewOntologyVersionEvent.TYPE);
    }

    @Override
    public void showOntologyVersions(final List<Ontology> ontologies) {
        listDialog.get(new AsyncCallback<OntologyListDialog>() {
            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(caught);
            }

            @Override
            public void onSuccess(OntologyListDialog result) {
                result.show(ontologies);
            }
        });
    }

    @UiHandler("viewVersions")
    void addButtonClicked(SelectEvent event) {
        fireEvent(new ViewOntologyVersionEvent());
    }

}
