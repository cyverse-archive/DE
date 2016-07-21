/**
 * 
 * @author sriram
 */
package org.iplantc.de.diskResource.client.views.dialogs;

import org.iplantc.de.client.models.genomes.Genome;
import org.iplantc.de.diskResource.client.ToolbarView;
import org.iplantc.de.diskResource.client.ToolbarView.Presenter;
import org.iplantc.de.diskResource.client.model.GenomeProperties;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.KeyCodeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import com.sencha.gxt.core.client.util.DelayedTask;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent.SelectionChangedHandler;

import java.util.ArrayList;
import java.util.List;

public class GenomeSearchDialog extends Window {

    public interface Appearance {
        String heading();

        String loading();

        String importText();

        String searchGenome();

        String organismName();

        String version();

        String chromosomeCount();

        String sequenceType();

        String norecords();
    }

    @UiTemplate("GenomeSearchView.ui.xml")
    interface GenomeSearchViewUiBinder extends UiBinder<Widget, GenomeSearchDialog> {
    }

    private static final GenomeSearchViewUiBinder BINDER = GWT.create(GenomeSearchViewUiBinder.class);

    @UiField
    TextField searchtxt;

    @UiField
    Grid<Genome> grid;

    @UiField(provided = true)
    ColumnModel<Genome> cm;

    @UiField(provided = true)
    ListStore<Genome> store;

    @UiField
    TextButton importBtn;

    ToolbarView.Presenter presenter;

    final Appearance apperance;

    private final DelayedTask dqTask;

    private final int queryDelay = 500;

    @Inject
    public GenomeSearchDialog() {
        apperance = GWT.<Appearance> create(Appearance.class);
        setHeadingHtml(apperance.heading());
        setModal(true);
        store = new ListStore<>(new ModelKeyProvider<Genome>() {

            @Override
            public String getKey(Genome item) {
                return item.getId() + "";
            }
        });
        buildColumnModel();
        add(BINDER.createAndBindUi(this));
        searchtxt.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                String term = searchtxt.getCurrentValue();
                if (term != null && term.length() > 2 && !isModifierKey(event.getNativeKeyCode())) {
                    grid.mask(apperance.loading());
                    dqTask.delay(queryDelay);
                }
            }
        });
        grid.getSelectionModel().addSelectionChangedHandler(new SelectionChangedHandler<Genome>() {

            @Override
            public void onSelectionChanged(SelectionChangedEvent<Genome> event) {
                if (event.getSelection().size() > 0) {
                    importBtn.setEnabled(true);
                } else {
                    importBtn.setEnabled(false);
                }
            }
        });

        grid.getView().setEmptyText(apperance.norecords());

        dqTask = new DelayedTask() {

            @Override
            public void onExecute() {
                serchGenome();
            }
        };
    }
    
    private boolean isModifierKey(int keyCode) {
        switch (keyCode) {
            case KeyCodes.KEY_ENTER:
                // Enter is special cased by TextInputCells, so it's handled by SearchFieldCell.
            case KeyCodes.KEY_ALT:
            case KeyCodes.KEY_CTRL:
            case KeyCodes.KEY_END:
            case KeyCodes.KEY_ESCAPE:
            case KeyCodes.KEY_HOME:
            case KeyCodes.KEY_PAGEDOWN:
            case KeyCodes.KEY_PAGEUP:
            case KeyCodes.KEY_SHIFT:
            case KeyCodes.KEY_TAB:
                return true;
            default:
                return KeyCodeEvent.isArrow(keyCode);
        }
    }
    
    @UiHandler("importBtn")
    void onImportedClicked(SelectEvent event) {
        presenter.importGenomeFromCoge(grid.getSelectionModel().getSelectedItem().getId());
    }

    public void buildColumnModel() {

        GenomeProperties props = GWT.create(GenomeProperties.class);

        ColumnConfig<Genome, String> version = new ColumnConfig<>(props.version(),
                                                                  50,
                                                                  apperance.version());

        ColumnConfig<Genome, String> seqType = new ColumnConfig<>(props.sequenceName(),
                                                                  150,
                                                                  apperance.sequenceType());
        
        ColumnConfig<Genome, String> organism = new ColumnConfig<>(props.organismName(),
                                                                   250,
                                                                   apperance.organismName());
        ColumnConfig<Genome, Integer> chromeCount = new ColumnConfig<>(props.chromosomeCount(),
                                                                       100,
                                                                      apperance.chromosomeCount());
        ArrayList<ColumnConfig<Genome, ?>> cols = new ArrayList<>();
        cols.add(organism);
        cols.add(version);
        cols.add(chromeCount);
        cols.add(seqType);

        cm = new ColumnModel<>(cols);
    }

    private void serchGenome() {
        presenter.searchGenomeInCoge(searchtxt.getCurrentValue());
    }

    public void loadResults(List<Genome> genomes) {
        store.clear();
        store.addAll(genomes);
        grid.unmask();
    }

    public void clearView() {
        store.clear();
        searchtxt.clear();
    }

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;

    }

    public void unmask() {
        grid.unmask();
    }
}
