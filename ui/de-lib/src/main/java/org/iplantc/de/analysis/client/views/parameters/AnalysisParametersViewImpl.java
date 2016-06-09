package org.iplantc.de.analysis.client.views.parameters;

import org.iplantc.de.analysis.client.AnalysesView;
import org.iplantc.de.analysis.client.AnalysisParametersView;
import org.iplantc.de.analysis.client.events.SaveAnalysisParametersEvent;
import org.iplantc.de.analysis.client.events.selection.AnalysisParamValueSelectedEvent;
import org.iplantc.de.client.models.IsHideable;
import org.iplantc.de.client.models.analysis.AnalysisParameter;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.diskResource.client.views.dialogs.SaveAsDialog;
import org.iplantc.de.shared.AsyncProviderWrapper;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

import java.util.List;

/**
 * FIXME JDS Fix debug ids.
 * @author jstroot
 */
public class AnalysisParametersViewImpl extends Composite implements AnalysisParametersView {

    interface AnalysisParamViewUiBinder extends UiBinder<BorderLayoutContainer, AnalysisParametersViewImpl> {
    }

    @UiField(provided = true) final ListStore<AnalysisParameter> listStore;
    @UiField(provided = true) final AnalysesView.Appearance appearance;
    @UiField(provided = true) final ColumnModel<AnalysisParameter> cm;
    private final AnalysisParamViewColumnModel apcm; // Convenience reference
    @UiField Grid<AnalysisParameter> grid;
    @UiField BorderLayoutContainer con;
    @UiField ToolBar menuToolBar;
    @UiField BorderLayoutData northData;
    @UiField TextButton btnSave;

    @Inject AsyncProviderWrapper<SaveAsDialog> saveAsDialogProvider;

    @Inject
    AnalysisParametersViewImpl(final AnalysesView.Appearance appearance,
                               final AnalysisParamViewColumnModel cm,
                               @Assisted final ListStore<AnalysisParameter> listStore) {
        this.appearance = appearance;
        this.cm = cm;
        this.apcm = cm;
        this.listStore = listStore;
        AnalysisParamViewUiBinder uiBinder = GWT.create(AnalysisParamViewUiBinder.class);
        initWidget(uiBinder.createAndBindUi(this));
        grid.getView().setEmptyText(appearance.noParameters());
    }

    @Override
    public HandlerRegistration addAnalysisParamValueSelectedEventHandler(AnalysisParamValueSelectedEvent.AnalysisParamValueSelectedEventHandler handler) {
        return apcm.addAnalysisParamValueSelectedEventHandler(handler);
    }

    @Override
    public HandlerRegistration addSaveAnalysisParametersEventHandler(SaveAnalysisParametersEvent.SaveAnalysisParametersEventHandler handler) {
        return addHandler(handler, SaveAnalysisParametersEvent.TYPE);
    }

    @UiHandler("btnSave")
    void onSaveClick(SelectEvent event) {
        saveAsDialogProvider.get(new AsyncCallback<SaveAsDialog>() {
            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(caught);
            }

            @Override
            public void onSuccess(final SaveAsDialog result) {
                result.addOkButtonSelectHandler(new SelectHandler() {

                    @Override
                    public void onSelect(SelectEvent event) {
                        if (result.isValid()) {
                            String fileContents = writeTabFile();
                            saveFile(result.getSelectedFolder().getPath() + "/" + result.getFileName(),
                                     fileContents, result);
                        }
                    }
                });

                result.addCancelButtonSelectHandler(new SelectHandler() {

                    @Override
                    public void onSelect(SelectEvent event) {
                        result.hide();
                    }
                });
                result.show(null);
                result.toFront();
            }
        });
    }

    @Override
    public void mask() {
        mask(appearance.retrieveParametersLoadingMask());
    }

    private void saveFile(final String path,
                          final String fileContents,
                          final IsHideable hideable) {
        fireEvent(new SaveAnalysisParametersEvent(path, fileContents, hideable));
    }

    private String writeTabFile() {
        StringBuilder sw = new StringBuilder();
        sw.append(appearance.paramName()).append("\t").append(appearance.paramType()).append("\t").append(appearance.paramValue()).append("\n");
        List<AnalysisParameter> params = grid.getStore().getAll();
        for (AnalysisParameter ap : params) {
            sw.append(ap.getName()).append("\t").append(ap.getType()).append("\t").append(ap.getDisplayValue()).append("\n");
        }

        return sw.toString();
    }

}
