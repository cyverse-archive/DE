package org.iplantc.de.client.desktop.views;

import org.iplantc.de.client.desktop.presenter.DEFeedbackPresenter;
import org.iplantc.de.client.desktop.views.DEFeedbackView.Presenter;
import org.iplantc.de.commons.client.views.gxt3.dialogs.IPlantDialog;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.gwt.user.client.Command;

import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

public class DEFeedbackDialog extends IPlantDialog {

    public DEFeedbackDialog() {
        setHeadingText(I18N.DISPLAY.feedbackTitle());
        setSize("400", "500");
        setHideOnButtonClick(false);
        setPredefinedButtons(PredefinedButton.OK, PredefinedButton.CANCEL);
        setOkButtonText(I18N.DISPLAY.submit());
        final Presenter p = new DEFeedbackPresenter(new Command() {

            @Override
            public void execute() {
                hide();

            }
        });
        p.go(this);

        addOkButtonSelectHandler(new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                p.validateAndSubmit();
            }
        });
        addCancelButtonSelectHandler(new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                hide();

            }
        });
        setAutoHide(false);

    }
}
