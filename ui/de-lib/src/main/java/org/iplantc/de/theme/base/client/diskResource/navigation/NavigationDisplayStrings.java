package org.iplantc.de.theme.base.client.diskResource.navigation;

import com.google.gwt.i18n.client.Messages;

/**
 * Created by jstroot on 1/21/15.
 * @author jstroot
 */
public interface NavigationDisplayStrings extends Messages {
    @Key("headingText")
    String headingText();

    String retrieveFolderInfoFailed();

    String savedFiltersRetrievalFailure();
}
