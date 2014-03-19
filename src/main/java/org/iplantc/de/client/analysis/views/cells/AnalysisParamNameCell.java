/**
 * 
 */
package org.iplantc.de.client.analysis.views.cells;

import org.iplantc.de.client.models.analysis.AnalysisParameter;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

/**
 * @author sriram
 * 
 */
public class AnalysisParamNameCell extends AbstractCell<AnalysisParameter> {

    @Override
    public void render(com.google.gwt.cell.client.Cell.Context context, AnalysisParameter value,
            SafeHtmlBuilder sb) {
        // add tool tip
        sb.appendHtmlConstant("<span title='" + value.getName() + "'>" + value.getName() + "</span>");
    }
}
