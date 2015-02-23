package org.iplantc.de.theme.base.client.analyses.cells;

import org.iplantc.de.analysis.client.views.parameters.cells.AnalysisParamNameCell;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

/**
 * @author jstroot
 */
public class AnalysisParamNameCellDefaultAppearance implements AnalysisParamNameCell.AnalysisParamNameCellAppearance {

    interface Templates extends SafeHtmlTemplates {
        @SafeHtmlTemplates.Template("<span title='{0}'>{1}</span>" )
        SafeHtml cell(String title, SafeHtml paramName);
    }

    private final Templates template = GWT.create(Templates.class);

    @Override
    public void render(Cell.Context context, String value, SafeHtmlBuilder sb) {
        final SafeHtml title = SafeHtmlUtils.fromString(value);
        sb.append(template.cell(title.asString(), title));
    }
}
