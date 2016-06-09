package org.iplantc.de.analysis.client.views;

import static org.iplantc.de.client.models.analysis.AnalysisExecutionStatus.CANCELED;
import static org.iplantc.de.client.models.analysis.AnalysisExecutionStatus.COMPLETED;
import static org.iplantc.de.client.models.analysis.AnalysisExecutionStatus.FAILED;
import static org.iplantc.de.client.models.analysis.AnalysisExecutionStatus.IDLE;
import static org.iplantc.de.client.models.analysis.AnalysisExecutionStatus.RUNNING;
import static org.iplantc.de.client.models.analysis.AnalysisExecutionStatus.SUBMITTED;

import org.iplantc.de.analysis.client.AnalysesView;
import org.iplantc.de.analysis.client.AnalysisToolBarView;
import org.iplantc.de.analysis.client.models.AnalysisFilter;
import org.iplantc.de.analysis.client.views.dialogs.AnalysisCommentsDialog;
import org.iplantc.de.analysis.client.views.dialogs.AnalysisParametersDialog;
import org.iplantc.de.analysis.client.views.widget.AnalysisSearchField;
import org.iplantc.de.analysis.shared.AnalysisModule;
import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.models.analysis.Analysis;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.validators.DiskResourceNameValidator;
import org.iplantc.de.commons.client.views.dialogs.IPlantPromptDialog;
import org.iplantc.de.shared.AsyncProviderWrapper;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import com.sencha.gxt.data.shared.StringLabelProvider;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoader;
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.form.SimpleComboBox;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.MenuItem;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

