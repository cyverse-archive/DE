package org.iplantc.de.admin.desktop.client.permIdRequest.view;

import org.iplantc.de.client.models.identifiers.PermanentIdRequest;
import org.iplantc.de.client.models.identifiers.PermanentIdRequestAutoBeanFactory;
import org.iplantc.de.client.models.identifiers.PermanentIdRequestStatus;
import org.iplantc.de.client.models.identifiers.PermanentIdRequestUpdate;
import org.iplantc.de.commons.client.views.dialogs.IPlantDialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.widget.core.client.box.MessageBox;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent.DialogHideHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
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
    Label currentStatusLabel;
    @UiField
    SimpleComboBox<PermanentIdRequestStatus> statusCombo;
    @UiField
    TextArea commentsEditor;
    private final PermanentIdRequestAutoBeanFactory factory;
    @SuppressWarnings("unused")
    private final PermanentIdRequest request;
    @SuppressWarnings("unused")
    private final PermIdRequestView.Presenter presenter;

    public UpdatePermanentIdRequestDialog(final PermanentIdRequest request,
                                          final PermIdRequestView.Presenter presenter,
                                          final PermanentIdRequestAutoBeanFactory factory) {

        this.factory = factory;
        this.request = request;
        this.presenter = presenter;
        setHeadingText("Update Status");
        getOkButton().setText("Submit");
        add(uiBinder.createAndBindUi(this));
        currentStatusLabel.setText(request.getStatus());
        commentsEditor.setHeight(200);
        setHideOnButtonClick(false);
        getOkButton().addSelectHandler(new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                if (statusCombo.getValue() != null
                        && statusCombo.getValue().equals(PermanentIdRequestStatus.Approved)) {
                    final MessageBox amb = new MessageBox("Request " + request.getType(),
                                                          "Do you want to submit request for "
                                                                  + request.getType() + " now?");
                    amb.setPredefinedButtons(PredefinedButton.YES,
                                             PredefinedButton.NO,
                                             PredefinedButton.CANCEL);
                    amb.setIcon(MessageBox.ICONS.question());
                    amb.addDialogHideHandler(new DialogHideHandler() {
                        @SuppressWarnings("incomplete-switch")
                        @Override
                        public void onDialogHide(DialogHideEvent event) {
                            PermanentIdRequestUpdate update = getPermanentIdRequestUpdate();
                            switch (event.getHideButton()) {
                                case YES:
                                    presenter.setSubmitRequestForId();
                                    presenter.updateRequest(update);
                                    UpdatePermanentIdRequestDialog.this.hide();
                                    break;
                                case NO:
                                    presenter.updateRequest(update);
                                    UpdatePermanentIdRequestDialog.this.hide();
                                    break;
                                case CANCEL:
                                    presenter.updateRequest(update);
                                    UpdatePermanentIdRequestDialog.this.hide();
                                    break;
                            }
                        }
                    });
                    amb.show();
                }
            }
        });
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

}
