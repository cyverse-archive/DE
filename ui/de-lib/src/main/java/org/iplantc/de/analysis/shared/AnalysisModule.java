package org.iplantc.de.analysis.shared;

/**
 * @author jstroot
 */
public interface AnalysisModule {

    interface Ids {
        String ANALYSES_VIEW = ".analysesView";
        String MENUBAR = ".menuBar";

        String MENUITEM_ANALYSES = ".analyses";
        String MENUITEM_GO_TO_FOLDER = ".goToFolder";
        String MENUITEM_VIEW_PARAMS = ".viewParams";
        String MENUITEM_VIEW_ANALYSES_INFO = ".viewInfo";
        String MENUITEM_RELAUNCH = ".relaunch";
        String MENUITEM_DELETE = ".delete";
        String MENUITEM_CANCEL = ".cancel";

        String MENUITEM_EDIT = ".edit";
        String MENUITEM_RENAME = ".rename";
        String MENUITEM_UPDATE_COMMENTS = ".updateComments";

        String VIEW_PARAMS = ".viewParamsView";
        String BUTTON_REFRESH = ".refresh";

        String FIELD_SEARCH = ".search";

        String SHARE_MENU =".share";
        String SHARE_COLLAB = ".sharecollab";
        String SHARE_SUPPORT =".sharesupport";
    }
}
