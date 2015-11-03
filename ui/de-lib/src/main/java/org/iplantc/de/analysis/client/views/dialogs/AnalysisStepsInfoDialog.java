package org.iplantc.de.analysis.client.views.dialogs;

import org.iplantc.de.analysis.client.views.AnalysisStepsView;
import org.iplantc.de.commons.client.views.dialogs.IPlantDialog;

public class AnalysisStepsInfoDialog extends IPlantDialog {

    public AnalysisStepsInfoDialog(AnalysisStepsView view) {
        setHeadingText("Analysis Info");
        setSize("600px", "300px");
        add(view);
        setPredefinedButtons(PredefinedButton.OK);
    }

}
