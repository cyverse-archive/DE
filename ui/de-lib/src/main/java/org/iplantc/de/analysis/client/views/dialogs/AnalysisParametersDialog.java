package org.iplantc.de.analysis.client.views.dialogs;

import org.iplantc.de.analysis.client.AnalysisParametersView;
import org.iplantc.de.client.models.analysis.Analysis;
import org.iplantc.de.commons.client.views.dialogs.IPlantDialog;

import com.google.inject.Inject;

/**
 * @author jstroot
 */
public class AnalysisParametersDialog extends IPlantDialog {

    private final AnalysisParametersView.Appearance appearance;

    @Inject AnalysisParametersView.Presenter presenter;

    @Inject
    AnalysisParametersDialog(final AnalysisParametersView.Appearance appearance){
        this.appearance = appearance;

        setSize("520px", "375px");
        setMinHeight(375);
        setMinWidth(520);
        setHideOnButtonClick(true);
        setResizable(true);
    }

    public void show(final Analysis analysis){
        setHeadingText(appearance.viewParameters(analysis.getName()));

        AnalysisParametersView view = presenter.getView();
        add(view);

        super.show();

        // Mask View, the Fetch analysis parameters
        presenter.fetchAnalysisParameters(analysis);
    }

    @Override
    public void show() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("This method is not supported. Use show(Analysis) method instead.");
    }
}
