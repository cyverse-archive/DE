package org.iplantc.de.systemMessages.client.view;

import org.iplantc.de.commons.client.views.dialogs.IPlantDialog;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Label;

/**
 * This dialog verifies that the user really does want to dismiss a message.
 */
final class DismissalDialog extends IPlantDialog {

    private final Command dismiss;

    /**
     * the constructor
     * 
     * @param dismiss the command to execute upon verification
     */
    DismissalDialog(final Command dismiss) {
        setHeadingText(I18N.DISPLAY.confirmAction());
        this.dismiss = dismiss;
        setWidget(new Label(I18N.DISPLAY.messageDismissQuery()));
    }

    /**
     * @see IPlantDialog#onOkButtonClicked()
     */
    @Override
    protected void onOkButtonClicked() {
        super.onOkButtonClicked();
        dismiss.execute();
    }

}
