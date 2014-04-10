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
    private AnalysesView parent;
    private AnalysesView.Presenter presenter;

    public AnalysesViewMenuImpl() {
       initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public void init(AnalysesView.Presenter presenter, AnalysesView parent) {
        this.presenter = presenter;
        this.parent = parent;
    }

    @Override
    public void onSelectionChanged(SelectionChangedEvent<Analysis> event) {
        final List<Analysis> selection = event.getSelection();

        int size = selection.size();
        final boolean canCancelSelection = canCancelSelection(selection);
        final boolean canDeleteSelection = canDeleteSelection(selection);
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
                cancelMI.setEnabled(canCancelSelection);
                deleteMI.setEnabled(canDeleteSelection);

                renameMI.setEnabled(true);
                updateCommentsMI.setEnabled(true);
                break;

            default:
                // If more than 1 is selected
                goToFolderMI.setEnabled(false);
                viewParamsMI.setEnabled(false);
                relaunchMI.setEnabled(false);

                cancelMI.setEnabled(canCancelSelection);
                deleteMI.setEnabled(canDeleteSelection);

                renameMI.setEnabled(false);
                updateCommentsMI.setEnabled(false);
        }

        if(canCancelSelection){
           // If we can cancel selection, clear tooltip
            cancelMI.setToolTip("");
        } else {
            // Tell them why they can't cancel
            cancelMI.setToolTip("Can only cancel analyses whose status is either " + SUBMITTED + ", " + IDLE + ", or " + RUNNING);
        }

        if(canDeleteSelection){
            // If we can delete selection, clear tooltip
            deleteMI.setToolTip("");
        } else {
            // Tell them why they can't delete
            deleteMI.setToolTip("Can only delete analyses whose status is either " + FAILED + " or " + COMPLETED);
        }
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

    /**
     * Determines if the cancel button should be enable for the given selection.
     *
     *
     * @param selection
     * @return true if the selection contains ANY status which is SUBMITTED, IDLE, or RUNNING; false otherwise.
     */
    boolean canCancelSelection(final List<Analysis> selection){
        for(Analysis ae : selection){
            if(ae == null)
                continue;

            final String status = ae.getStatus();
            if(SUBMITTED.toString().equalsIgnoreCase(status)
                    || IDLE.toString().equalsIgnoreCase(status)
                    || RUNNING.toString().equalsIgnoreCase(status)){
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if the delete button should be enabled for the given selection.
     *
     * @param selection
     * @return true if the selection ONLY contains FAILED or COMPLETED status, false otherwise.
     */
    boolean canDeleteSelection(List<Analysis> selection) {
        for(Analysis ae : selection){
            if(ae == null)
                continue;

            final String status = ae.getStatus();
            if(!(FAILED.toString().equalsIgnoreCase(status)
                    || COMPLETED.toString().equalsIgnoreCase(status))){
                return false;
            }

        }
        return true;
    }

    @UiHandler("cancelMI")
    void onCancelSelected(SelectionEvent<Item> event){
        presenter.cancelSelectedAnalyses();
    }

    @UiHandler("deleteMI")
    void onDeleteSelected(SelectionEvent<Item> event){
        presenter.deleteSelectedAnalyses();
    }

    @UiHandler("goToFolderMI")
    void onGoToFolderSelected(SelectionEvent<Item> event){
        presenter.goToSelectedAnalysisFolder();
    }

    @UiHandler("relaunchMI")
    void onRelaunchSelected(SelectionEvent<Item> event){
        presenter.relaunchSelectedAnalysis();
    }

    @UiHandler("renameMI")
    void onRenameSelected(SelectionEvent<Item> event){
        presenter.renameSelectedAnalysis();
    }

    @UiHandler("updateCommentsMI")
    void onUpdateCommentsSelected(SelectionEvent<Item> event){
        presenter.updateComments();
    }

    @UiHandler("viewParamsMI")
    void onViewParamsSelected(SelectionEvent<Item> event){
        parent.viewParams();
    }

}
