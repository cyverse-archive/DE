package org.iplantc.de.analysis.client.theme;

import org.iplantc.de.analysis.client.views.widget.cells.AnalysisParamNameCell;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

public class AnalysisParamNameCellDefaultAppearance implements AnalysisParamNameCell.AnalysisParamNameCellAppearance {

    interface Templates extends SafeHtmlTemplates {
        @SafeHtmlTemplates.Template("<span title='{0}'>{0}</span>" )
        SafeHtml cell(SafeHtml paramName);
    }

    private final Templates template = GWT.create(Templates.class);

    @Override
    public void render(Cell.Context context, String value, SafeHtmlBuilder sb) {
        sb.append(template.cell(SafeHtmlUtils.fromString(value)));
    }
}
