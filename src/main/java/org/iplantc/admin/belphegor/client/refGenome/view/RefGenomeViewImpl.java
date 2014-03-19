package org.iplantc.admin.belphegor.client.refGenome.view;

import org.iplantc.admin.belphegor.client.I18N;
import org.iplantc.admin.belphegor.client.refGenome.RefGenomeView;
import org.iplantc.admin.belphegor.client.refGenome.view.cells.ReferenceGenomeNameCell;
import org.iplantc.de.client.models.apps.refGenome.ReferenceGenome;
import org.iplantc.de.commons.client.views.gxt3.dialogs.IPlantDialog;
import org.iplantc.de.resources.client.IplantResources;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.Store;
import com.sencha.gxt.data.shared.Store.StoreFilter;
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class RefGenomeViewImpl extends Composite implements RefGenomeView {

    private static RefGenomeViewImpleUiBinder uiBinder = GWT.create(RefGenomeViewImpleUiBinder.class);

    private final class NameColumnComparatory implements Comparator<ReferenceGenome> {
        @Override
        public int compare(ReferenceGenome arg0, ReferenceGenome arg1) {
            return arg0.getName().compareToIgnoreCase(arg1.getName());
        }
    }

    private final class FilterByNameStoreFilter implements StoreFilter<ReferenceGenome> {
        private String query;

        @Override
        public boolean select(Store<ReferenceGenome> store, ReferenceGenome parent, ReferenceGenome item) {
            if (Strings.nullToEmpty(query).isEmpty()) {
                return false;
            }
            final boolean startsWith = item.getName().toLowerCase().startsWith(query.toLowerCase());
            return startsWith;
        }

        public void setQuery(String query) {
            this.query = query;
        }
    }

    interface RefGenomeViewImpleUiBinder extends UiBinder<Widget, RefGenomeViewImpl> {}

    @UiField(provided = true)
    IplantResources res;
    @UiField(provided = true)
    IplantDisplayStrings strings;

    @UiField
    TextButton addBtn;

    @UiField
    Grid<ReferenceGenome> grid;

    @UiField
    ListStore<ReferenceGenome> store;

    private final ReferenceGenomeProperties rgProps;
    private final FilterByNameStoreFilter nameFilter;
    private RefGenomeView.Presenter presenter;

    @Inject
    public RefGenomeViewImpl(IplantResources res, IplantDisplayStrings strings, ReferenceGenomeProperties rgProps) {
        this.res = res;
        this.strings = strings;
        this.rgProps = rgProps;
        initWidget(uiBinder.createAndBindUi(this));
        nameFilter = new FilterByNameStoreFilter();
    }

    @UiFactory
    ListStore<ReferenceGenome> createListStore() {
        final ListStore<ReferenceGenome> listStore = new ListStore<ReferenceGenome>(rgProps.id());
        listStore.setEnableFilters(true);
        return listStore;
    }

    @UiFactory
    ColumnModel<ReferenceGenome> createColumnModel() {
        ColumnConfig<ReferenceGenome, ReferenceGenome> nameCol = new ColumnConfig<ReferenceGenome, ReferenceGenome>(new IdentityValueProvider<ReferenceGenome>("name"), 300, strings.name());
        ColumnConfig<ReferenceGenome, String> pathCol = new ColumnConfig<ReferenceGenome, String>(rgProps.path(), 300, strings.path());
        ColumnConfig<ReferenceGenome, Date> createdOnCol = new ColumnConfig<ReferenceGenome, Date>(rgProps.createdDate(), 192, strings.createdOn());
        ColumnConfig<ReferenceGenome, String> createdByCol = new ColumnConfig<ReferenceGenome, String>(rgProps.createdBy(), 160, strings.createdBy());

        nameCol.setCell(new ReferenceGenomeNameCell(this));
        nameCol.setComparator(new NameColumnComparatory());
        createdOnCol.setFixed(true);

        @SuppressWarnings("unchecked")
        List<ColumnConfig<ReferenceGenome, ?>> colList = Lists.<ColumnConfig<ReferenceGenome, ?>> newArrayList(nameCol, pathCol, createdByCol, createdOnCol);
        return new ColumnModel<ReferenceGenome>(colList);
    }

    @UiHandler("addBtn")
    void addButtonClicked(SelectEvent event) {
        final IPlantDialog iDlg = new IPlantDialog();
        iDlg.setHideOnButtonClick(false);
        iDlg.setHeadingText(I18N.DISPLAY.addReferenceGenome());
        iDlg.getOkButton().setText("Save");
        final EditReferenceGenomeDialog addRefGenomePanel = EditReferenceGenomeDialog.addNewReferenceGenome();
        iDlg.add(addRefGenomePanel);
        iDlg.addOkButtonSelectHandler(new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                final ReferenceGenome value = addRefGenomePanel.getValue();
                if (!addRefGenomePanel.hasErrors()) {
                    iDlg.hide();
                    presenter.addReferenceGenome(value);
                }
            }
        });
        iDlg.addCancelButtonSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) { iDlg.hide(); }
        });

        iDlg.show();

    }

    @Override
    public void editReferenceGenome(ReferenceGenome refGenome) {
        final IPlantDialog iDlg = new IPlantDialog();
        iDlg.setHideOnButtonClick(false);
        iDlg.setHeadingText(I18N.DISPLAY.edit() + ": " + refGenome.getName());
        iDlg.getOkButton().setText("Save");
        final EditReferenceGenomeDialog addRefGenomePanel = EditReferenceGenomeDialog.editReferenceGenome(refGenome);
        iDlg.add(addRefGenomePanel);
        iDlg.addOkButtonSelectHandler(new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                final ReferenceGenome value = addRefGenomePanel.getValue();
                if (!addRefGenomePanel.hasErrors()) {
                    iDlg.hide();
                    presenter.editReferenceGenome(value);
                }
            }
        });
        iDlg.addCancelButtonSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                iDlg.hide();
            }
        });

        iDlg.show();

    }

    @UiHandler("filterField")
    void onFilterValueChanged(ValueChangeEvent<String> event) {
        store.removeFilters();
        final String query = Strings.nullToEmpty(event.getValue());
        if (query.isEmpty()) {
            return;
        }
        nameFilter.setQuery(query);
        store.addFilter(nameFilter);
    }

    @Override
    public void setReferenceGenomes(List<ReferenceGenome> refGenomes) {
        store.addAll(refGenomes);
    }

    @Override
    public void addReferenceGenome(ReferenceGenome referenceGenome) {
        store.add(referenceGenome);
    }

    @Override
    public void updateReferenceGenome(ReferenceGenome referenceGenome) {
        store.update(referenceGenome);
    }

    @Override
    public void setPresenter(RefGenomeView.Presenter presenter) {
        this.presenter = presenter;
    }
}
