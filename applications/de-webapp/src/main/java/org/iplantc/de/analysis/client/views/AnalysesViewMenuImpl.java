/**
 * 
 */
package org.iplantc.de.analysis.client.views;

import org.iplantc.de.analysis.shared.AnalysisModule;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiField;

import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.menu.MenuBarItem;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

/**
 * @author sriram
 * 
 */
public class AnalysesViewMenuImpl extends Composite implements AnalysesView.ViewMenu {

    private static AnalysesToolbarUiBinder uiBinder = GWT.create(AnalysesToolbarUiBinder.class);

    //@UiTemplate("AnalysesViewMenuImpl.ui.xml    interface AnalysesToolbarUiBinder extends UiBinder<Widget, AnalysesViewMenuImpl> {
    }


    @UiField
    MenuBarItem analysesMI;
    @UiField
    MenuBarItem editMI;
    @UiField
    MenuItem goToFolderMI;
    @UiField
    MenuItem viewParamsMI;
    @UiField
    MenuItem relaunchMI;

    @UiField
    MenuItem cancelMI;
    @UiField
    MenuItem deleteMI;
    @UiField
    MenuItem renameMI;
    @UiField
    MenuItem updateCommentsMI;

    public AnalysesViewMenuImpl() {
       initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    protected void onEnsureDebugId(String baseID) {
        super.onEnsureDebugId(baseID);
        // Analsis menu
        analysesMI.ensureDebugId(baseID + AnalysisModule.Ids.MENUITEM_ANALYSES);
        goToFolderMI.ensureDebugId(baseID + AnalysisModule.Ids.MENUITEM_ANALYSES + AnalysisModule.Ids.MENUITEM_GO_TO_FOLDER);
        viewParamsMI.ensureDebugId(baseID + AnalysisModule.Ids.MENUITEM_ANALYSES + AnalysisModule.Ids.MENUITEM_VIEW_PARAMS);
        relaunchMI.ensureDebugId(baseID + AnalysisModule.Ids.MENUITEM_ANALYSES + AnalysisModule.Ids.MENUITEM_RELAUNCH);
        cancelMI.ensureDebugId(baseID + AnalysisModule.Ids.MENUITEM_ANALYSES + AnalysisModule.Ids.MENUITEM_CANCEL);
        deleteMI.ensureDebugId(baseID + AnalysisModule.Ids.MENUITEM_ANALYSES + AnalysisModule.Ids.MENUITEM_DELETE);

        // Edit menu
        editMI.ensureDebugId(baseID + AnalysisModule.Ids.MENUITEM_EDIT);
        renameMI.ensureDebugId(baseID + AnalysisModule.Ids.MENUITEM_EDIT + AnalysisModule.Ids.MENUITEM_RENAME);
        updateCommentsMI.ensureDebugId(baseID + AnalysisModule.Ids.MENUITEM_EDIT + AnalysisModule.Ids.MENUITEM_UPDATE_COMMENTS);

    }

    @Override
    public void setDeleteButtonEnabled(boolean enabled) {

    }

    @Override
    public void setViewParamButtonEnabled(boolean enabled) {

    }

    @Override
    public void setCancelButtonEnabled(boolean enabled) {

    }

    @Override
    public void setRelaunchAnalysisEnabled(boolean enabled) {

    }

}