import java.util.Arrays;
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
    @UiField AnalysisSearchField searchField;
    @UiField(provided = true) final AnalysesView.Appearance appearance;

    @UiField
    TextButton share_menu;
    @UiField
    MenuItem shareCollabMI;

    //hidden for now...
    //@UiField
    //MenuItem shareSupportMI;

    @UiField(provided = true)
    SimpleComboBox<AnalysisFilter> filterCombo;

    @Inject AsyncProviderWrapper<AnalysisParametersDialog> analysisParametersDialogAsyncProvider;

    @Inject
    UserInfo userInfo;


    List<Analysis> currentSelection;
    private final AnalysesView.Presenter presenter;
    private final PagingLoader<FilterPagingLoadConfig, PagingLoadResult<Analysis>> loader;

    @Inject
    AnalysesToolBarImpl(final AnalysesView.Appearance appearance,
                        @Assisted final AnalysesView.Presenter presenter,
                        @Assisted PagingLoader<FilterPagingLoadConfig, PagingLoadResult<Analysis>> loader) {
        this.appearance = appearance;
        this.presenter = presenter;
        this.loader = loader;

        filterCombo = new SimpleComboBox<AnalysisFilter>(new StringLabelProvider<AnalysisFilter>());
        filterCombo.add(Arrays.asList(AnalysisFilter.ALL,
                                      AnalysisFilter.MY_ANALYSES,
                                      AnalysisFilter.SHARED_WITH_ME));
        AnalysesToolbarUiBinder uiBinder = GWT.create(AnalysesToolbarUiBinder.class);
        initWidget(uiBinder.createAndBindUi(this));
        filterCombo.setEditable(false);
        filterCombo.setValue(AnalysisFilter.ALL);
        filterCombo.addSelectionHandler(new SelectionHandler<AnalysisFilter>() {
            @Override
            public void onSelection(SelectionEvent<AnalysisFilter> event) {
                onFilterChange(event.getSelectedItem());
            }
        });
        filterCombo.addValueChangeHandler(new ValueChangeHandler<AnalysisFilter>() {
            @Override
            public void onValueChange(ValueChangeEvent<AnalysisFilter> event) {
               onFilterChange(event.getValue());
            }
        });
    }

    private void onFilterChange(AnalysisFilter af) {
        switch (af) {
            case ALL:
                applyFilter(AnalysisFilter.ALL);
                break;
            case SHARED_WITH_ME:
                applyFilter(AnalysisFilter.SHARED_WITH_ME);
                break;

            case MY_ANALYSES:
                applyFilter(AnalysisFilter.MY_ANALYSES);
                break;
        }
    }

    @UiFactory
    AnalysisSearchField createSearchField() {
        return new AnalysisSearchField(loader);
    }

    @Override
    public void filterByAnalysisId(String analysisId, String name) {
        searchField.filterByAnalysisId(analysisId, name);
        //reset filter. Users need to set Filter to ALL to go back...
        filterCombo.setValue(null);
        presenter.setCurrentFilter(null);
    }

    @Override
    public void filterByParentAnalysisId(String analysisId) {
        searchField.filterByParentId(analysisId);
        //reset filter. Users need to set Filter to ALL to go back...
        filterCombo.setValue(null);
        presenter.setCurrentFilter(null);
    }

    @Override
    public void onSelectionChanged(SelectionChangedEvent<Analysis> event) {
        
        GWT.log("user--->" + userInfo.getFullUsername());
        currentSelection = event.getSelection();

        int size = currentSelection.size();
        final boolean canCancelSelection = canCancelSelection(currentSelection);
        final boolean canDeleteSelection = canDeleteSelection(currentSelection);
        boolean isOwner = isOwner(currentSelection);
        boolean can_share = isSharable(currentSelection);

        boolean goToFolderEnabled, viewParamsEnabled, relaunchEnabled, cancelEnabled, deleteEnabled;
        boolean renameEnabled, updateCommentsEnabled, shareEnabled;
        switch (size) {
            case 0:
                goToFolderEnabled = false;
                viewParamsEnabled = false;
                relaunchEnabled = false;
                cancelEnabled = false;
                deleteEnabled = false;

                renameEnabled = false;
                updateCommentsEnabled = false;
                shareEnabled = false;

                break;
            case 1:
                goToFolderEnabled = true;
                viewParamsEnabled = true;
                relaunchEnabled = !currentSelection.get(0).isAppDisabled();
                cancelEnabled = canCancelSelection && isOwner;
                deleteEnabled = canDeleteSelection && isOwner;

                renameEnabled = isOwner;
                updateCommentsEnabled = isOwner;
                shareEnabled = isOwner && can_share;
                break;

            default:
                // If more than 1 is selected
                goToFolderEnabled = false;
                viewParamsEnabled = false;
                relaunchEnabled = false;
                cancelEnabled = canCancelSelection && isOwner;
                deleteEnabled = canDeleteSelection && isOwner;
                shareEnabled = isOwner && can_share;
                renameEnabled = false;
                updateCommentsEnabled = false;
        }

        goToFolderMI.setEnabled(goToFolderEnabled);
        viewParamsMI.setEnabled(viewParamsEnabled);
        viewJobInfoMI.setEnabled(viewParamsEnabled);
        relaunchMI.setEnabled(relaunchEnabled);
        cancelMI.setEnabled(cancelEnabled);
        deleteMI.setEnabled(deleteEnabled);
        share_menu.setEnabled(shareEnabled);
        shareCollabMI.setEnabled(shareEnabled);
       // shareSupportMI.setEnabled(shareEnabled);
        renameMI.setEnabled(renameEnabled);
        updateCommentsMI.setEnabled(updateCommentsEnabled);
    }

    private boolean isOwner(List<Analysis> selection) {
        for (Analysis a : selection) {
            if (!(a.getUserName().equals(userInfo.getFullUsername()))) {
                return false;
            }
        }

        return true;
    }

     boolean isSharable(List<Analysis> selection) {
        for (Analysis a : selection) {
            if ((!(a.getStatus().equals(COMPLETED.toString())
                  || a.getStatus().equals(FAILED.toString()))
                  || !( a.isSharable()))) {
                return false;
            }

        }
        return true;
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

        //share menu
        share_menu.ensureDebugId(baseID + AnalysisModule.Ids.SHARE_MENU );
        shareCollabMI.ensureDebugId(baseID + AnalysisModule.Ids.SHARE_COLLAB);
        shareCollabMI.ensureDebugId(baseID + AnalysisModule.Ids.SHARE_SUPPORT);

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
            filterCombo.setValue(AnalysisFilter.ALL);
        } else {
            filterCombo.setValue(null);
            presenter.setCurrentFilter(null);
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

    void applyFilter(AnalysisFilter filter) {
        presenter.setCurrentFilter(filter);
    }

    @Override
    public void setFilterInView(AnalysisFilter filter) {
        filterCombo.setValue(filter);
    }

    @UiHandler("shareCollabMI")
    void onShareSelected(SelectionEvent<Item> event) {
       presenter.onShareSelected(currentSelection);
    }

  /**  @UiHandler("shareSupportMI")
    void onShareSupportSelected(SelectionEvent<Item> event) {
        ConfirmMessageBox messageBox = new ConfirmMessageBox(appearance.shareSupport(),
                                                             appearance.shareSupportConfirm());
        messageBox.setPredefinedButtons(Dialog.PredefinedButton.YES,
                                        Dialog.PredefinedButton.NO,
                                        Dialog.PredefinedButton.CANCEL);
        messageBox.getButton(Dialog.PredefinedButton.YES).setText(appearance.shareWithInput());
        messageBox.getButton(Dialog.PredefinedButton.NO).setText(appearance.shareOutputOnly());

        messageBox.addDialogHideHandler(new DialogHideEvent.DialogHideHandler() {
            @Override
            public void onDialogHide(DialogHideEvent event) {
                switch (event.getHideButton()) {
                    case YES:
                        presenter.onShareSupportSelected(currentSelection, true);
                        break;
                    case NO:
                        presenter.onShareSupportSelected(currentSelection, false);
                        break;

                    case CANCEL:
                        break;
                }
            }
        });

        messageBox.show();

    } **/

}
