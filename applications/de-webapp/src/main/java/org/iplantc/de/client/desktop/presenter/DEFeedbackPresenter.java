package org.iplantc.de.client.desktop.presenter;

import org.iplantc.de.client.desktop.views.DEFeedbackView;
import org.iplantc.de.client.desktop.views.DEFeedbackView.Presenter;
import org.iplantc.de.client.desktop.views.DEFeedbackViewImpl;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HasOneWidget;

import com.sencha.gxt.widget.core.client.box.AlertMessageBox;

public class DEFeedbackPresenter implements Presenter {

    DEFeedbackView view;
    private final Command callbackCommand;

    public DEFeedbackPresenter(Command callbackCommand) {
        this.callbackCommand = callbackCommand;
        view = new DEFeedbackViewImpl();
    }

    @Override
    public void go(HasOneWidget container) {
        container.setWidget(view);
    }

    @Override
    public void validateAndSubmit() {
        // System.out.println("-->" + view.validate());
        if (view.validate()) {
            // System.out.println("-->" + view.toJson());
//            ServicesInjector.INSTANCE.getDeFeedbackServiceFacade().submitFeedback(view.toJson().toString(), new AsyncCallback<String>() {
//
//                @Override
//                public void onSuccess(String result) {
//                    if (callbackCommand != null) {
//                        callbackCommand.execute();
//                    }
//                    IplantInfoBox info = new IplantInfoBox(I18N.DISPLAY.feedbackTitle(), I18N.DISPLAY
//                            .feedbackSubmitted());
//                    info.show();
//                }
//
//                @Override
//                public void onFailure(Throwable caught) {
//                    if (callbackCommand != null) {
//                        callbackCommand.execute();
//                    }
//                    ErrorHandler.post(I18N.ERROR.feedbackServiceFailure(), caught);
//
//                }
//            });
        } else {
            AlertMessageBox amb = new AlertMessageBox(I18N.DISPLAY.warning(),
                    I18N.DISPLAY.publicSubmitTip());
            amb.setModal(true);
            amb.show();
        }

    }
}
