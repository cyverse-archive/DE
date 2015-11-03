package org.iplantc.de.analysis.client.views;

import static org.iplantc.de.client.models.analysis.AnalysisExecutionStatus.*;

import org.iplantc.de.analysis.client.AnalysesView;
import org.iplantc.de.analysis.client.AnalysisToolBarView;
import org.iplantc.de.analysis.client.views.dialogs.AnalysisCommentsDialog;
import org.iplantc.de.analysis.client.views.dialogs.AnalysisParametersDialog;
import org.iplantc.de.analysis.client.views.widget.AnalysisSearchField;
import org.iplantc.de.analysis.shared.AnalysisModule;
import org.iplantc.de.client.models.analysis.Analysis;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.validators.DiskResourceNameValidator;
import org.iplantc.de.commons.client.views.dialogs.IPlantPromptDialog;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.inject.client.AsyncProvider;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoader;
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.MenuItem;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

import java.util.List;

/**
 * @author sriram, jstroot
 */
public class AnalysesToolBarImpl extends Composite implements AnalysisToolBarView {

    @UiTemplate("AnalysesToolBarImpl.ui.xml")
    interface AnalysesToolbarUiBinder extends UiBinder<Widget, AnalysesToolBarImpl> { }

    @UiField ToolBar menuBar;
    @UiField MenuItem goToFolderMI;
    @UiField MenuItem viewParamsMI;
    @UiField
    MenuItem viewJobInfoMI;
    @UiField MenuItem relaunchMI;
    @UiField MenuItem cancelMI;
    @UiField MenuItem deleteMI;
    @UiField TextButton analysesTb;
    @UiField MenuItem updateCommentsMI;
    @UiField MenuItem renameMI;
    @UiField TextButton editTb;
    @UiField TextButton refreshTb;
    @UiField TextButton showAllTb;
    @UiField AnalysisSearchField searchField;
    @UiField(provided = true) final AnalysesView.Appearance appearance;
    @Inject AsyncProvider<AnalysisParametersDialog> analysisParametersDialogAsyncProvider;


    private List<Analysis> currentSelection;
    private final AnalysesView.Presenter presenter;
    private final PagingLoader<FilterPagingLoadConfig, PagingLoadResult<Analysis>> loader;

    @Inject
    AnalysesToolBarImpl(final AnalysesView.Appearance appearance,
                        @Assisted final AnalysesView.Presenter presenter,
                        @Assisted PagingLoader<FilterPagingLoadConfig, PagingLoadResult<Analysis>> loader) {
        this.appearance = appearance;
        this.presenter = presenter;
        this.loader = loader;
        AnalysesToolbarUiBinder uiBinder = GWT.create(AnalysesToolbarUiBinder.class);
        initWidget(uiBinder.createAndBindUi(this));
    }

    @UiFactory
    AnalysisSearchField createSearchField() {
        return new AnalysisSearchField(loader);
    }

    @Override
    public void filterByAnalysisId(String analysisId, String name) {
        searchField.filterByAnalysisId(analysisId, name);
        showAllTb.enable();
    }

    @Override
    public void filterByParentAnalysisId(String analysisId) {
        searchField.filterByParentId(analysisId);
        showAllTb.enable();
    }

    @Override
    public void onSelectionChanged(SelectionChangedEvent<Analysis> event) {
        currentSelection = event.getSelection();

        int size = currentSelection.size();
        final boolean canCancelSelection = canCancelSelection(currentSelection);
        final boolean canDeleteSelection = canDeleteSelection(currentSelection);

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
                relaunchEnabled = !currentSelection.get(0).isAppDisabled();
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
        viewJobInfoMI.setEnabled(viewParamsEnabled);
        relaunchMI.setEnabled(relaunchEnabled);
        cancelMI.setEnabled(cancelEnabled);
        deleteMI.setEnabled(deleteEnabled);

        renameMI.setEnabled(renameEnabled);
        updateCommentsMI.setEnabled(updateCommentsEnabled);
    }

