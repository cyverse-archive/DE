package org.iplantc.de.admin.desktop.client.permIdRequest.views;

import org.iplantc.de.admin.desktop.shared.Belphegor;
import org.iplantc.de.client.models.identifiers.PermanentIdRequestDetails;
import org.iplantc.de.client.models.identifiers.PermanentIdRequestAutoBeanFactory;
import org.iplantc.de.client.models.identifiers.PermanentIdRequestStatus;
import org.iplantc.de.client.models.identifiers.PermanentIdRequestUpdate;
import org.iplantc.de.commons.client.views.dialogs.IPlantDialog;
import org.iplantc.de.commons.client.widgets.IPlantAnchor;
import org.iplantc.de.notifications.client.views.dialogs.RequestHistoryDialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.form.SimpleComboBox;
import com.sencha.gxt.widget.core.client.form.TextArea;

/**
 * 
 * @author sriram
 * 
 */
public class UpdatePermanentIdRequestDialog extends IPlantDialog {

    private static UpdatePermanentIdRequestUiBinder uiBinder = GWT.create(UpdatePermanentIdRequestUiBinder.class);

    @UiTemplate("UpdatePermanentIdRequest.ui.xml")
    interface UpdatePermanentIdRequestUiBinder extends UiBinder<Widget, UpdatePermanentIdRequestDialog> {
    }

    @UiField
    IPlantAnchor currentStatusLabel;
    @UiField
    Label userEmail;
    @UiField
    SimpleComboBox<PermanentIdRequestStatus> statusCombo;
    @UiField
    TextArea commentsEditor;

    private final PermanentIdRequestAutoBeanFactory factory;

    public UpdatePermanentIdRequestDialog(String curr_status,
                                          final PermanentIdRequestDetails details,
                                          PermanentIdRequestAutoBeanFactory factory) {
        this.factory = factory;
        add(uiBinder.createAndBindUi(this));
        currentStatusLabel.setText(curr_status);
        currentStatusLabel.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                RequestHistoryDialog dlg = new RequestHistoryDialog(
                        details.getRequestor().getUsername() + "-" + details.getType(),
                        details.getHistory());
                dlg.show();
            }
        });
        userEmail.setText(details.getRequestor().getEmail());

        ensureDebugId(Belphegor.PermIds.UPDATE_PERMID_DIALOG + Belphegor.PermIds.VIEW);
    }

    @UiFactory
    SimpleComboBox<PermanentIdRequestStatus> createComboBox() {
        SimpleComboBox<PermanentIdRequestStatus> cb = new SimpleComboBox<>(new LabelProvider<PermanentIdRequestStatus>() {

            @Override
            public String getLabel(PermanentIdRequestStatus item) {
                return item.name();
            }
        });

        cb.add(PermanentIdRequestStatus.Submitted);
        cb.add(PermanentIdRequestStatus.Pending);
        cb.add(PermanentIdRequestStatus.Evaluation);
        cb.add(PermanentIdRequestStatus.Approved);
        cb.add(PermanentIdRequestStatus.Completion);
        cb.add(PermanentIdRequestStatus.Failed);
        cb.setAllowBlank(false);
        cb.setEditable(false);
        cb.setTriggerAction(TriggerAction.ALL);
        return cb;
    }

    public PermanentIdRequestUpdate getPermanentIdRequestUpdate() {
        PermanentIdRequestUpdate update = factory.getStatus().as();
        update.setStatus(statusCombo.getCurrentValue().toString());
        update.setComments(commentsEditor.getCurrentValue());
        return update;
    }

    @Override
    public void show() {
        super.show();
        ensureDebugId(Belphegor.PermIds.UPDATE_PERMID_DIALOG);
    }

    @Override
    protected void onEnsureDebugId(String baseID) {
        super.onEnsureDebugId(baseID);

        getButton(Dialog.PredefinedButton.OK).ensureDebugId(baseID + Belphegor.PermIds.UPDATE);
        currentStatusLabel.setId(baseID + Belphegor.PermIds.CURRENT_STATUS);
        userEmail.ensureDebugId(baseID + Belphegor.PermIds.USER_EMAIL);
        statusCombo.setId(baseID + Belphegor.PermIds.STATUS_COMBO);
        commentsEditor.setId(baseID + Belphegor.PermIds.COMMENTS);
    }
}
