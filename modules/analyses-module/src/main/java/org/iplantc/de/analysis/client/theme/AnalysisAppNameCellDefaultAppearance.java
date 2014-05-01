package org.iplantc.de.analysis.client.theme;

import org.iplantc.de.analysis.client.views.cells.AnalysisAppNameCell;
import org.iplantc.de.client.models.analysis.Analysis;
import org.iplantc.de.resources.client.messages.I18N;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;

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

public class AnalysisAppNameCellDefaultAppearance implements AnalysisAppNameCell.AnalysisAppNameCellAppearance {

    public interface AnalysisAppNameCellStyles extends CssResource {

        String disabledApp();

        String enabledApp();

        String hasResultFolder();

        String noResultFolder();
    }

    public interface AnalysisAppNameCellResources extends ClientBundle {

        @Source("AnalysisAppNameCell.css")
        AnalysisAppNameCellStyles styles();
    }

    interface Templates extends SafeHtmlTemplates {

        @SafeHtmlTemplates.Template("<span name='{0}' title='{3}' class='{1}'>{2}</span>")
        SafeHtml cell(String elementName, String className, SafeHtml analysisAppName, String tooltip);
    }

    private final AnalysisAppNameCellResources resources;
    private final Templates template;
    private final IplantDisplayStrings displayStrings = I18N.DISPLAY;

    public AnalysisAppNameCellDefaultAppearance() {
        this(GWT.<AnalysisAppNameCellResources> create(AnalysisAppNameCellResources.class));
    }

    public AnalysisAppNameCellDefaultAppearance(AnalysisAppNameCellResources resources){
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

        final AnalysisAppNameCellStyles styles = resources.styles();
        String style = Strings.isNullOrEmpty(model.getResultFolderId()) ? styles.noResultFolder()
                               : styles.hasResultFolder() + " "
                                         + ((model.isAppDisabled()) ? styles.disabledApp() : styles.enabledApp());
        String tooltip = model.getAppName() + " ";
        if (model.isAppDisabled()) {
            tooltip = tooltip + displayStrings.appDisabled();
        } else {
            tooltip = tooltip + displayStrings.relaunchAnalysis();
        }
        sb.append(template.cell(ELEMENT_NAME, style, SafeHtmlUtils.fromString(model.getAppName()),
                                        tooltip));
    }
}
