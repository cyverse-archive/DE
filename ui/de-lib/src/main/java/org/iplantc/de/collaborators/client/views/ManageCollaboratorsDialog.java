package org.iplantc.de.collaborators.client.views;

import org.iplantc.de.client.models.collaborators.Collaborator;
import org.iplantc.de.collaborators.client.presenter.ManageCollaboratorsPresenter;
import org.iplantc.de.collaborators.client.views.ManageCollaboratorsView.MODE;
import org.iplantc.de.collaborators.shared.CollaboratorsModule;
import org.iplantc.de.commons.client.views.dialogs.IPlantDialog;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.ui.HTML;

import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

import java.util.List;

/**
 * @author sriram, jstroot
 * 
 */
public class ManageCollaboratorsDialog extends IPlantDialog {


    private final ManageCollaboratorsView.Presenter p;
    private final ManageCollaboratorsView.Appearance appearance;


    public ManageCollaboratorsDialog(final MODE mode) {
        this(mode,
             GWT.<ManageCollaboratorsView.Appearance>create(ManageCollaboratorsView.Appearance.class));
    }

    ManageCollaboratorsDialog(final MODE mode,
                              final ManageCollaboratorsView.Appearance appearance) {
        super(true);
        this.appearance = appearance;
        initDialog();
        ListStore<Collaborator> store = new ListStore<>(new CollaboratorKeyProvider());
        ManageCollaboratorsView view = new ManageCollaboratorsViewImpl(store, mode);
        p = new ManageCollaboratorsPresenter(view);
        p.go(this);
    }

    @Override
    protected void onEnsureDebugId(String baseID) {
        super.onEnsureDebugId(baseID);
        getWidget().ensureDebugId(baseID + CollaboratorsModule.Ids.VIEW);
        getOkButton().ensureDebugId(baseID + CollaboratorsModule.Ids.OK);
    }

    private void initDialog() {
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

    @Override
    public void show() {
        super.show();

        ensureDebugId(CollaboratorsModule.Ids.DIALOG);
    }

    public List<Collaborator> getSelectedCollaborators() {
        return p.getSelectedCollaborators();
    }

}
