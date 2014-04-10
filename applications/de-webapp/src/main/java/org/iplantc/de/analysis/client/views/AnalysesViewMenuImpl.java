/**
 * 
 */
package org.iplantc.de.analysis.client.views;

import static org.iplantc.de.client.models.analysis.AnalysisExecutionStatus.*;
import org.iplantc.de.analysis.shared.AnalysisModule;
import org.iplantc.de.client.models.analysis.Analysis;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;

import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.MenuBarItem;
import com.sencha.gxt.widget.core.client.menu.MenuItem;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;

import java.util.List;

/**
 * @author sriram
 * 
 */
public class AnalysesViewMenuImpl extends Composite implements AnalysesView.ViewMenu {

    @UiTemplate("AnalysesViewMenuImpl.ui.xml")
    interface AnalysesToolbarUiBinder extends UiBinder<Widget, AnalysesViewMenuImpl> { }
    @UiField
    MenuBarItem analysesMI;
    @UiField
    MenuItem cancelMI;
    @UiField
    MenuItem deleteMI;
    @UiField
    MenuBarItem editMI;
    @UiField
    MenuItem goToFolderMI;
    @UiField
    MenuItem relaunchMI;
    @UiField
    MenuItem renameMI;
    @UiField
    MenuItem updateCommentsMI;
    @UiField
    MenuItem viewParamsMI;
    private static AnalysesToolbarUiBinder uiBinder = GWT.create(AnalysesToolbarUiBinder.class);
    private AnalysesView.Presenter presenter;
    private AnalysesView parent;

    public AnalysesViewMenuImpl() {
       initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public void onSelectionChanged(SelectionChangedEvent<Analysis> event) {
        final List<Analysis> selection = event.getSelection();

        int size = selection.size();
        switch (size) {
            case 0:
                goToFolderMI.setEnabled(false);
                viewParamsMI.setEnabled(false);
                relaunchMI.setEnabled(false);
                cancelMI.setEnabled(false);
                deleteMI.setEnabled(false);

                renameMI.setEnabled(false);
                updateCommentsMI.setEnabled(false);
                break;
            case 1:
                goToFolderMI.setEnabled(true);
                viewParamsMI.setEnabled(true);
                relaunchMI.setEnabled(!selection.get(0).isAppDisabled());
                cancelMI.setEnabled(canCancelSelection(selection));
                deleteMI.setEnabled(true);

                renameMI.setEnabled(false);
                updateCommentsMI.setEnabled(false);
                break;

            default:
                // If more than 1 is selected
                goToFolderMI.setEnabled(false);
                viewParamsMI.setEnabled(false);
                relaunchMI.setEnabled(false);
                cancelMI.setEnabled(canCancelSelection(selection));
                deleteMI.setEnabled(true);

                renameMI.setEnabled(false);
                updateCommentsMI.setEnabled(false);
        }
    }

    @Override
    public void init(AnalysesView.Presenter presenter, AnalysesView parent) {
        this.presenter = presenter;
        this.parent = parent;
    }

    @UiHandler("goToFolderMI")
    void onGoToFolderSelected(SelectionEvent<Item> event){
        presenter.goToSelectedAnalysisFolder();
    }
    @UiHandler("viewParamsMI")
    void onViewParamsSelected(SelectionEvent<Item> event){
        parent.viewParams();
    }
    @UiHandler("relaunchMI")
    void onRelaunchSelected(SelectionEvent<Item> event){
        presenter.relaunchSelectedAnalysis();
    }
    @UiHandler("cancelMI")
    void onCancelSelected(SelectionEvent<Item> event){
        presenter.cancelSelectedAnalyses();
    }
    @UiHandler("deleteMI")
    void onDeleteSelected(SelectionEvent<Item> event){
        presenter.deleteSelectedAnalyses();
    }
    @UiHandler("renameMI")
    void onRenameSelected(SelectionEvent<Item> event){
        presenter.renameSelectedAnalysis();
    }
    @UiHandler("updateCommentsMI")
    void onUpdateCommentsSelected(SelectionEvent<Item> event){
        parent.updateComments();
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

    boolean canCancelSelection(final List<Analysis> selection){
        for(Analysis ae : selection){
            if(ae == null)
                continue;

            if(SUBMITTED.toString().equalsIgnoreCase(ae.getStatus())
                    || IDLE.toString().equalsIgnoreCase(ae.getStatus())
                    || RUNNING.toString().equalsIgnoreCase(ae.getStatus())){
                return true;
            }
        }
        return false;
    }

}
