package org.iplantc.de.analysis.client.views.dialogs;

import org.iplantc.de.analysis.client.presenter.sharing.AnalysisSharingPresenter;
import org.iplantc.de.analysis.client.views.sharing.AnalysisSharingView;
import org.iplantc.de.analysis.client.views.sharing.AnalysisSharingViewImpl;
import org.iplantc.de.client.models.analysis.Analysis;
import org.iplantc.de.client.services.AnalysisServiceFacade;
import org.iplantc.de.client.sharing.SharingPresenter;
import org.iplantc.de.client.util.JsonUtil;
import org.iplantc.de.collaborators.client.util.CollaboratorsUtil;
import org.iplantc.de.commons.client.views.dialogs.IPlantDialog;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;

import java.util.ArrayList;
import java.util.List;

public class AnalysisSharingDialog extends IPlantDialog implements SelectHandler {

    private final AnalysisServiceFacade analysisService;

    private SharingPresenter sharingPresenter;

    @Inject
    CollaboratorsUtil collaboratorsUtil;
    @Inject
    JsonUtil jsonUtil;

    @Inject
    AnalysisSharingDialog(final AnalysisServiceFacade analysisService) {
        super(true);
        this.analysisService = analysisService;
        setPixelSize(600, 500);
        setHideOnButtonClick(true);
        setModal(true);
        setResizable(false);
        // addHelp(new HTML(appearance.sharePermissionsHelp()));
        setHeadingText("Manage Sharing");
        setOkButtonText("Done");
        addOkButtonSelectHandler(this);
    }

    @Override
    public void onSelect(SelectEvent event) {
        Preconditions.checkNotNull(sharingPresenter);
        sharingPresenter.processRequest();
    }

    public void show(final List<Analysis> resourcesToShare) {
        ListStore<Analysis> analysisStore = new ListStore<>(new ModelKeyProvider<Analysis>() {

            @Override
            public String getKey(Analysis item) {
                return item.getId();
            }
        });
        AnalysisSharingView view = new AnalysisSharingViewImpl(buildAnalysisColumnModel(), analysisStore);
        view.setSelectedAnalysis(resourcesToShare);
        sharingPresenter = new AnalysisSharingPresenter(analysisService,
                                                   resourcesToShare,
                                                   view,
                                                   collaboratorsUtil,
                                                   jsonUtil);
        sharingPresenter.go(this);
        super.show();
    }

    @Override
    public void show() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("This method is not supported for this class. ");
    }

    private ColumnModel<Analysis> buildAnalysisColumnModel() {
        List<ColumnConfig<Analysis, ?>> list = new ArrayList<>();

        ColumnConfig<Analysis, String> name = new ColumnConfig<>(new ValueProvider<Analysis, String>() {

            @Override
            public String getValue(Analysis object) {
                return object.getName();
            }

            @Override
            public void setValue(Analysis object, String value) {
                // TODO Auto-generated method stub
            }

            @Override
            public String getPath() {
                return "name";
            }
        }, 180, "Name");
        list.add(name);
        return new ColumnModel<>(list);
    }

}
