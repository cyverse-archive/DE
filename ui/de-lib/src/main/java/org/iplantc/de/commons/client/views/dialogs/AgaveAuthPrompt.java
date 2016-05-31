package org.iplantc.de.commons.client.views.dialogs;

import org.iplantc.de.client.models.UserInfo;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;

import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent;

/**
 * @author aramsey
 */
public class AgaveAuthPrompt extends ConfirmMessageBox {

    public interface AgaveAuthAppearance {

        String agaveRedirectTitle();

        String agaveRedirectMessage();
    }

    static AgaveAuthAppearance appearance = GWT.create(AgaveAuthAppearance.class);
    UserInfo userInfo;

    public AgaveAuthPrompt(String title, String message) {
        super(title, message);

        this.userInfo = UserInfo.getInstance();
        addDialogHideHandler(new DialogHideEvent.DialogHideHandler() {
            @Override
            public void onDialogHide(DialogHideEvent event) {
                if (event.getHideButton() == Dialog.PredefinedButton.YES) {
                    Window.Location.replace(userInfo.getAgaveRedirect());
                }
            }
        });

        setWidth(500);
    }

    public AgaveAuthPrompt() {
        this(appearance.agaveRedirectTitle(), appearance.agaveRedirectMessage());
    }
}
