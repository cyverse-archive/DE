package org.iplantc.de.theme.base.client.admin.workshopAdmin;

import com.google.gwt.i18n.client.LocalizableResource;
import com.google.gwt.i18n.client.Messages;

/**
 * @author dennis
 */
public interface WorkshopAdminDisplayStrings extends Messages {

    @LocalizableResource.Key("delete")
    String delete();

    @LocalizableResource.Key("nameColumnLabel")
    String nameColumnLabel();

    @LocalizableResource.Key("emailColumnLabel")
    String emailColumnLabel();

    @LocalizableResource.Key("institutionColumnLabel")
    String institutionColumnLabel();
}
