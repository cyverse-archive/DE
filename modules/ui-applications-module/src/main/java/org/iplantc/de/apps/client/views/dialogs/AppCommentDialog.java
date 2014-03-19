package org.iplantc.de.apps.client.views.dialogs;


import org.iplantc.de.commons.client.views.gxt3.dialogs.IPlantDialog;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.TextArea;

/**
 * A simple dialog that lets the user enter a comment on an app. The dialog is initially disabled and can
 * be enabled via unmaskDialog().
 *
 * @author sriram
 *
 */

public class AppCommentDialog extends IPlantDialog {
    private TextArea textArea;
    private Command onConfirm;

    /**
     * Creates a new AppCommentDialog with no on-confirm command set.
     *
     * @param appName name of the app
     * @param commentId the Confluence ID when an existing comment, or null for a new comment
     * @param comment the comment pointed to by commentId, or null if no comment exists yet
     */
    public AppCommentDialog(String appName) {
        init(appName);
    }

    private void init(String appName) {
        setHeadingText(I18N.DISPLAY.appCommentDialogTitle());
        setPredefinedButtons(PredefinedButton.OK, PredefinedButton.CANCEL);
        setSize("400", "250");
        setResizable(false);

        textArea = new TextArea();
        textArea.setSize("385", "164");
        setFocusWidget(textArea);
        compose(appName);
        maskDialog();

        setHideOnButtonClick(true);

        // call onConfirm on OK
        addOkButtonSelectHandler(new SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				onConfirm.execute();

			}
		});

        setModal(true);
    }

    /**
     * Sets the comment text shown in the text area.
     *
     * @param text a comment
     */
    public void setText(String text) {
        textArea.setValue(text);
    }

    /**
     * Disables the dialog.
     */
    public void maskDialog() {
        textArea.mask(I18N.DISPLAY.loadingMask());
       getOkButton().disable();
    }

    /**
     * Enables the dialog.
     */
    public void unmaskDialog() {
        textArea.unmask();
        getOkButton().enable();
    }

    private void compose(String appName) {
        VerticalPanel pnl = new VerticalPanel();
        pnl.add(new Label(I18N.DISPLAY.appCommentExplanation(appName)));
        HorizontalLayoutContainer lc = new HorizontalLayoutContainer();
        lc.add(textArea);
        pnl.add(lc);
        add(pnl);
    }

    /**
     * Sets a command to run when the OK button is clicked.
     *
     * @param onConfirm
     */
    public void setCommand(Command onConfirm) {
        this.onConfirm = onConfirm;
    }

    /**
     * Returns the comment entered by the user if the dialog was closed with the OK button. If the Cancel
     * button was clicked, the return value is undefined.
     *
     * @return
     */
    public String getComment() {
        return textArea.getValue();
    }
}
