package org.iplantc.de.theme.base.client.diskResource.sharing;

import com.google.gwt.i18n.client.Messages;
import com.google.gwt.safehtml.shared.SafeHtml;

/**
 * @author jstroot
 */
public interface SharingMessages extends Messages {
    String chooseFromCollab();

    String explain();

    String groupByData();

    String manageSharing();

    String selectFilesFolders();

    String selfShareWarning();

    SafeHtml sharePermissionsHelp();

    String sharingCompleteMsg();

    SafeHtml unshare();

    String variablePermissionsNotice();

    String whoHasAccess();

    String groupByUser();
}
