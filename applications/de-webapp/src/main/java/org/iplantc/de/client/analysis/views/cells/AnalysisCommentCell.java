package org.iplantc.de.client.analysis.views.cells;

import org.iplantc.de.client.models.analysis.Analysis;
import org.iplantc.de.commons.client.views.gxt3.dialogs.IPlantDialog;
import org.iplantc.de.resources.client.IplantResources;
import org.iplantc.de.resources.client.messages.I18N;

import static com.google.gwt.dom.client.BrowserEvents.CLICK;
import static com.google.gwt.dom.client.BrowserEvents.MOUSEOUT;
import static com.google.gwt.dom.client.BrowserEvents.MOUSEOVER;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.user.client.Event;

import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.form.TextArea;

public class AnalysisCommentCell extends AbstractCell<Analysis> {

    interface MyCss extends CssResource {
        @ClassName("comment_icon")
        String commentIcon();
    }

    interface Resource extends ClientBundle {
        @Source("AnalysisCommentCell.css")
        MyCss css();
    }

    /**
     * The HTML templates used to render the cell.
     */
    interface Templates extends SafeHtmlTemplates {
        @SafeHtmlTemplates.Template("<img name=\"{0}\" title=\"{1}\" class=\"{2}\" src=\"{3}\"></img>")
        SafeHtml imgCell(String name, String toolTip, String className, SafeUri imgSrc);
    }

    private static Templates templates = GWT.create(Templates.class);
    private static final Resource resources = GWT.create(Resource.class);

    public AnalysisCommentCell() {
        super(CLICK, MOUSEOVER, MOUSEOUT);
        resources.css().ensureInjected();
    }
    @Override
    public void render(com.google.gwt.cell.client.Cell.Context context, Analysis value, SafeHtmlBuilder sb) {
        sb.append(templates.imgCell(I18N.DISPLAY.comments(), I18N.DISPLAY.comments(), resources.css().commentIcon(), IplantResources.RESOURCES
                .userComment().getSafeUri()));

    }

    @Override
    public void onBrowserEvent(Cell.Context context, Element parent, Analysis value, NativeEvent event, ValueUpdater<Analysis> valueUpdater) {
        if (value == null) {
            return;
        }

        Element eventTarget = Element.as(event.getEventTarget());
        if (parent.isOrHasChild(eventTarget)) {

            switch (Event.as(event).getTypeInt()) {
                case Event.ONCLICK:
                    doOnClick(eventTarget, value, valueUpdater);
                    break;
                default:
                    break;
            }
        }
    }

    private void doOnClick(Element eventTarget, Analysis value, ValueUpdater<Analysis> valueUpdater) {
        Dialog d = new IPlantDialog();
        d.setHeadingText(I18N.DISPLAY.comments());
        d.setSize("350px", "300px");
        TextArea ta = new TextArea();
        ta.setSize("300px", "200px");
        ta.setValue(value.getDescription());
        d.add(ta);
        d.show();
    }


}
