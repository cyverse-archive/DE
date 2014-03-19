package org.iplantc.de.commons.client.collaborators.views;

import org.iplantc.de.client.models.collaborators.Collaborator;
import org.iplantc.de.commons.client.collaborators.presenter.ManageCollaboratorsPresenter;
import org.iplantc.de.commons.client.collaborators.presenter.ManageCollaboratorsPresenter.MODE;
import org.iplantc.de.commons.client.collaborators.views.ManageCollaboratorsView.Presenter;
import org.iplantc.de.commons.client.views.gxt3.dialogs.IPlantDialog;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.HTML;

import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.grid.CheckBoxSelectionModel;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sriram
 *
 */
public class ManageCollaboratorsDailog extends IPlantDialog {

    private CheckBoxSelectionModel<Collaborator> checkBoxModel;
    private CollaboratorProperties properties;
    private final Presenter p;

    public ManageCollaboratorsDailog(MODE mode) {
        super(true);
        initDialog();
        ListStore<Collaborator> store = new ListStore<Collaborator>(new CollaboratorKeyProvider());
        ColumnModel<Collaborator> cm = buildColumnModel();
        ManageCollaboratorsView view = new ManageCollaboratorsViewImpl(checkBoxModel, cm, store, mode);
        p = new ManageCollaboratorsPresenter(view);
        p.go(this);
    }



    private void initDialog() {
        properties = GWT.create(CollaboratorProperties.class);
        setPredefinedButtons(PredefinedButton.OK);
        setHeadingText(I18N.DISPLAY.collaborators());
        addHelp(new HTML(I18N.HELP.collaboratorsHelp()));
        setPixelSize(450, 400);
        addOkButtonHandler();
        setHideOnButtonClick(true);
    }

    private void addOkButtonHandler() {
        addOkButtonSelectHandler(new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                hide();
            }
        });
    }

    @Override
    protected void onHide() {
        p.cleanup();
        super.onHide();
    }

    private ColumnModel<Collaborator> buildColumnModel() {

        List<ColumnConfig<Collaborator, ?>> configs = new ArrayList<ColumnConfig<Collaborator, ?>>();
        IdentityValueProvider<Collaborator> valueProvider = new IdentityValueProvider<Collaborator>();

        checkBoxModel = new CheckBoxSelectionModel<Collaborator>(valueProvider);

        ColumnConfig<Collaborator, Collaborator> colCheckBox = checkBoxModel.getColumn();
        configs.add(colCheckBox);

        ColumnConfig<Collaborator, Collaborator> name = new ColumnConfig<Collaborator, Collaborator>(
                valueProvider, 150);
        name.setHeader(I18N.DISPLAY.name());
        name.setCell(new AbstractCell<Collaborator>() {

            @Override
            public void render(com.google.gwt.cell.client.Cell.Context context, Collaborator value,
                    SafeHtmlBuilder sb) {
                StringBuilder builder = new StringBuilder();
                if (value.getFirstName() != null && !value.getFirstName().isEmpty()) {
                    builder.append(value.getFirstName());
                    if (value.getLastName() != null && !value.getLastName().isEmpty()) {
                        builder.append(" " + value.getLastName());
                    }
                    sb.appendEscaped(builder.toString());
                } else {
                    sb.appendEscaped(value.getUserName());
                }

            }
        });
        configs.add(name);

        ColumnConfig<Collaborator, String> ins = new ColumnConfig<Collaborator, String>(
                properties.institution(), 150);
        ins.setHeader(I18N.DISPLAY.institution());
        configs.add(ins);
        return new ColumnModel<Collaborator>(configs);

    }

    public List<Collaborator> getSelectedCollaborators() {
        return p.getSelectedCollaborators();
    }

}
