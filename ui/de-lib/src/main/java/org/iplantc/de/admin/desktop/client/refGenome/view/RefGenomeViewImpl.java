package org.iplantc.de.admin.desktop.client.refGenome.view;

import org.iplantc.de.admin.desktop.client.refGenome.RefGenomeView;
import org.iplantc.de.admin.desktop.client.refGenome.view.cells.ReferenceGenomeNameCell;
import org.iplantc.de.admin.desktop.shared.Belphegor;
import org.iplantc.de.client.models.apps.refGenome.ReferenceGenome;
import org.iplantc.de.commons.client.views.dialogs.IPlantDialog;

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
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * @author jstroot
 */
public class RefGenomeViewImpl extends Composite implements RefGenomeView {

    private static RefGenomeViewImplUiBinder uiBinder = GWT.create(RefGenomeViewImplUiBinder.class);

    private final class NameColumnComparator implements Comparator<ReferenceGenome> {
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
            return item.getName().toLowerCase().startsWith(query.toLowerCase());
        }

        public void setQuery(String query) {
            this.query = query;
        }
    }

    interface RefGenomeViewImplUiBinder extends UiBinder<Widget, RefGenomeViewImpl> {}

    @UiField TextButton addBtn;
    @UiField Grid<ReferenceGenome> grid;
    @UiField ListStore<ReferenceGenome> store;
    @UiField TextField filterField;
    @UiField(provided = true) RefGenomeAppearance appearance;

    private final ReferenceGenomeProperties rgProps;
    private final FilterByNameStoreFilter nameFilter;
    private RefGenomeView.Presenter presenter;

    @Inject
    RefGenomeViewImpl(final ReferenceGenomeProperties rgProps,
                      final RefGenomeAppearance appearance) {
        this.rgProps = rgProps;
        this.appearance = appearance;
        initWidget(uiBinder.createAndBindUi(this));
        nameFilter = new FilterByNameStoreFilter();
    }

    @UiFactory
    ListStore<ReferenceGenome> createListStore() {
        final ListStore<ReferenceGenome> listStore = new ListStore<>(rgProps.id());
        listStore.setEnableFilters(true);
        return listStore;
    }

    @UiFactory
    ColumnModel<ReferenceGenome> createColumnModel() {
        ColumnConfig<ReferenceGenome, ReferenceGenome> nameCol = new ColumnConfig<>(new IdentityValueProvider<ReferenceGenome>("name"), 300, appearance.nameColumn());
        ColumnConfig<ReferenceGenome, String> pathCol = new ColumnConfig<>(rgProps.path(), 300, appearance.pathColumn());
        ColumnConfig<ReferenceGenome, Date> createdOnCol = new ColumnConfig<>(rgProps.createdDate(), 192, appearance.createdOnColumn());
        ColumnConfig<ReferenceGenome, String> createdByCol = new ColumnConfig<>(rgProps.createdBy(), 160, appearance.createdByColumn());

        nameCol.setCell(new ReferenceGenomeNameCell(this));
        nameCol.setComparator(new NameColumnComparator());
        createdOnCol.setFixed(true);

        @SuppressWarnings("unchecked")
        List<ColumnConfig<ReferenceGenome, ?>> colList = Lists.newArrayList(nameCol, pathCol, createdByCol, createdOnCol);
        return new ColumnModel<>(colList);
    }

    @UiHandler("addBtn")
    void addButtonClicked(SelectEvent event) {
        final IPlantDialog iDlg = new IPlantDialog();
        iDlg.setHideOnButtonClick(false);
        iDlg.setHeadingText(appearance.addReferenceGenomeDialogHeading());
        iDlg.getOkButton().setText(appearance.saveBtnText());
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
        setDialogDebugIds(iDlg);

    }

    private void setDialogDebugIds(IPlantDialog dialog) {
        dialog.ensureDebugId(Belphegor.RefGenomeIds.GENOME_EDITOR);
        dialog.getOkButton().ensureDebugId(Belphegor.RefGenomeIds.GENOME_EDITOR + Belphegor.RefGenomeIds.SAVE_BTN);
    }

    @Override
    public void editReferenceGenome(ReferenceGenome refGenome) {
        final IPlantDialog iDlg = new IPlantDialog();
        iDlg.setHideOnButtonClick(false);
        iDlg.setHeadingText(appearance.editReferenceGenomeDialogHeading(refGenome.getName()));
        iDlg.getOkButton().setText(appearance.saveBtnText());
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
        setDialogDebugIds(iDlg);

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

    @Override
    protected void onEnsureDebugId(String baseID) {
        super.onEnsureDebugId(baseID);

        addBtn.ensureDebugId(baseID + Belphegor.RefGenomeIds.ADD);
        filterField.setId(baseID + Belphegor.RefGenomeIds.NAME_FILTER);
        grid.ensureDebugId(baseID + Belphegor.RefGenomeIds.GRID);
    }
}
