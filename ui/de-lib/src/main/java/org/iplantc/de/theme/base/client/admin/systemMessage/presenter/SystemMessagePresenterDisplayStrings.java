package org.iplantc.de.theme.base.client.admin.systemMessage.presenter;

import com.google.gwt.i18n.client.Messages;

/**
 * Created by jstroot on 1/16/15.
 * @author jstroot
 */
public interface SystemMessagePresenterDisplayStrings extends Messages {
    @Key("addSystemMessageSuccessMessage")
    String addSystemMessageSuccessMessage();

    @Key("deleteSystemMessageSuccessMessage")
    String deleteSystemMessageSuccessMessage();

    @Key("editSystemMessageSuccessMessage")
    String editSystemMessageSuccessMessage();
}
