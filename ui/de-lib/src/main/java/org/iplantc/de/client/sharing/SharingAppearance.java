package org.iplantc.de.client.sharing;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safecss.shared.SafeStyles;
import com.google.gwt.safehtml.shared.SafeHtml;

public interface SharingAppearance {
    String chooseFromCollab();

    int dataSharingDlgNameColumnWidth();

    ImageResource deleteIcon();

    String done();

    String explain();

    ImageResource folderIcon();

    String groupByData();

    String groupByUser();

    ImageResource helpIcon();

    String loadingMask();

    String manageSharing();

    String nameColumnLabel();

    int nameColumnWidth();

    String permissionsColumnLabel();

    SafeStyles permissionsColumnStyle();

    int permissionsColumnWidth();

    SafeStyles removeColumnStyle();

    String removeColumnTextClass();

    int removeColumnWidth();

    String selfShareWarning();

    int shareBreakDownDlgHeight();

    int shareBreakDownDlgNameColumnWidth();

    int shareBreakDownDlgPermissionColumnWidth();

    int shareBreakDownDlgToolbarHeight();

    int shareBreakDownDlgWidth();

    ImageResource shareIcon();

    SafeHtml sharePermissionsHelp();

    SafeHtml unshare();

    String variablePermissionsNotice();

    String warning();

    String whoHasAccess();

    String selectFilesFolders();
}
