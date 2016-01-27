package org.iplantc.de.theme.base.client.admin;

import com.google.gwt.i18n.client.Messages;

import java.util.List;

/**
 * Created by jstroot on 1/15/15.
 * @author jstroot
 */
public interface BelphegorDisplayStrings extends Messages {
    @Key("addCategoryPrompt")
    String addCategoryPrompt();

    @DefaultMessage("Successfully added {0} to the following Categories: {1,list}.")
    @AlternateMessage({"=1", "Successfully added {0} to the following Category: {1,list}."})
    @Key("appCategorizeSuccess")
    String appCategorizeSuccess(String name, @PluralCount List<String> groupNames);

    @Key("appDescription")
    String appDescription();

    String appDocumentationLabel();

    @Key("avgUserRatingColumnLabel")
    String avgUserRatingColumnLabel();

    @Key("categorize")
    String categorize();

    @Key("clearSelection")
    String clearSelection();

    @Key("confirmDeleteAppCategory")
    String confirmDeleteAppCategory(String name);

    @Key("confirmDeleteAppTitle")
    String confirmDeleteAppTitle();

    @Key("deleteApp")
    String deleteApp();

    @Key("deleteCategory")
    String deleteCategory();

    String docHelpHtml();

    @Key("editApp")
    String editApp();

    @Key("moveCategory")
    String moveCategory();

    @Key("referenceGenomes")
    String referenceGenomes();

    @Key("renamePrompt")
    String renamePrompt();

    @Key("restoreApp")
    String restoreApp();

    @Key("restoreAppSuccessMsg")
    String restoreAppSuccessMsg(String name, String s);

    @Key("restoreAppSuccessMsgTitle")
    String restoreAppSuccessMsgTitle();

    @Key("selectCategories")
    String selectCategories(String name);

    @Key("systemMessages")
    String systemMessages();

    @Key("tempDisable")
    String tempDisable();

    String templateLinkPopupHeading();

    String templateLinkTitle();

    @Key("toolRequests")
    String toolRequests();

    @Key("toolAdmin")
    String toolAdmin();

    String updateDocumentationSuccess();
}
