package org.iplantc.de.admin.desktop.client.permIdRequest.view;

import org.iplantc.de.client.models.identifiers.PermanentIdRequest;
import org.iplantc.de.client.models.identifiers.PermanentIdRequestStatus;
import org.iplantc.de.client.models.identifiers.PermanentIdRequestType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
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
    BorderLayoutContainer con;

    @UiField
    ToolBar toolbar;

    @UiField
    TextButton updateBtn, metadataBtn;

    @UiField
    Grid<PermanentIdRequest> grid;

    PermanentIdRequestProperties pr_props;

    PermIdRequestViewAppearance appearance;

    public PermIdRequestViewImpl(PermanentIdRequestProperties pr_props) {
        this.pr_props = pr_props;
        appearance = GWT.create(PermIdRequestViewAppearance.class);
        initWidget(uiBinder.createAndBindUi(this));
    }

    @UiFactory
    ListStore<PermanentIdRequest> createListStore() {
        return new ListStore<>(pr_props.id());
    }

    @UiFactory
    ColumnModel<PermanentIdRequest> createColumnModel() {
        List<ColumnConfig<PermanentIdRequest, ?>> list = new ArrayList<>();
        ColumnConfig<PermanentIdRequest, String> nameCol = new ColumnConfig<>(pr_props.name(),
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

        ColumnConfig<PermanentIdRequest, PermanentIdRequestStatus> status = new ColumnConfig<PermanentIdRequest, PermanentIdRequestStatus>(pr_props.status(),
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
        con.mask(loadingMask);

    }

    @Override
    public void unmask() {
        con.unmask();
    }

}