    @Override
    protected void onEnsureDebugId(String baseID) {
        super.onEnsureDebugId(baseID);
        // Analysis menu
        analysesTb.ensureDebugId(baseID + AnalysisModule.Ids.MENUITEM_ANALYSES);
        goToFolderMI.ensureDebugId(baseID + AnalysisModule.Ids.MENUITEM_ANALYSES
                + AnalysisModule.Ids.MENUITEM_GO_TO_FOLDER);
        viewParamsMI.ensureDebugId(baseID + AnalysisModule.Ids.MENUITEM_ANALYSES
                + AnalysisModule.Ids.MENUITEM_VIEW_PARAMS);
        viewJobInfoMI.ensureDebugId(baseID + AnalysisModule.Ids.MENUITEM_ANALYSES
                + AnalysisModule.Ids.MENUITEM_VIEW_ANALYSES_INFO);
        relaunchMI.ensureDebugId(baseID + AnalysisModule.Ids.MENUITEM_ANALYSES
                + AnalysisModule.Ids.MENUITEM_RELAUNCH);
        cancelMI.ensureDebugId(baseID + AnalysisModule.Ids.MENUITEM_ANALYSES
                + AnalysisModule.Ids.MENUITEM_CANCEL);
        deleteMI.ensureDebugId(baseID + AnalysisModule.Ids.MENUITEM_ANALYSES
                + AnalysisModule.Ids.MENUITEM_DELETE);

        // Edit menu
        editTb.ensureDebugId(baseID + AnalysisModule.Ids.MENUITEM_EDIT);
        renameMI.ensureDebugId(baseID + AnalysisModule.Ids.MENUITEM_EDIT
                + AnalysisModule.Ids.MENUITEM_RENAME);
        updateCommentsMI.ensureDebugId(baseID + AnalysisModule.Ids.MENUITEM_EDIT
                + AnalysisModule.Ids.MENUITEM_UPDATE_COMMENTS);

        refreshTb.ensureDebugId(baseID + AnalysisModule.Ids.BUTTON_REFRESH);
        searchField.ensureDebugId(baseID + AnalysisModule.Ids.FIELD_SEARCH);
    }

