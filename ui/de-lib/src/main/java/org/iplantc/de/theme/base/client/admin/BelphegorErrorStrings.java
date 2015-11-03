package org.iplantc.de.theme.base.client.admin;

import com.google.gwt.i18n.client.Messages;

/**
 * Created by jstroot on 1/15/15.
 * @author jstroot
 */
public interface BelphegorErrorStrings extends Messages{
    @Key("addAppCategoryError")
    String addAppCategoryError(String name);

    @Key("addCategoryPermissionError")
    String addCategoryPermissionError();

    @Key("deleteAppCategoryError")
    String deleteAppCategoryError(String name);

    @Key("deleteApplicationError")
    String deleteApplicationError(String name);

    @Key("deleteCategoryPermissionError")
    String deleteCategoryPermissionError();

    @Key("invalidAppNameMsg")
    String invalidAppNameMsg(String badStartChars, String badChars);

    @Key("invalidMoveCategoryMsg")
    String invalidMoveCategoryMsg();

    @Key("moveCategoryError")
    String moveCategoryError(String name);

    @Key("noCategoriesSelected")
    String noCategoriesSelected();

    @Key("renameCategoryError")
    String renameCategoryError(String name);

    @Key("restoreAppFailureMsg")
    String restoreAppFailureMsg(String name);

    @Key("restoreAppFailureMsgTitle")
    String restoreAppFailureMsgTitle();

    @Key("updateApplicationError")
    String updateApplicationError();

    String validDocError();
}
