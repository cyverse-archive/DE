/**
 * 
 */
package org.iplantc.de.analysis.client.views;

import static org.iplantc.de.client.models.analysis.AnalysisExecutionStatus.COMPLETED;
import static org.iplantc.de.client.models.analysis.AnalysisExecutionStatus.FAILED;
import static org.iplantc.de.client.models.analysis.AnalysisExecutionStatus.IDLE;
import static org.iplantc.de.client.models.analysis.AnalysisExecutionStatus.RUNNING;
import static org.iplantc.de.client.models.analysis.AnalysisExecutionStatus.SUBMITTED;

import org.iplantc.de.analysis.client.views.widget.AnalysisSearchField;
import org.iplantc.de.analysis.shared.AnalysisModule;
import org.iplantc.de.client.models.analysis.Analysis;
import org.iplantc.de.resources.client.IplantResources;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoader;
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.MenuItem;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

import java.util.List;

/**
 * @author sriram
 * 
 */
public class AnalysesViewMenuImpl extends Composite implements AnalysesView.ViewMenu {

    @UiTemplate("AnalysesViewMenuImpl.ui.xml")
    interface AnalysesToolbarUiBinder extends UiBinder<Widget, AnalysesViewMenuImpl> { }

    @UiField(provided = true)
    IplantResources icons;
    @UiField(provided = true)
    IplantDisplayStrings strings;

    @UiField
    ToolBar menuBar;
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
    TextButton analysesTb;
    @UiField
    MenuItem updateCommentsMI;
    @UiField
    MenuItem renameMI;
    @UiField
    TextButton editTb;
    @UiField
    TextButton refreshTb;

    private static AnalysesToolbarUiBinder uiBinder = GWT.create(AnalysesToolbarUiBinder.class);
    private AnalysesView parent;
    private PagingLoader<FilterPagingLoadConfig, PagingLoadResult<Analysis>> loader;
    private AnalysesView.Presenter presenter;
    private AnalysisSearchField searchField;

    @Inject
    public AnalysesViewMenuImpl(final IplantDisplayStrings displayStrings, final IplantResources resources) {
        this.strings = displayStrings;
        this.icons = resources;

        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public void filterByAnalysisId(String analysisId, String name) {
       searchField.filterByAnalysisId(analysisId, name);
    }

    @Override
    public void init(AnalysesView.Presenter presenter, AnalysesView parent, PagingLoader<FilterPagingLoadConfig, PagingLoadResult<Analysis>> loader){
        this.presenter = presenter;
        this.parent = parent;
        this.loader = loader;
        searchField = new AnalysisSearchField(loader);
        searchField.setEmptyText(strings.filterAnalysesList());
        menuBar.add(searchField);
    }

    @Override
    public void onSelectionChanged(SelectionChangedEvent<Analysis> event) {
        final List<Analysis> selection = event.getSelection();

        int size = selection.size();
        final boolean canCancelSelection = canCancelSelection(selection);
        final boolean canDeleteSelection = canDeleteSelection(selection);

        boolean goToFolderEnabled, viewParamsEnabled, relaunchEnabled, cancelEnabled, deleteEnabled;
        boolean renameEnabled, updateCommentsEnabled;
        switch (size) {
            case 0:
                goToFolderEnabled = false;
                viewParamsEnabled = false;
                relaunchEnabled = false;
                cancelEnabled = false;
                deleteEnabled = false;

                renameEnabled = false;
                updateCommentsEnabled = false;

                break;
            case 1:
                goToFolderEnabled = true;
                viewParamsEnabled = true;
                relaunchEnabled = !selection.get(0).isAppDisabled();
                cancelEnabled = canCancelSelection;
                deleteEnabled = canDeleteSelection;

                renameEnabled = true;
                updateCommentsEnabled = true;
                break;

            default:
                // If more than 1 is selected
                goToFolderEnabled = false;
                viewParamsEnabled = false;
                relaunchEnabled = false;
                cancelEnabled = canCancelSelection;
                deleteEnabled = canDeleteSelection;

                renameEnabled = false;
                updateCommentsEnabled = false;
        }

        goToFolderMI.setEnabled(goToFolderEnabled);
        viewParamsMI.setEnabled(viewParamsEnabled);
        relaunchMI.setEnabled(relaunchEnabled);
        cancelMI.setEnabled(cancelEnabled);
        deleteMI.setEnabled(deleteEnabled);

        renameMI.setEnabled(renameEnabled);
        updateCommentsMI.setEnabled(updateCommentsEnabled);
    }

    @Override
    protected void onEnsureDebugId(String baseID) {
        super.onEnsureDebugId(baseID);
        // Analsis menu
        analysesTb.ensureDebugId(baseID + AnalysisModule.Ids.MENUITEM_ANALYSES);
        goToFolderMI.ensureDebugId(baseID + AnalysisModule.Ids.MENUITEM_ANALYSES + AnalysisModule.Ids.MENUITEM_GO_TO_FOLDER);
        viewParamsMI.ensureDebugId(baseID + AnalysisModule.Ids.MENUITEM_ANALYSES + AnalysisModule.Ids.MENUITEM_VIEW_PARAMS);
        relaunchMI.ensureDebugId(baseID + AnalysisModule.Ids.MENUITEM_ANALYSES + AnalysisModule.Ids.MENUITEM_RELAUNCH);
        cancelMI.ensureDebugId(baseID + AnalysisModule.Ids.MENUITEM_ANALYSES + AnalysisModule.Ids.MENUITEM_CANCEL);
        deleteMI.ensureDebugId(baseID + AnalysisModule.Ids.MENUITEM_ANALYSES + AnalysisModule.Ids.MENUITEM_DELETE);

        // Edit menu
        editTb.ensureDebugId(baseID + AnalysisModule.Ids.MENUITEM_EDIT);
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

    @UiHandler("refreshTb")
    void onRefreshSelected(SelectEvent event) {
        presenter.loadAnalyses();
    }

}