    /**
     * Determines if the cancel button should be enable for the given selection.
     *
     * @return true if the selection contains ANY status which is SUBMITTED, IDLE, or RUNNING; false
     *         otherwise.
     */
    boolean canCancelSelection(final List<Analysis> selection) {
        for (Analysis ae : selection) {
            if (ae == null)
                continue;

            final String status = ae.getStatus();
            if (SUBMITTED.toString().equalsIgnoreCase(status)
                    || IDLE.toString().equalsIgnoreCase(status)
                    || RUNNING.toString().equalsIgnoreCase(status)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if the delete button should be enabled for the given selection.
     * 
     * @return true if the selection ONLY contains FAILED or COMPLETED status, false otherwise.
     */
    boolean canDeleteSelection(List<Analysis> selection) {
        for (Analysis ae : selection) {
            if (ae == null)
                continue;

            final String status = ae.getStatus();
            if (!(FAILED.toString().equalsIgnoreCase(status)
                    || COMPLETED.toString().equalsIgnoreCase(status)
                      || CANCELED.toString().equalsIgnoreCase(status))) {
                return false;
            }

        }
        return true;
    }

    //<editor-fold desc="Ui Handlers">
    @UiHandler("searchField")
    void searchFieldKeyUp(KeyUpEvent event){
        if (Strings.isNullOrEmpty(searchField.getCurrentValue())) {
            // disable show all since an empty search field would fire load all.
            showAllTb.disable();
        } else {
            showAllTb.enable();
        }
    }

    @UiHandler("cancelMI")
    void onCancelSelected(SelectionEvent<Item> event) {
        Preconditions.checkNotNull(currentSelection);
        Preconditions.checkState(!currentSelection.isEmpty());

        presenter.cancelSelectedAnalyses(currentSelection);
    }

    @UiHandler("deleteMI")
    void onDeleteSelected(SelectionEvent<Item> event) {
        Preconditions.checkNotNull(currentSelection);
        Preconditions.checkState(!currentSelection.isEmpty());

        ConfirmMessageBox cmb = new ConfirmMessageBox(appearance.warning(),
                                                      appearance.analysesExecDeleteWarning());
        cmb.setPredefinedButtons(Dialog.PredefinedButton.OK, Dialog.PredefinedButton.CANCEL);
        cmb.addDialogHideHandler(new DialogHideEvent.DialogHideHandler() {
            @Override
            public void onDialogHide(DialogHideEvent event) {
                if (Dialog.PredefinedButton.OK.equals(event.getHideButton())){
                    presenter.deleteSelectedAnalyses(currentSelection);
                }
            }
        });
        cmb.show();
    }

    @UiHandler("goToFolderMI")
    void onGoToFolderSelected(SelectionEvent<Item> event) {
        Preconditions.checkNotNull(currentSelection);
        Preconditions.checkState(currentSelection.size() == 1);

        presenter.goToSelectedAnalysisFolder(currentSelection.iterator().next());
    }

    @UiHandler("relaunchMI")
    void onRelaunchSelected(SelectionEvent<Item> event) {
        Preconditions.checkNotNull(currentSelection);
        Preconditions.checkState(currentSelection.size() == 1);

        presenter.relaunchSelectedAnalysis(currentSelection.iterator().next());
    }

    @UiHandler("renameMI")
    void onRenameSelected(SelectionEvent<Item> event) {
        Preconditions.checkNotNull(currentSelection);
        Preconditions.checkState(currentSelection.size() == 1);

        final Analysis selectedAnalysis = currentSelection.iterator().next();
        final String name = selectedAnalysis.getName();
        final IPlantPromptDialog dlg = new IPlantPromptDialog(appearance.rename(),
                                                              -1,
                                                              name,
                                                              new DiskResourceNameValidator());
        dlg.setHeadingText(appearance.renameAnalysis());
        dlg.addOkButtonSelectHandler(new SelectEvent.SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                if (!selectedAnalysis.getName().equals(dlg.getFieldText())) {
                    presenter.renameSelectedAnalysis(selectedAnalysis, dlg.getFieldText());
                }
            }
        });
        dlg.show();
    }

    @UiHandler("updateCommentsMI")
    void onUpdateCommentsSelected(SelectionEvent<Item> event) {
        Preconditions.checkNotNull(currentSelection);
        Preconditions.checkState(currentSelection.size() == 1,
                                 "There should only be 1 analysis selected, but there were %i",
                                 currentSelection.size());


        final Analysis selectedAnalysis = currentSelection.iterator().next();
        final AnalysisCommentsDialog d = new AnalysisCommentsDialog(selectedAnalysis);
        d.addDialogHideHandler(new DialogHideEvent.DialogHideHandler() {
            @Override
            public void onDialogHide(DialogHideEvent event) {
                if (Dialog.PredefinedButton.OK.equals(event.getHideButton())
                        && d.isCommentChanged()) {

                    presenter.updateAnalysisComment(selectedAnalysis, d.getComment());
                }
            }
        });
        d.show();
    }

    @UiHandler("viewParamsMI")
    void onViewParamsSelected(SelectionEvent<Item> event) {
        Preconditions.checkNotNull(currentSelection);
        Preconditions.checkState(currentSelection.size() == 1);

        analysisParametersDialogAsyncProvider.get(new AsyncCallback<AnalysisParametersDialog>() {
            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(caught);
            }

            @Override
            public void onSuccess(AnalysisParametersDialog result) {
                result.show(currentSelection.iterator().next());
            }
        });
    }

    @UiHandler("viewJobInfoMI")
    void onViewAnalysisStepsInfo(SelectionEvent<Item> event) {
        presenter.getAnalysisStepInfo(currentSelection.get(0));
    }

    @UiHandler("refreshTb")
    void onRefreshSelected(SelectEvent event) {
        presenter.onRefreshSelected();
    }

    @UiHandler("showAllTb")
    void onShowAllSelected(SelectEvent event) {
        searchField.clear();
        showAllTb.setEnabled(false);
        presenter.onShowAllSelected();
    }
    //</editor-fold>

}
