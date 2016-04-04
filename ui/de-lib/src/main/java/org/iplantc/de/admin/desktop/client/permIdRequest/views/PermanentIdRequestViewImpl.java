package org.iplantc.de.admin.desktop.client.permIdRequest.views;

import org.iplantc.de.admin.desktop.client.permIdRequest.model.PermanentIdRequestPathProvider;
import org.iplantc.de.admin.desktop.shared.Belphegor;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.models.identifiers.PermanentIdRequest;
import org.iplantc.de.client.models.identifiers.PermanentIdRequestAutoBeanFactory;
import org.iplantc.de.client.models.identifiers.PermanentIdRequestType;
import org.iplantc.de.client.services.DiskResourceServiceFacade;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.MessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent.DialogHideHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent.SelectionChangedHandler;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 
 * 
 * @author sriram
 * 
 */
public class PermanentIdRequestViewImpl extends Composite implements PermanentIdRequestView {

    private static PermanentIdRequestViewImplUiBinder uiBinder = GWT.create(PermanentIdRequestViewImplUiBinder.class);

    interface PermanentIdRequestViewImplUiBinder extends UiBinder<Widget, PermanentIdRequestViewImpl> {
    }

    @UiField
    ToolBar toolbar;

    @UiField
    TextButton updateBtn;

    @UiField
    TextButton metadataBtn;

    @UiField
    TextButton createDOIBtn;

    @UiField
    Grid<PermanentIdRequest> grid;

    @UiField
    ListStore<PermanentIdRequest> store;

    PermanentIdRequestProperties pr_props;

    PermanentIdRequestViewAppearance appearance;

    private Presenter presenter;

    private final PermanentIdRequestAutoBeanFactory factory;

    @Inject
    PermanentIdRequestViewImpl(PermanentIdRequestProperties pr_props,
                               PermanentIdRequestAutoBeanFactory factory,
                               PermanentIdRequestViewAppearance appearance) {
        this.pr_props = pr_props;
        this.factory = factory;
        this.appearance = appearance;
        initWidget(uiBinder.createAndBindUi(this));
        grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        grid.getSelectionModel()
            .addSelectionChangedHandler(new SelectionChangedHandler<PermanentIdRequest>() {

                @Override
                public void onSelectionChanged(SelectionChangedEvent<PermanentIdRequest> event) {
                    onSelectionChange(event);
                }
            });
        grid.getView().setEmptyText(appearance.noRows());
    }

    @Override
    protected void onEnsureDebugId(String baseID) {
        super.onEnsureDebugId(baseID);

        updateBtn.ensureDebugId(baseID + Belphegor.PermIds.UPDATE);
        metadataBtn.ensureDebugId(baseID + Belphegor.PermIds.METADATA);
        createDOIBtn.ensureDebugId(baseID + Belphegor.PermIds.DOI);
        grid.ensureDebugId(baseID + Belphegor.PermIds.GRID);
    }

    @UiHandler("updateBtn")
    void onUpdateBtnClicked(SelectEvent event) {
       presenter.onUpdateRequest();
    }

    @UiHandler("metadataBtn")
    void onMetadataBtnClicked(SelectEvent event) {
        presenter.fetchMetadata();
    }

    @UiHandler("createDOIBtn")
    void onCreateDOIBtnClicked(SelectEvent event) {
        final MessageBox amb = new MessageBox(appearance.request()
                                                      + " "
                                                      + grid.getSelectionModel()
                                                            .getSelectedItem()
                                                            .getType(),
                                              appearance.confirmCreate(grid.getSelectionModel()
                                                                           .getSelectedItem()
                                                                           .getType()
                                                                           .toString()));
        amb.setPredefinedButtons(PredefinedButton.YES, PredefinedButton.NO, PredefinedButton.CANCEL);
        amb.setIcon(MessageBox.ICONS.question());
        amb.addDialogHideHandler(new DialogHideHandler() {
            @SuppressWarnings("incomplete-switch")
            @Override
            public void onDialogHide(DialogHideEvent event) {

                switch (event.getHideButton()) {
                    case OK:
                        break;
                    case CANCEL:
                        break;
                    case CLOSE:
                        break;
                    case YES:
                        presenter.createPermanentId();
                        break;
                    case NO:
                        break;
                    default:
                        break;
                }
            }
        });
        amb.show();
        setMsgBoxDebugIds(amb);
    }

