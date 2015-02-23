package org.iplantc.de.analysis.client.views.parameters.cells;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

/**
 * @author sriram, jstroot
 */
public class AnalysisParamNameCell extends AbstractCell<String> {
    public interface AnalysisParamNameCellAppearance {
        void render(Context context, String value, SafeHtmlBuilder sb);
    }

    private final AnalysisParamNameCellAppearance appearance;

    public AnalysisParamNameCell(){
        this(GWT.<AnalysisParamNameCellAppearance> create(AnalysisParamNameCellAppearance.class));
    }

    protected AnalysisParamNameCell(AnalysisParamNameCellAppearance appearance){
        this.appearance = appearance;
    }

    @Override
    public void render(com.google.gwt.cell.client.Cell.Context context, String value,
            SafeHtmlBuilder sb) {
        appearance.render(context, value, sb);
    }
}
