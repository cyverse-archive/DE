package org.iplantc.de.theme.base.client.analyses.cells;

import org.iplantc.de.analysis.client.views.cells.AnalysisNameCell;
import org.iplantc.de.client.models.analysis.Analysis;
import org.iplantc.de.client.models.analysis.BatchStatus;
import org.iplantc.de.theme.base.client.analyses.AnalysesMessages;

import com.google.common.base.Strings;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

/**
 * @author jstroot
 */
public class AnalysisNameCellDefaultAppearance implements AnalysisNameCell.AnalysisNameCellAppearance {

    public interface AnalysisNameCellStyle extends CssResource {
        String htList();

        String hasResultFolder();

        String noResultFolder();
    }

    public interface AnalysisNameCellResources extends ClientBundle {
        @Source("AnalysisNameCell.css")
        AnalysisNameCellStyle getAnalysisNameStyleCss();

        @Source("htlist.png")
        ImageResource htList();
    }

    interface Templates extends SafeHtmlTemplates {
        @SafeHtmlTemplates.Template("<span name=\"{0}\" title=\" {3}\" class=\"{1}\">{2}</span>")
        SafeHtml analysis(String elementName, String className, SafeHtml analysisName, String tooltip);

        @SafeHtmlTemplates.Template("<span name='{5}' title='{6}' class=\"{4}\"></span>&nbsp;<span name=\"{0}\" title=\" {3}\" class=\"{1}\">{2}</span> ")
        SafeHtml htAnalysis(String elementName,
                            String className,
                            SafeHtml analysisName,
                            String tooltip,
                            String hticon,
                            String htElementName,
                            String batch_Status);
    }

    private final AnalysisNameCellResources resources;
    private final Templates template;
    private final AnalysesMessages analysesMessages;

    public AnalysisNameCellDefaultAppearance() {
        this(GWT.<AnalysisNameCellResources> create(AnalysisNameCellResources.class),
             GWT.<Templates> create(Templates.class),
             GWT.<AnalysesMessages> create(AnalysesMessages.class));
    }

    public AnalysisNameCellDefaultAppearance(final AnalysisNameCellResources resources,
                                             final Templates template,
                                             final AnalysesMessages analysesMessages){
        this.resources = resources;
        this.template = template;
        this.analysesMessages = analysesMessages;
        resources.getAnalysisNameStyleCss().ensureInjected();
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

        final AnalysisNameCellStyle nameStyles = resources.getAnalysisNameStyleCss();
        String style = Strings.isNullOrEmpty(model.getResultFolderId()) ? nameStyles.noResultFolder()
                               : nameStyles.hasResultFolder();
        String tooltip = analysesMessages.goToOutputFolder() + " of " + model.getName();
        if(model.isBatch()) {
            BatchStatus bs = model.getBatchStatus();
            StringBuilder httooltipSB = new StringBuilder("Click to see individual analysis status.");
            if (bs != null) {
                httooltipSB.append("Completed:" + bs.getCompleted() + ", ");
                httooltipSB.append("Running:" + bs.getRunning() + ", ");
                httooltipSB.append("Failed:" + bs.getFailed() + ", ");
                httooltipSB.append("Submitted:" + bs.getSubmitted() + ".");

            }
            sb.append(template.htAnalysis(ELEMENT_NAME,
                                          style,
                                          SafeHtmlUtils.fromString(model.getName()),
                                          tooltip,
                                          nameStyles.htList(),
                                          HT_ELEMENT_NAME,
                                          httooltipSB.toString()));
        } else {
            sb.append(template.analysis(ELEMENT_NAME, style, SafeHtmlUtils.fromString(model.getName()), tooltip));
        }

    }
}
