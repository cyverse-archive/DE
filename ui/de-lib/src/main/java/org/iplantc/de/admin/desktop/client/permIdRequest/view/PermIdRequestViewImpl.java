package org.iplantc.de.admin.desktop.client.permIdRequest.view;

import org.iplantc.de.client.models.identifiers.PermanentIdRequest;
import org.iplantc.de.client.models.identifiers.PermanentIdRequestAutoBeanFactory;
import org.iplantc.de.client.models.identifiers.PermanentIdRequestType;

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
import com.sencha.gxt.widget.core.client.button.TextButton;
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
public class PermIdRequestViewImpl extends Composite implements PermIdRequestView {

    private static PermIdRequestViewImplUiBinder uiBinder = GWT.create(PermIdRequestViewImplUiBinder.class);

    interface PermIdRequestViewImplUiBinder extends UiBinder<Widget, PermIdRequestViewImpl> {
    }

    @UiField
    ToolBar toolbar;

    @UiField
    TextButton updateBtn, metadataBtn;

    @UiField
    Grid<PermanentIdRequest> grid;

    @UiField
    ListStore<PermanentIdRequest> store;

    PermanentIdRequestProperties pr_props;

    PermIdRequestViewAppearance appearance;

    private Presenter presenter;

    private final PermanentIdRequestAutoBeanFactory factory;

    @Inject
    public PermIdRequestViewImpl(PermanentIdRequestProperties pr_props,
                                 PermanentIdRequestAutoBeanFactory factory,
                                 PermIdRequestViewAppearance appearance) {
        this.pr_props = pr_props;
        this.factory = factory;
        this.appearance = appearance;
        initWidget(uiBinder.createAndBindUi(this));
        grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        grid.getSelectionModel()
            .addSelectionChangedHandler(new SelectionChangedHandler<PermanentIdRequest>() {

                @Override
                public void onSelectionChanged(SelectionChangedEvent<PermanentIdRequest> event) {
                    presenter.setSelectedRequest(event.getSelection().get(0));
                    updateBtn.setEnabled(true);
                    metadataBtn.setEnabled(true);
                }
            });
        grid.getView().setEmptyText("No rows to display!");
    }

    @UiHandler("updateBtn")
    void onUpdateBtnClicked(SelectEvent event) {
        final UpdatePermanentIdRequestDialog dialog = new UpdatePermanentIdRequestDialog(grid.getSelectionModel()
                                                                                             .getSelectedItem(),
                                                                                         presenter,
                                                                                   factory);
        dialog.show();
    }

    @UiHandler("metadataBtn")
    void onMetadataBtnClicked(SelectEvent event) {
        presenter.fetchMetadata();
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
        ColumnConfig<PermanentIdRequest, String> pathCol = new ColumnConfig<>(pr_props.path(),
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

}
