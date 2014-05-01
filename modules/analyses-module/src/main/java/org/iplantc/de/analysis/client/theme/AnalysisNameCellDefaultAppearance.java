package org.iplantc.de.analysis.client.theme;

import org.iplantc.de.analysis.client.views.cells.AnalysisNameCell;
import org.iplantc.de.client.models.analysis.Analysis;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.common.base.Strings;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

public class AnalysisNameCellDefaultAppearance implements AnalysisNameCell.AnalysisNameCellAppearance {

    public interface AnalysisNameCellStyles extends CssResource {

        String hasResultFolder();

        String noResultFolder();

        String disabledApp();

        String enabledApp();
    }

    public interface AnalysisNameCellResources extends ClientBundle {
        @Source("AnalysisAppNameCell.css")
        AnalysisNameCellStyles styles();

    }

    interface Templates extends SafeHtmlTemplates {

        @SafeHtmlTemplates.Template("<span name=\"{0}\" title=\" {3}\" class=\"{1}\">{2}</span>")
        SafeHtml cell(String elementName, String className, SafeHtml analysisName, String tooltip);
    }

    private final AnalysisNameCellResources resources;
    private final Templates template;

    public AnalysisNameCellDefaultAppearance() {
        this(GWT. <AnalysisNameCellResources> create(AnalysisNameCellResources.class));
    }

    public AnalysisNameCellDefaultAppearance(AnalysisNameCellResources resources){
        this.resources = resources;
        resources.styles().ensureInjected();
        this.template = GWT.create(Templates.class);
    }

    @Override
    public void doOnMouseOut(Element eventTarget, Analysis value) {
        if (eventTarget.getAttribute("name").equalsIgnoreCase(ELEMENT_NAME)
                    && !Strings.isNullOrEmpty(value.getResultFolderId())) {
            eventTarget.getStyle().setTextDecoration(Style.TextDecoration.NONE);
        }
    }

    @Override
    public void doOnMouseOver(Element eventTarget, Analysis value) {
        if (eventTarget.getAttribute("name").equalsIgnoreCase(ELEMENT_NAME)
                    && !Strings.isNullOrEmpty(value.getResultFolderId())) {
            eventTarget.getStyle().setTextDecoration(Style.TextDecoration.UNDERLINE);
        }
    }

    @Override
    public void render(Cell.Context context, Analysis model, SafeHtmlBuilder sb) {
        if (model == null)
            return;

        final AnalysisNameCellStyles styles = resources.styles();
        String style = Strings.isNullOrEmpty(model.getResultFolderId()) ? styles.noResultFolder()
                               : styles.hasResultFolder();
        String tooltip = I18N.DISPLAY.goToOutputFolder() + " of " + model.getName();
        sb.append(template.cell(ELEMENT_NAME, style, SafeHtmlUtils.fromString(model.getName()), tooltip));

    }
}