    private void setMsgBoxDebugIds(MessageBox amb) {
        amb.ensureDebugId(Belphegor.PermIds.CREATE_DOI_MSG);
        amb.getButton(PredefinedButton.CANCEL).ensureDebugId(Belphegor.PermIds.CREATE_DOI_MSG + Belphegor.PermIds.CANCEL);
        amb.getButton(PredefinedButton.YES).ensureDebugId(Belphegor.PermIds.CREATE_DOI_MSG + Belphegor.PermIds.YES);
        amb.getButton(PredefinedButton.NO).ensureDebugId(Belphegor.PermIds.CREATE_DOI_MSG + Belphegor.PermIds.NO);
    }

    @UiFactory
    ListStore<PermanentIdRequest> createListStore() {
        return new ListStore<>(pr_props.id());
    }

    @UiFactory
    ColumnModel<PermanentIdRequest> createColumnModel() {
        List<ColumnConfig<PermanentIdRequest, ?>> list = new ArrayList<>();
        ColumnConfig<PermanentIdRequest, String> nameCol = new ColumnConfig<>(pr_props.requestedBy(),
                                                                              appearance.nameColumnWidth(),
                                                                              appearance.nameColumnLabel());
        ColumnConfig<PermanentIdRequest, String> pathCol = new ColumnConfig<>(new PermanentIdRequestPathProvider(appearance),
                                                                              appearance.pathColumnWidth(),
                                                                              appearance.pathColumnLabel());
        ColumnConfig<PermanentIdRequest, Date> dateSubCol = new ColumnConfig<>(pr_props.dateSubmitted(),
                                                                               appearance.dateSubmittedColumnWidth(),
                                                                               appearance.dateSubmittedColumnLabel());

        ColumnConfig<PermanentIdRequest, Date> dateUpCol = new ColumnConfig<>(pr_props.dateUpdated(),
                                                                              appearance.dateUpdatedColumnWidth(),
                                                                              appearance.dateUpdatedColumnLabel());

        ColumnConfig<PermanentIdRequest, PermanentIdRequestType> type = new ColumnConfig<PermanentIdRequest, PermanentIdRequestType>(pr_props.type(),
                                                                                                                                     50,
                                                                                                                                     "Type");

        ColumnConfig<PermanentIdRequest, String> status = new ColumnConfig<PermanentIdRequest, String>(pr_props.status(),
                                                                                                       75,
                                                                                                       "Status");

        list.add(nameCol);
        list.add(pathCol);
        list.add(dateSubCol);
        list.add(dateUpCol);
        list.add(type);
        list.add(status);

        return new ColumnModel<>(list);
    }

    @Override
    public void mask(String loadingMask) {
        grid.mask(loadingMask);

    }

    @Override
    public void unmask() {
        grid.unmask();
    }

    @Override
    public void setPresenter(Presenter p) {
        this.presenter = p;

    }

    @Override
    public void loadRequests(List<PermanentIdRequest> requests) {
        store.clear();
        store.addAll(requests);

    }

    @Override
    public void update(PermanentIdRequest request) {
        store.update(request);

    }

    void onSelectionChange(SelectionChangedEvent<PermanentIdRequest> event) {
        List<PermanentIdRequest> selection = event.getSelection();
        if (selection.size() > 0) {
            presenter.setSelectedRequest(selection.get(0));
            updateBtn.setEnabled(true);
            metadataBtn.setEnabled(true);
            createDOIBtn.setEnabled(true);
        } else {
            presenter.setSelectedRequest(null);
            updateBtn.setEnabled(false);
            metadataBtn.setEnabled(false);
            createDOIBtn.setEnabled(false);
        }
    }

    @Override
    public void fetchMetadata(Folder selectedFolder,
                              PermanentIdRequestPresenterAppearance appearance,
                              DiskResourceServiceFacade drsvc) {
        MetadataDialog dialog = new MetadataDialog(selectedFolder, appearance, drsvc);
        dialog.show();

    }

}
