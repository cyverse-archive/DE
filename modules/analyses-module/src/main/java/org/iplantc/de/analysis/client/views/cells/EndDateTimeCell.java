package org.iplantc.de.analysis.client.views.cells;

import org.iplantc.de.client.models.analysis.Analysis;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

public class EndDateTimeCell extends AnalysisTimeStampCell {

    @Override
    public void render(com.google.gwt.cell.client.Cell.Context arg0, Analysis arg1, SafeHtmlBuilder sb) {
        sb.append(SafeHtmlUtils.fromTrustedString(getFormattedDate(arg1.getEndDate())));
    }

}
